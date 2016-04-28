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


    public static final String NOMBRE = "nombre";

    private EditText nombre;

    private String nom;

    private VinosDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.wine_edit);

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

        nom = (savedInstanceState == null) ? null :
                (String) savedInstanceState.getSerializable(NOMBRE);
        if (nom == null) {
            Bundle extras = getIntent().getExtras();
            nom = (extras != null) ? extras.getString(NOMBRE)
                    : null;
        }

        if(nom==null) {
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
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateFields() {
        if (nom != null) {
            nombre.setText(nom);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NOMBRE, nom);
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
        if(nom!=null) {
            mDbHelper.actualizarGrupo(nom,nombre.getText().toString());
        }
        else{
            mDbHelper.crearGrupo(nombre.getText().toString());
        }
    }

}
