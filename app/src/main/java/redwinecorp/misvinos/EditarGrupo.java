package redwinecorp.misvinos;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditarGrupo extends AppCompatActivity {


    public static final String ID = "id";

    private EditText nombre;

    private Long grupo;

    private VinosDbAdapter mDbHelper;

    @Override
    /**
     * *     metodo constructor de la clase
     **/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.group_edit);

        nombre = (EditText) findViewById(R.id.nomGrupo);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button confirmButton = (Button) findViewById(R.id.confirmar);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        grupo = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ID);
        if (grupo == null) {
            Bundle extras = getIntent().getExtras();
            grupo = (extras != null) ? extras.getLong(ID)
                    : null;
        }

        if(grupo==null) {
            setTitle("Crear Grupo");
        }
        else{
            setTitle("Editar Grupo");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * *     metodo que se encarga de añadir a la base de datos los atributos de un grupo que el usuario
     *      ha introducido anteriormente
     **/
    private void populateFields() {
        if (grupo != null) {
            Cursor cG = mDbHelper.getGrupo(grupo.longValue());
            cG.moveToFirst();
            nombre.setText(cG.getString(cG.getColumnIndex(VinosDbAdapter.KEY_GRUPO_NOMBRE)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(ID, grupo);
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
        if(grupo!=null) {
            mDbHelper.actualizarGrupo(grupo.longValue(),nombre.getText().toString());
        }
        else{
            mDbHelper.crearGrupo(nombre.getText().toString());
        }
    }

}
