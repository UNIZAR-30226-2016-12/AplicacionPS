package redwinecorp.misvinos;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Asier on 18/04/2016.
 */
public class VerVino extends AppCompatActivity {

    public static final String ID = "id";

    private TextView nombre;
    private TextView tipo;
    private TextView uva;
    private TextView denominacion;
    private TextView year;
    private TextView localizacion;
    private TextView premios;
    private RatingBar valoracion;
    private TextView nota;

    private Long id;

    private VinosDbAdapter mDbHelper;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.activity_ver_vino);
        setTitle("Ver Vino");

        nombre = (TextView) findViewById(R.id.nomVino);
        tipo = (TextView) findViewById(R.id.vino);
        uva = (TextView) findViewById(R.id.uva);
        denominacion = (TextView) findViewById(R.id.denominacion);
        year = (TextView) findViewById(R.id.anno);
        localizacion = (TextView) findViewById(R.id.localizacion);
        premios = (TextView) findViewById(R.id.premios);
        valoracion = (RatingBar) findViewById(R.id.ratingBar);
        nota = (TextView) findViewById(R.id.notas);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        id = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ID);
        if (id == null) {
            Bundle extras = getIntent().getExtras();
            id = (extras != null) ? extras.getLong(ID)
                    : null;
        }
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

    private void populateFields() {
        Cursor cV = mDbHelper.getVino(id.longValue());
        Cursor cD = mDbHelper.getDenominacion(id.longValue());
        Cursor cT = mDbHelper.getTipo(id.longValue());

        cV.moveToFirst();
        cD.moveToFirst();
        cT.moveToFirst();

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
        valoracion.setRating(cV.getFloat(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_VALORACION))/2);
        nota.setText(cV.getString(cV.getColumnIndex(VinosDbAdapter.KEY_VINO_NOTA)));

        //Dado un cursor con las uvas y los porcentajes, se convierte en un String("u1-p1, u2-p2...)
        uva.setText(tratarUvas(mDbHelper.getUvas(id.longValue())));
        //Dado un cursor con los premio y los años, se convierte en un String("p1-a1, p2-a2...)
        premios.setText(tratarPremios(mDbHelper.getPremios(id.longValue())));
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
}