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
    public static final String VER = "ver";    //true-> sin opciones
                                                //false-> con opciones
    public static final String INICIO = "inicio"; //true-> al pulsar un grupo se va a ver los vinos de ese grupo
                                                  //false-> al pulsar un grupo se devuelve el nombre de ese grupo

    //Opciones del menu de todos los grupos(id==null)
    private static final int AÑADIR_GRUPO = Menu.FIRST;
    private static final int ORDENAR_POR_NOMBRE_ASC = Menu.FIRST+1;
    private static final int ORDENAR_POR_NOMBRE_DESC = Menu.FIRST+2;
    private static final int ORDENAR_POR_DEFECTO = Menu.FIRST+3;

    //Opciones del menu de todos los grupos(id!=null)
    private static final int ORDENARV_POR_NOMBRE_ASC = Menu.FIRST;
    private static final int ORDENARV_POR_NOMBRE_DESC = Menu.FIRST+1;
    private static final int ORDENARV_POR_DEFECTO = Menu.FIRST+2;

    //Opciones del vino
    private static final int EDITAR_GRUPO = Menu.FIRST;
    private static final int BORRAR_GRUPO = Menu.FIRST+1;
    private static final int QUITAR_GRUPO = Menu.FIRST+2;

    //Para saber que hay que mostrar(null->todos , !null->los del vino)
    private Long idVino = null;
    private Boolean inicio = null;
    private Boolean ver = null;

    private int orden = -1;

    //Base de datos
    private VinosDbAdapter mDbHelper;

    private Menu menu;
    private Cursor mGruposCursor;
    private ListView mList;
    private FloatingActionButton bNuevo;

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
            idVino = (extras != null) ? extras.getLong(ID_VINO, -1)
                    : null;
            if(idVino.longValue()==-1) idVino=null;
        }

        inicio = (savedInstanceState == null) ? null :
                (Boolean) savedInstanceState.getSerializable(INICIO);
        if (inicio == null) {
            Bundle extras = getIntent().getExtras();
            inicio = (extras != null) ? extras.getBoolean(INICIO)
                    : null;
        }

        ver = (savedInstanceState == null) ? null :
                (Boolean) savedInstanceState.getSerializable(VER);
        if (ver == null) {
            Bundle extras = getIntent().getExtras();
            ver = (extras != null) ? extras.getBoolean(VER)
                    : null;
        }

        mList = (ListView) findViewById(R.id.listG);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                if(idVino==null && inicio!=null && inicio.booleanValue()){
                    // Ir a MisVinos pasando el id del grupo pulsado
                    verVinos(id);
                }
                else if(idVino==null && inicio!=null && !inicio.booleanValue()){
                    // Devolver a la actividad invocante el id del grupo seleccionado y del nombre
                    devolverGrupoAsignar(id);
                }
                else if(idVino!=null && (ver==null || !ver.booleanValue())){
                    // Devolver a la actividad invocante el id del grupo seleccionado y del nombre
                    devolverGrupoEliminar(id);
                }
            }
        });

        bNuevo = (FloatingActionButton) findViewById(R.id.nuevo);
        bNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                añadirGrupo();
            }
        });

        fillData();

        registerForContextMenu(mList);
    }

    /**
     * *     metodo encargado de mostrar el listado de los grupos de vinos
     **/
    private void fillData() {

        Cursor cGrupos;
        if(idVino==null) {
            cGrupos = mDbHelper.obtenerGruposOrdenados(orden);
        }
        else{
            cGrupos = mDbHelper.obtenerGruposOrdenadosVino(idVino.longValue(), orden);
        }

        startManagingCursor(cGrupos);

        // Creamos un array de los campos que vamos a mostrar
        String[] from = new String[] { VinosDbAdapter.KEY_GRUPO_NOMBRE };

        // Creamos un array de los campos a los que los vamos a asignar
        int[] to = new int[] { R.id.text2};

        // Creamos un array adapter y lo preparamos para mostrar los datos
        SimpleCursorAdapter grupos =
               new SimpleCursorAdapter(this, R.layout.grupos_row, cGrupos, from, to);

        mList.setAdapter(grupos);

        numGrupos.setText(("Número total de grupos: " + cGrupos.getCount()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        boolean resultado = super.onCreateOptionsMenu(m);
        if(idVino==null) {
            m.add(Menu.NONE, AÑADIR_GRUPO, Menu.NONE, "Añadir Grupo");
            m.add(Menu.NONE, ORDENAR_POR_NOMBRE_ASC, Menu.NONE, "Ordenar por nombre asc.");
            m.add(Menu.NONE, ORDENAR_POR_NOMBRE_DESC, Menu.NONE, "Ordenar por nombre desc.");
            m.add(Menu.NONE, ORDENAR_POR_DEFECTO, Menu.NONE, "Ordenación por defecto");
        }
        else{
            m.add(Menu.NONE, ORDENARV_POR_NOMBRE_ASC, Menu.NONE, "Ordenar por nombre asc.");
            m.add(Menu.NONE, ORDENARV_POR_NOMBRE_DESC, Menu.NONE, "Ordenar por nombre desc.");
            m.add(Menu.NONE, ORDENARV_POR_DEFECTO, Menu.NONE, "Ordenación por defecto");
        }
        menu = m;
        return resultado;
    }

    @Override
    /**
     * *     metodo encargado de llamar al metodo que realiza cada una de las acciones posibles de la pantalla de mis grupos ( ordenar o añadir )
     **/
    public boolean onOptionsItemSelected(MenuItem item) {

        if(idVino==null){
            switch (item.getItemId()){
                case AÑADIR_GRUPO:
                    añadirGrupo();
                    return true;
                case ORDENAR_POR_NOMBRE_ASC:
                    orden=0;
                    fillData();
                    return true;
                case ORDENAR_POR_NOMBRE_DESC:
                    orden=1;
                    fillData();
                    return true;
                case ORDENAR_POR_DEFECTO:
                    orden=-1;
                    fillData();
                    return true;
            }
        }
        else{
            switch (item.getItemId()){
                case ORDENAR_POR_NOMBRE_ASC:
                    orden=0;
                    fillData();
                    return true;
                case ORDENAR_POR_NOMBRE_DESC:
                    orden=1;
                    fillData();
                    return true;
                case ORDENAR_POR_DEFECTO:
                    orden=-1;
                    fillData();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * *     metodo encargado de los botones correspondientes a la pantalla de mis vinos ( borrar, editar )
     **/
    public void onCreateContextMenu(ContextMenu m, View v,
                                    ContextMenu.ContextMenuInfo mInfo) {
        super.onCreateContextMenu(m, v, mInfo);
        if(idVino==null && inicio.booleanValue()){
            m.add(Menu.NONE, EDITAR_GRUPO, Menu.NONE, "Editar grupo");
            m.add(Menu.NONE, BORRAR_GRUPO, Menu.NONE, "Borrar grupo");
        }
        else if((idVino==null && !inicio.booleanValue()) || (idVino!=null && !ver.booleanValue())){
            m.add(Menu.NONE, EDITAR_GRUPO, Menu.NONE, "Editar grupo");
        }
    }

    @Override
    /**
     * *     metodo encargado de las realizar las acciones sobre un grupo ( borrarlo o editarlo)
     **/
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(idVino==null && inicio.booleanValue()){
            switch (item.getItemId()) {
                case EDITAR_GRUPO:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    editarGrupo(info.id);
                    fillData();
                    return true;
                case BORRAR_GRUPO:
                    borrarGrupo(info.id);
                    return true;
            }
        }
        else if((idVino==null && !inicio.booleanValue()) || (idVino!=null && !ver.booleanValue())){
            switch (item.getItemId()) {
                case EDITAR_GRUPO:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    editarGrupo(info.id);
                    fillData();
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }
    /**
     * *     metodo encargado de añadir un grupo
     **/
    private void añadirGrupo(){
        Intent i = new Intent(this, EditarGrupo.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    /**
     * *     metodo encargado de editar un grupo
     **/
    private void editarGrupo(long grupo){
        Intent i = new Intent(this, EditarGrupo.class);
        i.putExtra(EditarGrupo.ID,new Long(grupo));
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    /**
     * *     metodo encargado de borrar un grupo
     **/
    private void borrarGrupo(long grupo){
        mDbHelper.borrarGrupo(grupo);
        fillData();
    }
    /**
     * *     metodo encargado de ver los vinos de un grupo
     **/
    private void verVinos(long id){
        Intent i = new Intent(this, MisVinos.class);
        i.putExtra(MisVinos.MOSTRAR_GRUPO, new Long(id));
        startActivity(i);
    }
    /**
     * *     metodo encargado de asignar un vino a un grupo
     **/
    private void devolverGrupoAsignar(long id){
        Intent data = new Intent();
        data.putExtra(EditarVino.ASIGNAR, new Long(id));
        setResult(RESULT_OK, data);
        finish();
    }
    /**
     * *     metodo encargado de quitar un vino de un grupo
     **/
    private void devolverGrupoEliminar(long id){
        Intent data = new Intent();
        data.putExtra(EditarVino.ELIMINAR, new Long(id));
        setResult(RESULT_OK, data);
        finish();
    }
    /**
     * *     metodo encargado de actualizar los cambios en la pantalla despues de realizar alguna actividad
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}