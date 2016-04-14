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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class EditarVino extends AppCompatActivity {

    public static final String ID = "id";

    private EditText nombre;
    private EditText tipo;
    private EditText uva;
    private EditText denominacion;
    private EditText year;
    private EditText localizacion;
    private EditText premios;
    private EditText valoracion;
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
        valoracion = (EditText) findViewById(R.id.valoracion);
        nota = (EditText) findViewById(R.id.notas);

        Button confirmButton = (Button) findViewById(R.id.confirmar);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

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
            Cursor vino = mDbHelper.getInfoVino(id);
            startManagingCursor(vino);
            nombre.setText(vino.getString(
                    vino.getColumnIndexOrThrow(VinosDbAdapter.KEY_VINO_NOMBRE)));
            tipo.setText(vino.getString(
                    vino.getColumnIndexOrThrow(VinosDbAdapter.KEY_ES_TIPO)));
            denominacion.setText(vino.getString(
                    vino.getColumnIndexOrThrow(VinosDbAdapter.KEY_POSEE_DENOMINACION)));
            year.setText(vino.getString(
                    vino.getColumnIndexOrThrow(VinosDbAdapter.KEY_VINO_AÑO)));
            localizacion.setText(vino.getString(
                    vino.getColumnIndexOrThrow(VinosDbAdapter.KEY_VINO_POSICION)));
            valoracion.setText(vino.getString(
                    vino.getColumnIndexOrThrow(VinosDbAdapter.KEY_VINO_VALORACION)));
            nota.setText(vino.getString(
                    vino.getColumnIndexOrThrow(VinosDbAdapter.KEY_VINO_NOTA)));

            //Dado un cursor con las uvas y los porcentajes, se convierte en un String("u1-p1, u2-p2...)
            premios.setText(tratarPremios(mDbHelper.getInfoPremios(id)));
            //Dado un cursor con los premio y los años, se convierte en un String("p1-a1, p2-a2...)
            premios.setText(tratarPremios(mDbHelper.getInfoUvas(id)));
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

        String no = nombre.getText().toString();
        String t = tipo.getText().toString();
        String d = denominacion.getText().toString();
        String nt = nota.getText().toString();

        try {
            Long p = Long.parseLong(localizacion.getText().toString());
        }
        catch (NumberFormatException e){
            //
        }
        try {
            Long a = Long.parseLong(year.getText().toString());
        }
        catch (NumberFormatException e){
            //
        }
        try {
            Long v = Long.parseLong(valoracion.getText().toString());
        }
        catch (NumberFormatException e){
            //
        }


        premios.getText().toString();
        uva.getText().toString();

        if (id == null) {
            long id = mDbHelper.crearVino(nombre,localizacion,);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body,categoria);
        }
    }


}
