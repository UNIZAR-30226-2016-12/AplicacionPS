package redwinecorp.misvinos;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.util.Log;

public class EditarVino extends AppCompatActivity {

    public static final String ID = "id";

    private EditText nombre;
    private EditText tipo;
    private EditText uva;
    private EditText denominacion;
    private EditText year;
    private EditText localizacion;
    private EditText premios;
    private RatingBar valoracion;
    private EditText nota;

    private Long id;

    private VinosDbAdapter mDbHelper;

    @Override
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

        Button confirmButton = (Button) findViewById(R.id.confirmar);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
/**
        valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                valoracion.setRating(rating);

            }
        });
*/
        id = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ID);
        if (id == null) {
            Bundle extras = getIntent().getExtras();
            id = (extras != null) ? extras.getLong(ID)
                    : null;
        }
    }

    private void populateFields() {
        if (id != null) {
            Cursor cV = mDbHelper.getVino(id.longValue());
            Cursor cD = mDbHelper.getDenominacion(id.longValue());
            Cursor cT = mDbHelper.getTipo(id.longValue());

            cV.moveToFirst();
            cD.moveToFirst();
            cT.moveToFirst();

            nombre.setText(cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_NOMBRE)));
            tipo.setText(cT.getString(cT.getColumnIndex(VinosDbAdapter.KEY_ES_TIPO)));
            denominacion.setText(cD.getString(cD.getColumnIndex(VinosDbAdapter.KEY_POSEE_DENOMINACION)));
            year.setText(cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_AÑO)));
            localizacion.setText(cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_POSICION)));
            valoracion.setRating(cV.getFloat(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_VALORACION)));
            nota.setText(cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_NOTA)));

            //Dado un cursor con las uvas y los porcentajes, se convierte en un String("u1-p1, u2-p2...)
            uva.setText(tratarUvas(mDbHelper.getUvas(id.longValue())));
            //Dado un cursor con los premio y los años, se convierte en un String("p1-a1, p2-a2...)
            premios.setText(tratarPremios(mDbHelper.getPremios(id.longValue())));
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
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        if(id!=null) {
            Cursor cV = mDbHelper.getVino(id.longValue());
            Cursor cU = mDbHelper.getUvas(id.longValue());
            Cursor cP = mDbHelper.getPremios(id.longValue());
            Cursor cD = mDbHelper.getDenominacion(id.longValue());
            Cursor cT = mDbHelper.getTipo(id.longValue());

            cV.moveToFirst();
            cU.moveToFirst();
            cP.moveToFirst();
            cD.moveToFirst();
            cT.moveToFirst();

            boolean valido = true;

            String no = nombre.getText().toString();
            String nt = nota.getText().toString();
            Long p=null,a=null,v=null;
            try {
                p = Long.parseLong(localizacion.getText().toString());
                a = Long.parseLong(year.getText().toString());
                v = new Long(Math.round(valoracion.getRating()));
                System.out.println(v);
            } catch (NumberFormatException e) {
                valido=false;
            }
            if (valido) {
                mDbHelper.actualizarVino(id,no,a,p,v,nt);
            }

            //Cambiamos la denominacion
            String nd = denominacion.getText().toString();
            String d = cD.getString(cD.getColumnIndex(VinosDbAdapter.KEY_POSEE_DENOMINACION));
            if(!mDbHelper.cambiarDenominacion(id,d,nd)){
                mDbHelper.crearDenominacion(nd);
                mDbHelper.cambiarDenominacion(id, d, nd);
            }

            //Cambiamos el tipo
            String ntp = tipo.getText().toString();
            String tp = cT.getString(cT.getColumnIndex(VinosDbAdapter.KEY_ES_TIPO));
            if(!mDbHelper.cambiarTipo(id,tp,ntp)){
                mDbHelper.crearTipo(ntp);
                mDbHelper.cambiarTipo(id,tp,ntp);
            }

            cambiarUvas(id,uva.getText().toString());
            cambiarPremios(id,premios.getText().toString());
        }
        else{
            boolean valido = true;
            long idVino;

            String no = nombre.getText().toString();
            String nt = nota.getText().toString();
            Long p=null,a=null,v=null;
            try {
                p = Long.parseLong(localizacion.getText().toString());
                a = Long.parseLong(year.getText().toString());
                v = new Long(Math.round(valoracion.getRating()));
                System.out.println(v);
            } catch (NumberFormatException e) {
                valido=false;
            }
            if (valido) {
                idVino = mDbHelper.crearVino(no,p,a,v,nt);

                //Creamos la denominacion y la asignamos
                String d = denominacion.getText().toString();
                mDbHelper.crearDenominacion(d);
                mDbHelper.añadirDenominacion(d,idVino);

                //Creamoos el tipo y lo asignamos
                String t = tipo.getText().toString();
                mDbHelper.crearTipo(t);
                mDbHelper.añadirTipo(t, idVino);

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

                devolver += nombreUva + "-" + porcentaje + ", ";
            }while(c.moveToNext());
        }
        if(!devolver.equalsIgnoreCase("")){
            devolver = devolver.substring(0,devolver.length() - 2);
        }
        System.out.println(devolver);
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

        String auxNuevas = nuevas.replace(" ", "");
        String[] elementosNuevas = auxNuevas.split(",");

        //Añadimos las nuevas
        for (int i=0 ; i<elementosNuevas.length ; i++) {
            mDbHelper.crearUva(elementosNuevas[i].split("-")[0]);
            mDbHelper.añadirUva(elementosNuevas[i].split("-")[0],
                    Double.parseDouble(elementosNuevas[i].split("-")[1]), id);
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

                devolver += nombrePremio + "-" + anno + ", ";
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

        //Añadimos los nuevos
        for (int i=0 ; i<elementosNuevos.length ; i++){
            mDbHelper.crearPremio(elementosNuevos[i].split("-")[0]);
            mDbHelper.añadirPremio(elementosNuevos[i].split("-")[0],
                    Long.parseLong(elementosNuevos[i].split("-")[1]), id);
        }
    }
}
