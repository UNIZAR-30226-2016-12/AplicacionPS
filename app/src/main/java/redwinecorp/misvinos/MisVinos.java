package redwinecorp.misvinos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import redwinecorp.misvinos.VinosDbAdapter;


public class MisVinos extends AppCompatActivity {

    //Cadenas para crear el intent de esta actividad
    public static final String MOSTRAR_VINOS = "vinos";

    //Opciones del menu de los vinos
    private static final int AÑADIR_VINO = Menu.FIRST;
    private static final int ORDENAR_VINOS = Menu.FIRST+1;

    //Opciones del menu de los grupos

    //Opciones del vino
    private static final int EDITAR_VINO = Menu.FIRST;
    private static final int BORRAR_VINO = Menu.FIRST+1;

    //Para saber que hay que mostrar(true->vinos , false->grupos)
    private Boolean modoVinos;

    //Base de datos
    private VinosDbAdapter mDbHelper;

    private Menu menu;
    private Cursor mVinosCursor;
    private ListView mList;


    /** Constructor de la clase */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coleccion_list);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        modoVinos = (savedInstanceState == null) ? null :
                (Boolean) savedInstanceState.getSerializable(MOSTRAR_VINOS);
        if (modoVinos == null) {
            Bundle extras = getIntent().getExtras();
            modoVinos = (extras != null) ? extras.getBoolean(MOSTRAR_VINOS) : null;
        }

        mList = (ListView) findViewById(R.id.list);
        fillData();

        //Comportamiento al pulsar en un vino o grupo
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, int id) {
                if(modoVinos){
                    editarVino(id);
                }
                else{
                    editarGrupo(id);
                }
            }
        });

        registerForContextMenu(mList);
    }


    private void fillData() {
        if(modoVinos){
            Cursor cVinos;

            cVinos = mDbHelper.obtenerVinos();

            // Obtenemos los vinos de la base de datos
            startManagingCursor(cVinos);

            // Creamos un array de los campos que vamos a mostrar
            String[] from = new String[] { VinosDbAdapter.KEY_VINO_NOMBRE, VinosDbAdapter.KEY_VINO_AÑO};

            // Creamos un array de los campos a los que los vamos a asignar
            int[] to = new int[] { R.id.???};

            // Creamos un array adapter y lo preparamos para mostrar los datos
            SimpleCursorAdapter vinos =
                    new SimpleCursorAdapter(this, R.layout.notes_row, cVinos, from, to);

            mList.setAdapter(vinos);
        } else{
            //
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        boolean resultado = super.onCreateOptionsMenu(m);
        if(modoVinos) {
            m.add(Menu.NONE, AÑADIR_VINO, Menu.NONE, R.string.menu_insert);
            m.add(Menu.NONE, ORDENAR_VINOS, Menu.NONE, "Ordenar");
        }
        else{
            //
        }
        menu = m;
        return resultado;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(modoVinos){
            switch (item.getItemId()){
                case AÑADIR_VINO:
                    añadirVino();
                    return true;
                case ORDENAR_VINOS:
                    ordenarVinos();
                    return true;
            }
        }
        else{
            //
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu m, View v,
                                    ContextMenu.ContextMenuInfo mInfo) {
        super.onCreateContextMenu(m, v, mInfo);
        if(modoVinos){
            m.add(Menu.NONE, EDITAR_VINO, Menu.NONE, "Editar vino");
            m.add(Menu.NONE, BORRAR_VINO, Menu.NONE, "Borrar vino");
        } else{
            //
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(modoVinos){
            switch (item.getItemId()) {
                case EDITAR_VINO:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    editarVino(info);
                    fillData();
                    return true;
                case BORRAR_VINO:
                    borrarVino(info);
                    return true;
            }
        }
        else{
            //
        }
        return super.onContextItemSelected(item);
    }

    private void añadirVino(){

    }

    private void editarVino(AdapterView.AdapterContextMenuInfo info){

    }

    private void borrarVino(AdapterView.AdapterContextMenuInfo info){
        //Obtener nombre y año del vino
        String nombre = "";
        long año = 0;
        mDbHelper.borrarVino(nombre,año);
    }

    private void ordenarVinos(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
