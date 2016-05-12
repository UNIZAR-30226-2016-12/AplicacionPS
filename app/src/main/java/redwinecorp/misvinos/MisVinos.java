package redwinecorp.misvinos;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
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
import android.widget.EditText;
import android.widget.TextView;

import redwinecorp.misvinos.VinosDbAdapter;


public class MisVinos extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    //Cadenas para crear el intent de esta actividad
    public static final String MOSTRAR_GRUPO = "grupo"; //null-todos, !null-vinos del grupo
    public static final String MOSTRAR_BUSQUEDA_P = "parametro";
    public static final String MOSTRAR_BUSQUEDA_V1 = "valor1";
    public static final String MOSTRAR_BUSQUEDA_V2 = "valor2";


    //Opciones del menu de todos los vinos
    private static final int AÑADIR_VINO = Menu.FIRST;
    private static final int ORDENAR_POR_NOMBRE_ASC = Menu.FIRST+1;
    private static final int ORDENAR_POR_NOMBRE_DESC = Menu.FIRST+2;
    private static final int ORDENAR_POR_AÑO_ASC = Menu.FIRST+3;
    private static final int ORDENAR_POR_AÑO_DESC = Menu.FIRST+4;
    private static final int ORDENAR_POR_POSICION_ASC = Menu.FIRST+5;
    private static final int ORDENAR_POR_POSICION_DESC = Menu.FIRST+6;
    private static final int ORDENAR_POR_VALORACION_ASC = Menu.FIRST+7;
    private static final int ORDENAR_POR_VALORACION_DESC = Menu.FIRST+8;
    private static final int ORDENAR_POR_DEFECTO = Menu.FIRST+9;

    //Opciones de todos los vinos
    private static final int EDITAR_VINO = Menu.FIRST;
    private static final int BORRAR_VINO = Menu.FIRST+1;

    //Opciones de los vinos de un grupo
    private static final int EDITAR_VINOG = Menu.FIRST;
    private static final int BORRAR_VINOG = Menu.FIRST+1;
    private static final int QUITAR_VINOG = Menu.FIRST+2;

    //Para saber que hay que mostrar(null-todos, !null-vinos del grupo)
    private Long idGrupo;
    private String parametro;
    private String valor1;
    private String valor2;

    //Orden de la lista
    private int orden = -1;

    //Base de datos
    private VinosDbAdapter mDbHelper;

    private Menu menu;
    private Cursor mVinosCursor;
    private ListView mList;
    private FloatingActionButton bNuevo;

    private TextView numVinos;


    /** Constructor de la clase */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coleccion_list);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        numVinos = (TextView) findViewById(R.id.numVinos);
        numVinos.setFocusable(false);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        idGrupo = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(MOSTRAR_GRUPO);
        if (idGrupo == null) {
            Bundle extras = getIntent().getExtras();
            idGrupo = (extras != null) ? extras.getLong(MOSTRAR_GRUPO,-1)
                    : null;
            if(idGrupo.longValue()==-1) idGrupo=null;
        }

        parametro = (savedInstanceState == null) ? null :
                (String) savedInstanceState.getSerializable(MOSTRAR_BUSQUEDA_P);
        if (parametro == null) {
            Bundle extras = getIntent().getExtras();
            parametro = (extras != null) ? extras.getString(MOSTRAR_BUSQUEDA_P, "")
                    : null;
        }

        valor1 = (savedInstanceState == null) ? null :
                (String) savedInstanceState.getSerializable(MOSTRAR_BUSQUEDA_V1);
        if (valor1 == null) {
            Bundle extras = getIntent().getExtras();
            valor1 = (extras != null) ? extras.getString(MOSTRAR_BUSQUEDA_V1, "")
                    : null;
        }

        valor2 = (savedInstanceState == null) ? null :
                (String) savedInstanceState.getSerializable(MOSTRAR_BUSQUEDA_V2);
        if (valor2 == null) {
            Bundle extras = getIntent().getExtras();
            valor2 = (extras != null) ? extras.getString(MOSTRAR_BUSQUEDA_V2, "")
                    : null;
        }

        mList = (ListView) findViewById(R.id.list);

        bNuevo = (FloatingActionButton) findViewById(R.id.nuevo);
        bNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                añadirVino();
            }
        });

        fillData();

        //Comportamiento al pulsar en un vino o grupo
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                verVino(id);
            }
        });

        registerForContextMenu(mList);
    }

    /**
     * *     metodo encargado de mostrar el listado de los vinos
     **/
    private void fillData() {
        Cursor cVinos;
        if(idGrupo==null) {
            cVinos = mDbHelper.obtenerVinosOrdenados(orden);
        }
        else {
            cVinos = mDbHelper.obtenerVinosGrupo(idGrupo.longValue());
        }
        if(parametro!=null && !parametro.equals("") && valor1!=null && !valor1.equals("")){
            if(parametro.equals(VinosDbAdapter.KEY_VINO_NOMBRE)){
                cVinos = mDbHelper.buscarNombre(valor1);
            }
            else if (parametro.equals(VinosDbAdapter.KEY_POSEE_DENOMINACION)) {
                cVinos = mDbHelper.buscarDenominacion(valor1);
            }
            else if (parametro.equals(VinosDbAdapter.KEY_ES_TIPO)) {
                cVinos = mDbHelper.buscarTipo(valor1);
            }
            else if(valor2!=null && !valor2.equals("")){
                try {
                    int v1 = Integer.parseInt(valor1);
                    int v2 = Integer.parseInt(valor2);
                    if (parametro.equals(VinosDbAdapter.KEY_VINO_AÑO)) {
                        cVinos = mDbHelper.buscarAño(v1, v2);
                    } else if (parametro.equals(VinosDbAdapter.KEY_VINO_VALORACION)) {
                        cVinos = mDbHelper.buscarValoracion(v1,v2);
                    } else if (parametro.equals(VinosDbAdapter.KEY_VINO_POSICION)) {
                        cVinos = mDbHelper.buscarPosicion(v1,v2);
                    }
                }
                catch(NumberFormatException e){}
            }
        }

        // Obtenemos los vinos de la base de datos
        startManagingCursor(cVinos);

        // Creamos un array de los campos que vamos a mostrar
        String[] from = new String[] { VinosDbAdapter.KEY_VINO_NOMBRE, VinosDbAdapter.KEY_VINO_AÑO};

        // Creamos un array de los campos a los que los vamos a asignar
        int[] to = new int[] { R.id.text1};

        // Creamos un array adapter y lo preparamos para mostrar los datos
        SimpleCursorAdapter vinos =
                new SimpleCursorAdapter(this, R.layout.vinos_row, cVinos, from, to);

        mList.setAdapter(vinos);

        numVinos.setText(("Número total de vinos: " + cVinos.getCount()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        boolean resultado = super.onCreateOptionsMenu(m);
        if(idGrupo==null) {
            m.add(Menu.NONE, AÑADIR_VINO, Menu.NONE, R.string.menu_insert);
            m.add(Menu.NONE, ORDENAR_POR_NOMBRE_ASC, Menu.NONE, "Ordenar por nombre asc.");
            m.add(Menu.NONE, ORDENAR_POR_NOMBRE_DESC, Menu.NONE, "Ordenar por nombre desc.");
            m.add(Menu.NONE, ORDENAR_POR_AÑO_ASC, Menu.NONE, "Ordenar por año asc.");
            m.add(Menu.NONE, ORDENAR_POR_AÑO_DESC, Menu.NONE, "Ordenar por año desc.");
            m.add(Menu.NONE, ORDENAR_POR_POSICION_ASC, Menu.NONE, "Ordenar por posicion asc.");
            m.add(Menu.NONE, ORDENAR_POR_POSICION_DESC, Menu.NONE, "Ordenar por posicion desc.");
            m.add(Menu.NONE, ORDENAR_POR_VALORACION_ASC, Menu.NONE, "Ordenar por valoracion asc.");
            m.add(Menu.NONE, ORDENAR_POR_VALORACION_DESC, Menu.NONE, "Ordenar por valoracion desc.");
            m.add(Menu.NONE, ORDENAR_POR_DEFECTO, Menu.NONE, "Ordenación por defecto");
        }
        menu = m;
        return resultado;
    }

    @Override
    /**
     * *     metodo encargado de llamar al metodo que realiza cada una de las acciones posibles de la pantalla de mis vinos ( ordenar o añadir )
     **/
    public boolean onOptionsItemSelected(MenuItem item) {

        if(idGrupo==null){
            switch (item.getItemId()){
                case android.R.id.home:
                    this.finish();
                    return true;
                case AÑADIR_VINO:
                    añadirVino();
                    return true;
                case ORDENAR_POR_NOMBRE_ASC:
                    orden=0;
                    fillData();
                    return true;
                case ORDENAR_POR_NOMBRE_DESC:
                    orden=1;
                    fillData();
                    return true;
                case ORDENAR_POR_AÑO_ASC:
                    orden=2;
                    fillData();
                    return true;
                case ORDENAR_POR_AÑO_DESC:
                    orden=3;
                    fillData();
                    return true;
                case ORDENAR_POR_POSICION_ASC:
                    orden=4;
                    fillData();
                    return true;
                case ORDENAR_POR_POSICION_DESC:
                    orden=5;
                    fillData();
                    return true;
                case ORDENAR_POR_VALORACION_ASC:
                    orden=6;
                    fillData();
                    return true;
                case ORDENAR_POR_VALORACION_DESC:
                    orden=7;
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
     * *     metodo encargado de los botones correspondientes a la pantalla de mis vinos ( borrar, editar o quitar vino de un grupo)
     **/
    public void onCreateContextMenu(ContextMenu m, View v,
                                    ContextMenu.ContextMenuInfo mInfo) {
        super.onCreateContextMenu(m, v, mInfo);
        if(idGrupo==null) {
            m.add(Menu.NONE, EDITAR_VINO, Menu.NONE, "Editar vino");
            m.add(Menu.NONE, BORRAR_VINO, Menu.NONE, "Borrar vino");
        }
        else{
            m.add(Menu.NONE, EDITAR_VINOG, Menu.NONE, "Editar vino");
            m.add(Menu.NONE, BORRAR_VINOG, Menu.NONE, "Borrar vino");
            m.add(Menu.NONE, QUITAR_VINOG, Menu.NONE, "Quitar vino del grupo");
        }
    }

    @Override
    /**
     * *     metodo encargado de las realizar las acciones sobre un vino ( borrarlo, quitarlo de un grupo o editarlo)
     **/
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(idGrupo==null) {
            switch (item.getItemId()) {
                case EDITAR_VINO:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    editarVino(info.id);
                    fillData();
                    return true;
                case BORRAR_VINO:
                    borrarVino(info.id);
                    return true;
            }
        }
        else{
            switch (item.getItemId()) {
                case EDITAR_VINO:
                    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    editarVino(info.id);
                    fillData();
                    return true;
                case BORRAR_VINO:
                    borrarVino(info.id);
                    return true;
                case QUITAR_VINOG:
                    quitarVino(info.id, idGrupo.longValue());
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }
    /**
     * *     metodo encargado de añadir un vino
     **/
    private void añadirVino(){
        Intent i = new Intent(this, EditarVino.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    /**
     * *     metodo encargado de editar un vino
     **/
    private void editarVino(long id){
        Intent i = new Intent(this, EditarVino.class);
        i.putExtra(EditarVino.ID, new Long(id));
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    /**
     * *     metodo encargado de quitar un vino de un grupo
     **/
    private void quitarVino(long vino, long grupo){
        mDbHelper.borrarPertenece(vino,grupo);
        fillData();
    }
    /**
     * *     metodo encargado de borrar un vino de la lista
     **/
    private void borrarVino(long id){

        mDbHelper.borrarVino(id);
        fillData();
    }
    /**
     * *     metodo encargado de ver un vino de la lista
     **/
    private void verVino(long id){
        Intent i = new Intent(this, VerVino.class);
        i.putExtra(VerVino.ID,new Long(id));
        startActivity(i);
    }

    @Override
    /**
     * *     metodo encargado de actualizar los cambios en la pantalla despues de realizar alguna actividad
     **/
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
