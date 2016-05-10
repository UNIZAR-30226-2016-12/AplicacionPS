package redwinecorp.misvinos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditarVino extends AppCompatActivity {

    public static final String ID = "id";
    public static final String ELIMINAR = "eliminar";
    public static final String ASIGNAR = "asignar";

    private EditText nombre;
    private EditText tipo;
    private EditText uva;
    private EditText denominacion;
    private EditText year;
    private EditText localizacion;
    private EditText premios;
    private RatingBar valoracion;
    private EditText nota;
    private EditText grupo;

    private Long id;

    private VinosDbAdapter mDbHelper;

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Button btnSelect;
    ImageView ivImage;

    @Override
    /**
     * *     metodo constructor de la clase
     **/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.wine_edit);
        setTitle("EDITAR VINO");

        nombre = (EditText) findViewById(R.id.nomVino);
        tipo = (EditText) findViewById(R.id.vino);
        uva = (EditText) findViewById(R.id.uva);
        denominacion = (EditText) findViewById(R.id.denominacion);
        year = (EditText) findViewById(R.id.anno);
        localizacion = (EditText) findViewById(R.id.localizacion);
        premios = (EditText) findViewById(R.id.premios);
        valoracion = (RatingBar) findViewById(R.id.ratingBar);
        nota = (EditText) findViewById(R.id.notas);
        grupo = (EditText) findViewById(R.id.grupos);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button confirmButton = (Button) findViewById(R.id.confirmar);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (comprobarEntradas()){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        //Boton para ir a MisGrupos para quitar uno.
        Button quitarGrupoButtom = (Button) findViewById(R.id.quitar_grupo);
        quitarGrupoButtom.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(id==null){
                    grupo.setText("");
                    grupo.setHintTextColor(Color.rgb(255, 0, 0));
                    grupo.setHint("Antes de quitar un grupo debes crear el vino.");
                }
                else {
                    quitarGrupo();
                }
            }

        });

        //Boton para ir a MisGrupos para añadir uno.
        Button añadirGrupoButtom = (Button) findViewById(R.id.añadir_grupo);
        añadirGrupoButtom.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(id==null){
                    grupo.setText("");
                    grupo.setHintTextColor(Color.rgb(255, 0, 0));
                    grupo.setHint("Antes de añadir un grupo debes crear el vino.");
                }
                else {
                    añadirGrupo();
                }
            }

        });

        id = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ID);
        if (id == null) {
            Bundle extras = getIntent().getExtras();
            id = (extras != null) ? extras.getLong(ID)
                    : null;
        }

        if(id==null) {
            setTitle("Crear Vino");
        }
        else{
            setTitle("Editar Vino");
        }

        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);
    }
    /**
     * *     metodo encargado de seleccionar una foto para ponerla en un vino
     **/
    private void selectImage() {
        final CharSequence[] items = { "Cámara", "Elegir de la galería",
                "Cancelar" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditarVino.this);
        builder.setTitle("Elige una foto!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    /**
     * *     metodo encargado de comprimir la imagen que se va a colocar como atributo de un vino
     **/
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ivImage.setImageBitmap(bm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * *     metodo que se encarga de añadir a la base de datos los atributos de un vino que el usuario
     *      ha introducido anteriormente
     **/
    private void populateFields() {
        if (id != null) {
            Cursor cV = mDbHelper.getVino(id);
            Cursor cD = mDbHelper.getDenominacion(id);
            Cursor cT = mDbHelper.getTipo(id);
            Cursor cG = mDbHelper.obtenerGruposOrdenadosVino(id,10);

            cV.moveToFirst();
            cD.moveToFirst();
            cT.moveToFirst();
            cG.moveToFirst();

            nombre.setText(cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_NOMBRE)));
            if(cT.getCount()>0) {
                tipo.setText(cT.getString(cT.getColumnIndex(VinosDbAdapter.KEY_ES_TIPO)));
            }
            else{
                tipo.setText("");
            }
            if(cD.getCount()>0) {
                denominacion.setText(cD.getString(cD.getColumnIndex(VinosDbAdapter.KEY_POSEE_DENOMINACION)));
            }
            else{
                denominacion.setText("");
            }
            String a = cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_AÑO));
            if(a.equals("-1")){
                year.setText("");
            }else{
                year.setText(a);
            }
            String p = cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_POSICION));
            if(p.equals("-1")){
                localizacion.setText("");
            }else{
                localizacion.setText(p);
            }
            valoracion.setRating(cV.getFloat(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_VALORACION)) / 2.0f);
            nota.setText(cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_NOTA)));

            //Dado un cursor con las uvas y los porcentajes, se convierte en un String("u1-p1, u2-p2...)
            uva.setText(tratarUvas(mDbHelper.getUvas(id)));
            //Dado un cursor con los premio y los años, se convierte en un String("p1-a1, p2-a2...)
            premios.setText(tratarPremios(mDbHelper.getPremios(id)));

            if(cG.getCount()==0) grupo.setText("");
            if(cG.getCount()>0) {
                String grupos = cG.getString(cG.getColumnIndex(VinosDbAdapter.KEY_GRUPO_NOMBRE));
                cG.moveToNext();
                for(int i=1 ; i<cG.getCount() ; i++){
                    grupos = grupos + "," + cG.getString(cG.getColumnIndex(VinosDbAdapter.KEY_GRUPO_NOMBRE));
                    cG.moveToNext();
                }
                grupo.setText(grupos);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(ID, id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(comprobarEntradas()) {
            saveState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        if(id!=null) {
            Cursor cV = mDbHelper.getVino(id);
            Cursor cU = mDbHelper.getUvas(id);
            Cursor cP = mDbHelper.getPremios(id);
            Cursor cD = mDbHelper.getDenominacion(id);
            Cursor cT = mDbHelper.getTipo(id);

            cV.moveToFirst();
            cU.moveToFirst();
            cP.moveToFirst();
            cD.moveToFirst();
            cT.moveToFirst();

            if(comprobarEntradas()) {

                String p = localizacion.getText().toString();
                long pos = -1;
                if(!(p==null || p.equals(""))) {
                    try {
                        pos = Long.parseLong(p);
                    } catch (NumberFormatException e) {
                        pos=-1;
                    }
                }

                String a = year.getText().toString();
                long año = -1;
                if(!(a==null || a.equals(""))) {
                    try {
                        año = Long.parseLong(a);
                    } catch (NumberFormatException e) {
                        año=-1;
                    }
                }

                mDbHelper.actualizarVino(id, nombre.getText().toString(),
                        año, pos, (long) (valoracion.getRating()*2.0),
                        nota.getText().toString());


                //Cambiamos la denominacion
                String nd = denominacion.getText().toString();
                if(cD.getCount()>0) {
                    String d = cD.getString(cD.getColumnIndex(VinosDbAdapter.KEY_POSEE_DENOMINACION));
                    if (!mDbHelper.cambiarDenominacion(id, d, nd)) {
                        mDbHelper.crearDenominacion(nd);
                        mDbHelper.cambiarDenominacion(id, d, nd);
                    }
                } else{
                    mDbHelper.crearDenominacion(nd);
                    mDbHelper.añadirDenominacion(nd, id);
                }

                //Cambiamos el tipo
                String ntp = tipo.getText().toString();
                if(cT.getCount()>0) {
                    String tp = cT.getString(cT.getColumnIndex(VinosDbAdapter.KEY_ES_TIPO));
                    if (!mDbHelper.cambiarTipo(id, tp, ntp)) {
                        mDbHelper.crearTipo(ntp);
                        mDbHelper.cambiarTipo(id, tp, ntp);
                    }
                } else{
                    mDbHelper.crearTipo(ntp);
                    mDbHelper.añadirTipo(ntp,id);
                }

                cambiarUvas(id, uva.getText().toString());
                cambiarPremios(id, premios.getText().toString());
            }
        }
        else{
            long idVino;

            if (comprobarEntradas()) {
                String p = localizacion.getText().toString();
                long pos = -1;
                if(!(p==null || p.equals(""))) {
                    try {
                        pos = Long.parseLong(p);
                    } catch (NumberFormatException e) {
                        pos=-1;
                    }
                }

                String a = year.getText().toString();
                long año = -1;
                if(!(a==null || a.equals(""))) {
                    try {
                        año = Long.parseLong(a);
                    } catch (NumberFormatException e) {
                        año=-1;
                    }
                }

                idVino = mDbHelper.crearVino(nombre.getText().toString(),
                        pos, año, (long) (valoracion.getRating()*2.0),
                        nota.getText().toString());

                //Creamos la denominacion y la asignamos
                String d = denominacion.getText().toString();
                if(!(d==null || d.equals(""))) {
                    mDbHelper.crearDenominacion(d);
                    mDbHelper.añadirDenominacion(d, idVino);
                }

                //Creamoos el tipo y lo asignamos
                String t = tipo.getText().toString();
                if(!(t==null || t.equals(""))) {
                    mDbHelper.crearTipo(t);
                    mDbHelper.añadirTipo(t, idVino);
                }

                //Creamos las uvas y las asignamos
                cambiarUvas(idVino,uva.getText().toString());

                //Creamos los premios y los asignamos
                cambiarPremios(idVino,premios.getText().toString());
            }
        }
    }

    /* Transforma el cursor con los nombres de las uvas y los porcentajes en un String:
     * "uva1-porcentaje1, uva2-porcentaje2, uva3-porcentaje3..."
     */
    private String tratarUvas(Cursor c){
        String devolver = "";
        if (c.moveToFirst()){
            do{
                String nombreUva = c.getString(c.getColumnIndex(mDbHelper.KEY_COMPUESTO_UVA));
                Double porcentaje = Double.parseDouble(c.getString(c.getColumnIndex(mDbHelper.KEY_COMPUESTO_PORCENTAJE)));

                devolver = devolver + nombreUva + "-" + porcentaje + ", ";
            }while(c.moveToNext());
        }
        if(!devolver.equalsIgnoreCase("")){
            devolver = devolver.substring(0,devolver.length() - 2);
        }
        return devolver;
    }

    /* Dado un String con la forma
     * "uva1-porcentaje1, uva2-porcentaje2, uva3-porcentaje3..."
     * Cambia las uvas y los porcentajes(tabla compuesto de la BD) del vino(id) por las del String
     * Si no tiene ninguna asignada se le asignan todas las nuevas
     */
    private void cambiarUvas(long id,String nuevas){
        Cursor c = mDbHelper.getUvas(id);

        //Si habia uvas antes las borramos
        if (c.moveToFirst()){
            String anteriores = tratarUvas(c);
            String auxAnteriores = anteriores.replace(" ","");
            String[] elementosAnteriores = auxAnteriores.split(",");

            for (int i=0 ; i < elementosAnteriores.length ; i++) {
                mDbHelper.borrarCompuesto(id,elementosAnteriores[i].split("-")[0]);
            }
        }
        if(nuevas!=null && !nuevas.equals("")) {
            String auxNuevas = nuevas.replace(" ", "");
            String[] elementosNuevas = auxNuevas.split(",");

            //Añadimos las nuevas
            for (int i = 0; i < elementosNuevas.length; i++) {
                mDbHelper.crearUva(elementosNuevas[i].split("-")[0]);
                mDbHelper.añadirUva(elementosNuevas[i].split("-")[0],
                        Double.parseDouble(elementosNuevas[i].split("-")[1]), id);
            }
        }
    }

    /* Transforma el cursor con los nombres de los premios y los años en un String:
     * "premio1-año1, premio2-año2, premio3-año3..."
     */
    private String tratarPremios(Cursor c){
        String devolver = "";
        if (c.moveToFirst()){
            do{
                String nombrePremio = c.getString(c.getColumnIndex(mDbHelper.KEY_GANA_PREMIO));
                Long anno = Long.parseLong(c.getString(c.getColumnIndex(mDbHelper.KEY_GANA_AÑO)));

                devolver = devolver + nombrePremio + "-" + anno + ", ";
            }while(c.moveToNext());
        }
        if(!devolver.equalsIgnoreCase("")){
            devolver = devolver.substring(0,devolver.length() - 2);
        }
        return devolver;
    }

    /* Dado un String con la forma
     * "premio1-año1, premio2-año2, premio3-año3..."
     * Cambia los premios y los años(tabla gana de la BD) del vino(id) por los del String
     * Si no tiene ninguno asignado se le asignan todos los nuevos
     */
    private void cambiarPremios(long id,String nuevos){
        Cursor c = mDbHelper.getPremios(id);

        //Si habia alguno anteriormente lo borramos
        if (c.moveToFirst()){
            String anteriores = tratarPremios(c);
            String auxAnteriores = anteriores.replace(" ", "");
            String[] elementosAnteriores = auxAnteriores.split(",");

            for (int i=0 ; i < elementosAnteriores.length ; i++) {
                mDbHelper.borrarGana(id, elementosAnteriores[i].split("-")[0],
                        Long.parseLong(elementosAnteriores[i].split("-")[1]));
            }
        }

        String auxNuevas = nuevos.replace(" ", "");
        String[] elementosNuevos = auxNuevas.split(",");

        if(nuevos!=null && !nuevos.equals("")) {
            //Añadimos los nuevos
            for (int i = 0; i < elementosNuevos.length; i++) {
                mDbHelper.crearPremio(elementosNuevos[i].split("-")[0]);
                mDbHelper.añadirPremio(elementosNuevos[i].split("-")[0],
                        Long.parseLong(elementosNuevos[i].split("-")[1]), id);
            }
        }
    }
    /**
     * *     metodo encargado de analizar que son correctos los datos introducidos por el usuario
     **/
    private boolean comprobarEntradas(){
        boolean correcto = true;

        String no = nombre.getText().toString();
        if(no==null || no.equals("")){
            //Mostrar error(nombre no puede ser null)
            nombre.setText("");
            nombre.setHintTextColor(Color.rgb(255,0,0));
            nombre.setHint("El vino tiene que tener un nombre.");
            correcto=false;
        }
        String p=localizacion.getText().toString();
        if(p!=null && !p.equals("")) {
            try {
                Long.parseLong(p);
            } catch (NumberFormatException e) {
                localizacion.setText("");
                localizacion.setHintTextColor(Color.rgb(255,0,0));
                localizacion.setHint("La localizacion tiene que ser un entero.");
                correcto = false;
            }
        }
        String a=year.getText().toString();
        if(a!=null && !a.equals("")) {
            try {
                Long.parseLong(a);
            } catch (NumberFormatException e) {
                year.setText("");
                year.setHintTextColor(Color.rgb(255,0,0));
                year.setHint("El año tiene que ser un entero.");
                correcto = false;
            }
        }

        boolean correctoU = comprobarUvas(uva.getText().toString());
        if (!correctoU) {
            uva.setText("");
            uva.setHintTextColor(Color.rgb(255, 0, 0));
            uva.setHint("Las uvas no tienen el formato correcto.");
            correcto=false;
        }
        boolean correctoP = comprobarPremios(premios.getText().toString());
        if(!correctoP){
            premios.setText("");
            premios.setHintTextColor(Color.rgb(255,0,0));
            premios.setHint("Los premios no tienen el formato correcto.");
            correcto=false;
        }

        return correcto;
    }
    /**
     * *     metodo encargado de comprobar que el formato de las uvas introducidas son correctas
     **/
    private static boolean comprobarUvas(String s){
        if(s==null || s.equals("")){
            return true;
        }
        s = s.replace(" ", "");
        Pattern mPattern = Pattern.compile("^(([a-zA-Z]|[0-9])+[-]([0-9])+[.]([0-9])+[,])*" +
                "([a-zA-Z]|[0-9])+[-]([0-9])+[.]([0-9])+$");

        Matcher matcher = mPattern.matcher(s);
        return matcher.matches();
    }
    /**
     * *     metodo encargado de comprobar que el formato de los premios introducidas son correctas
     **/
    private static boolean comprobarPremios(String s){
        if(s==null || s.equals("")){
            return true;
        }
        s = s.replace(" ", "");
        Pattern mPattern = Pattern.compile("^(([a-zA-Z]|[0-9])+[-]([0-9])+[,])*" +
                                        "([a-zA-Z]|[0-9])+[-]([0-9])+$");

        Matcher matcher = mPattern.matcher(s);
        return matcher.matches();
    }
    /**
     * *     metodo encargado de añadir un grupo a un vino
     **/
    private void añadirGrupo(){
        Intent i = new Intent(this, MisGrupos.class);
        i.putExtra(MisGrupos.INICIO, new Boolean(false));
        startActivityForResult(i, 2);
    }
    /**
     * *     metodo encargado de quitar un grupo de un vino
     **/
    private void quitarGrupo(){
        Intent i = new Intent(this, MisGrupos.class);
        i.putExtra(MisGrupos.ID_VINO, id);
        i.putExtra(MisGrupos.VER, new Boolean(false));
        startActivityForResult(i, 2);
    }
    /**
     * *     metodo encargado de mostrar las pantallas actualizadas despues de realizar cualquier actividad
     **/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
        if (resultCode == RESULT_OK) {
            Long idGrupoA = data.getLongExtra(ASIGNAR, -1);
            if (idGrupoA.longValue() != -1) {
                mDbHelper.añadirGrupo(idGrupoA.longValue(), id);
            }
            Long idGrupoE = data.getLongExtra(ELIMINAR, -1);
            if (idGrupoE.longValue() != -1) {
                mDbHelper.borrarPertenece(id, idGrupoE.longValue());
            }
            populateFields();
        }
    }
}
