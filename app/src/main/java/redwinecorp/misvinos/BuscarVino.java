package redwinecorp.misvinos;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class BuscarVino extends AppCompatActivity {

    private Spinner desplegable;
    private VinosDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.activity_buscar_vino);

        desplegable = (Spinner) findViewById(R.id.spinner);

        String[] arraySpinner = new String[] {
                mDbHelper.KEY_VINO_ID,mDbHelper.KEY_VINO_AÑO,mDbHelper.KEY_VINO_NOMBRE,
                mDbHelper.KEY_VINO_NOTA,mDbHelper.KEY_VINO_POSICION,mDbHelper.KEY_VINO_VALORACION
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        desplegable.setAdapter(adapter);
    }

}
