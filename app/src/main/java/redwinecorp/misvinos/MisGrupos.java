package redwinecorp.misvinos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MisGrupos extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    //Cadenas para crear el intent de esta actividad
    public static final String ID_VINO = "id"; //id-> muestra los grupos de ese vino
                                               //null-> muestra todos los grupos

    //Opciones del menu de los vinos
    private static final int AÑADIR_GRUPO = Menu.FIRST;

    //Opciones del vino
    private static final int EDITAR_GRUPO = Menu.FIRST;
    private static final int BORRAR_GRUPO = Menu.FIRST+1;

    //Para saber que hay que mostrar(null->todos , !null->los del vino)
    private Long idVino;

    //Base de datos
    private VinosDbAdapter mDbHelper;

    private Menu menu;
    private Cursor mGruposCursor;
    private ListView mList;

    private TextView numGrupos;


    /** Constructor de la clase */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_list);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        numGrupos = (TextView) findViewById(R.id.numGrupos);
        numGrupos.setFocusable(false);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        idVino = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ID_VINO);
        if (idVino == null) {
            Bundle extras = getIntent().getExtras();
            idVino = (extras != null) ? extras.getLong(ID_VINO)
                    : null;
        }

        mList = (ListView) findViewById(R.id.listG);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                if(idVino!=null){
                    // Devolver a la actividad invocante el id del grupo seleccionado
                    devolver();
                }
            }
        });

        fillData();

        registerForContextMenu(mList);
    }


    private void fillData() {

        Cursor cGrupos;
        if(idVino==null) {
            cGrupos = mDbHelper.getGrupos();
        }
        else{
            cGrupos = mDbHelper.getGrupos(idVino.longValue());
        }

        startManagingCursor(cGrupos);

        // Creamos un array de los campos que vamos a mostrar
        String[] from = new String[] { VinosDbAdapter.KEY_GRUPO_NOMBRE };

        // Creamos un array de los campos a los que los vamos a asignar
        int[] to = new int[] { R.id.text1};

        // Creamos un array adapter y lo preparamos para mostrar los datos
        SimpleCursorAdapter grupos =
               new SimpleCursorAdapter(this, R.layout.grupos_row, cGrupos, from, to);

        mList.setAdapter(grupos);

        numGrupos.setText(("Número total de vinos: "+cGrupos.getCount()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        boolean resultado = super.onCreateOptionsMenu(m);
        // Si estamos mostrando todos los grupos, permitimos añadir grupos
        if(idVino==null) {
            m.add(Menu.NONE, AÑADIR_GRUPO, Menu.NONE, R.string.menu_insert);
        }
        menu = m;
        return resultado;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(idVino==null){
            switch (item.getItemId()){
                case AÑADIR_GRUPO:
                    añadirGrupo();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu m, View v,
                                    ContextMenu.ContextMenuInfo mInfo) {
        super.onCreateContextMenu(m, v, mInfo);
        if(idVino==null){
            m.add(Menu.NONE, EDITAR_GRUPO, Menu.NONE, "Editar grupo");
            m.add(Menu.NONE, BORRAR_GRUPO, Menu.NONE, "Borrar grupo");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(idVino==null){
            switch (item.getItemId()) {
                case EDITAR_GRUPO:
                    editarGrupo(item.getTitle().toString());
                    fillData();
                    return true;
                case BORRAR_GRUPO:
                    borrarGrupo(item.getTitle().toString());
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void añadirGrupo(){
        Intent i = new Intent(this, EditarGrupo.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void editarGrupo(String grupo){
        Intent i = new Intent(this, EditarGrupo.class);
        i.putExtra(EditarGrupo.NOMBRE,grupo);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void borrarGrupo(String grupo){
        mDbHelper.borrarGrupo(grupo);
        fillData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}