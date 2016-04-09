package redwinecorp.misvinos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 *
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class VinosDbAdapter {

    /**
     **     Palabras clave de la base de datos
     **/
    // Palabras clave de la tabla Vino
    private static final String DATABASE_NAME_VINO = "vino";
    public static final String KEY_VINO_ID = "_id";
    public static final String KEY_VINO_NOMBRE = "nombre";
    public static final String KEY_VINO_POSICION = "posicion";
    public static final String KEY_VINO_AÑO = "año";
    public static final String KEY_VINO_VALORACION = "valoracion";
    public static final String KEY_VINO_NOTA = "tipo";

    // Atributos de la tabla Uva
    private static final String DATABASE_NAME_UVA = "uva";
    public static final String KEY_UVA_NOMBRE = "nombre";

    // Atributos de la tabla Premio
    private static final String DATABASE_NAME_PREMIO = "premio";
    public static final String KEY_PREMIO_NOMBRE = "nombre";

    // Atributos de la tabla Denominacion
    private static final String DATABASE_NAME_DENOMINACION = "denominacion";
    public static final String KEY_DENOMINACION_NOMBRE = "nombre";

    // Atributos de la tabla Tipo
    private static final String DATABASE_NAME_TIPO = "tipo";
    public static final String KEY_TIPO_NOMBRE = "nombre";

    // Atributos de la tabla Compuesto
    private static final String DATABASE_NAME_COMPUESTO = "compuesto";
    public static final String KEY_COMPUESTO_VINO = "vino";
    public static final String KEY_COMPUESTO_UVA = "uva";
    public static final String KEY_COMPUESTO_PORCENTAJE = "porcentaje";

    // Atributos de la tabla Gana
    private static final String DATABASE_NAME_GANA = "gana";
    public static final String KEY_GANA_VINO = "vino";
    public static final String KEY_GANA_PREMIO = "premio";
    public static final String KEY_GANA_AÑO = "año";

    // Atributos de la tabla Posee
    private static final String DATABASE_NAME_POSEE = "posee";
    public static final String KEY_POSEE_VINO = "vino";
    public static final String KEY_POSEE_DENOMINACION = "denominacion";

    // Atributos de la tabla Es
    private static final String DATABASE_NAME_ES = "es";
    public static final String KEY_ES_VINO = "vino";
    public static final String KEY_ES_TIPO = "tipo";


    private static final String TAG = "VinosDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     **     Sentencias de creacion de las tablas de la base de datos
     **/
    private static final String DATABASE_CREATE_VINO =
            "create table " + DATABASE_NAME_VINO + " (" +
                    KEY_VINO_ID             + " integer primary key, " +
                    KEY_VINO_NOMBRE         + " text not null, " +
                    KEY_VINO_POSICION       + " integer, " +
                    KEY_VINO_AÑO            + " integer, not null" +
                    KEY_VINO_VALORACION     + " integer, " +
                    KEY_VINO_NOTA           + " text);";

    private static final String DATABASE_CREATE_UVA =
            "create table " + DATABASE_NAME_UVA + " (" +
                    KEY_UVA_NOMBRE          + " text primary key); ";

    private static final String DATABASE_CREATE_PREMIO =
            "create table " + DATABASE_NAME_PREMIO + " (" +
                    KEY_PREMIO_NOMBRE       + " text primary key); ";

    private static final String DATABASE_CREATE_DENOMINACION =
            "create table " + DATABASE_NAME_DENOMINACION + " (" +
                    KEY_DENOMINACION_NOMBRE + " text primary key); ";

    private static final String DATABASE_CREATE_TIPO =
            "create table " + DATABASE_NAME_TIPO + " (" +
                    KEY_TIPO_NOMBRE         + " text primary key); ";

    private static final String DATABASE_CREATE_COMPUESTO =
            "create table " + DATABASE_NAME_COMPUESTO + " (" +
                    KEY_COMPUESTO_VINO           + " integer, " +
                    KEY_COMPUESTO_UVA            + " text, " +
                    KEY_COMPUESTO_PORCENTAJE     + " real, " +
                    "foreign key (" + KEY_COMPUESTO_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_COMPUESTO_UVA + ") references " + DATABASE_NAME_UVA + "(" + KEY_UVA_NOMBRE + "), " +
                    "primary key (" + KEY_COMPUESTO_VINO + "," + KEY_COMPUESTO_UVA + "));";

    private static final String DATABASE_CREATE_GANA =
            "create table " + DATABASE_NAME_GANA + " (" +
                    KEY_GANA_VINO           + " integer, " +
                    KEY_GANA_PREMIO         + " text, " +
                    KEY_GANA_AÑO            + " integer, " +
                    "foreign key (" + KEY_GANA_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_GANA_PREMIO + ") references " + DATABASE_NAME_PREMIO + "(" + KEY_PREMIO_NOMBRE + "), " +
                    "primary key (" + KEY_GANA_VINO + "," + KEY_GANA_PREMIO + "));";

    private static final String DATABASE_CREATE_POSEE =
            "create table " + DATABASE_NAME_POSEE + " (" +
                    KEY_POSEE_VINO              + " integer, " +
                    KEY_POSEE_DENOMINACION      + " text, " +
                    "foreign key (" + KEY_POSEE_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_POSEE_DENOMINACION + ") references " + DATABASE_NAME_DENOMINACION + "(" + KEY_DENOMINACION_NOMBRE + "), " +
                    "primary key (" + KEY_POSEE_VINO + "," + KEY_POSEE_DENOMINACION + "));";

    private static final String DATABASE_CREATE_ES =
            "create table " + DATABASE_NAME_ES + " (" +
                    KEY_ES_VINO              + " integer, " +
                    KEY_ES_TIPO      + " text, " +
                    "foreign key (" + KEY_ES_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_ES_TIPO + ") references " + DATABASE_NAME_TIPO + "(" + KEY_TIPO_NOMBRE + "), " +
                    "primary key (" + KEY_ES_VINO + "," + KEY_ES_TIPO + "));";

    /**
     **     Sentencias de creacion de los triggers
     **/
    private static final String TRIGGER_DB_UPDATE_UVA =
            "CREATE TRIGGER actualizar_uva\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_UVA + " BEGIN " +
                    "UPDATE " + DATABASE_NAME_TIPO + " SET " + KEY_COMPUESTO_UVA + " = new." + KEY_UVA_NOMBRE +
                    " WHERE " + KEY_COMPUESTO_UVA + " = old." + KEY_UVA_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_UVA =
            "CREATE TRIGGER borrar_uva\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_UVA + " BEGIN " +
                    "DELETE " + DATABASE_NAME_TIPO + " WHERE " + KEY_COMPUESTO_UVA + " = old." + KEY_UVA_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_PREMIO =
            "CREATE TRIGGER actualizar_premio\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_PREMIO + " BEGIN " +
                    "UPDATE " + DATABASE_NAME_GANA + " SET " + KEY_GANA_PREMIO + " = new." + KEY_PREMIO_NOMBRE +
                    " WHERE " + KEY_GANA_PREMIO + " = old." + KEY_PREMIO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_PREMIO =
            "CREATE TRIGGER borrar_premio\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_PREMIO + " BEGIN " +
                    "DELETE " + DATABASE_NAME_GANA + " WHERE " + KEY_GANA_PREMIO + " = old." + KEY_PREMIO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_DENOMINACION =
            "CREATE TRIGGER actualizar_denominacion\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_DENOMINACION + " BEGIN " +
                    "UPDATE " + DATABASE_NAME_POSEE + " SET " + KEY_POSEE_DENOMINACION + " = new." + KEY_DENOMINACION_NOMBRE +
                    " WHERE " + KEY_POSEE_DENOMINACION + " = old." + KEY_DENOMINACION_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_DENOMINACION =
            "CREATE TRIGGER borrar_denominacion\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_DENOMINACION + " BEGIN " +
                    "DELETE " + DATABASE_NAME_POSEE + " WHERE " + KEY_POSEE_DENOMINACION + " = old." + KEY_DENOMINACION_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_TIPO =
            "CREATE TRIGGER actualizar_tipo\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_TIPO + " BEGIN " +
                    "UPDATE " + DATABASE_NAME_ES + " SET " + KEY_ES_TIPO + " = new." + KEY_TIPO_NOMBRE +
                    " WHERE " + KEY_ES_TIPO + " = old." + KEY_TIPO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_TIPO =
            "CREATE TRIGGER borrar_denominacion\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_TIPO + " BEGIN " +
                    "DELETE " + DATABASE_NAME_ES + " WHERE " + KEY_ES_TIPO + " = old." + KEY_TIPO_NOMBRE + "; " +
                    "END;";

    /**
     **     Sentencias de borrado de las tablas
     **/
    private static final String DATABASE_DROP_VINO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_VINO + ";";

    private static final String DATABASE_DROP_UVA =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_UVA + ";";

    private static final String DATABASE_DROP_PREMIO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_PREMIO + ";";

    private static final String DATABASE_DROP_DENOMINACION =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_DENOMINACION + ";";

    private static final String DATABASE_DROP_TIPO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_TIPO + ";";

    private static final String DATABASE_DROP_COMPUESTO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_COMPUESTO + ";";

    private static final String DATABASE_DROP_GANA =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_GANA + ";";

    private static final String DATABASE_DROP_POSEE =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_POSEE + ";";

    private static final String DATABASE_DROP_ES =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_ES + ";";

    /**
     **     Propiedades de la base de datos
     **/
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 2;



    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_VINO);
            db.execSQL(DATABASE_CREATE_UVA);
            db.execSQL(DATABASE_CREATE_PREMIO);
            db.execSQL(DATABASE_CREATE_DENOMINACION);
            db.execSQL(DATABASE_CREATE_TIPO);
            db.execSQL(DATABASE_CREATE_COMPUESTO);
            db.execSQL(DATABASE_CREATE_GANA);
            db.execSQL(DATABASE_CREATE_POSEE);
            db.execSQL(DATABASE_CREATE_ES);

            db.execSQL(TRIGGER_DB_UPDATE_UVA);
            db.execSQL(TRIGGER_DB_DELETE_UVA);
            db.execSQL(TRIGGER_DB_UPDATE_PREMIO);
            db.execSQL(TRIGGER_DB_DELETE_PREMIO);
            db.execSQL(TRIGGER_DB_UPDATE_DENOMINACION);
            db.execSQL(TRIGGER_DB_DELETE_DENOMINACION);
            db.execSQL(TRIGGER_DB_UPDATE_TIPO);
            db.execSQL(TRIGGER_DB_DELETE_TIPO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(DATABASE_DROP_ES);
            db.execSQL(DATABASE_DROP_POSEE);
            db.execSQL(DATABASE_DROP_GANA);
            db.execSQL(DATABASE_DROP_COMPUESTO);
            db.execSQL(DATABASE_DROP_TIPO);
            db.execSQL(DATABASE_DROP_DENOMINACION);
            db.execSQL(DATABASE_DROP_PREMIO);
            db.execSQL(DATABASE_DROP_UVA);
            db.execSQL(DATABASE_DROP_VINO);
            onCreate(db);
        }
    }

    /**
     * Constructor - Toma el Context para permitir la creacion/apertura de la base de datos.
     * takes the context to allow the database to be
     * @param ctx el Context en el que se esta trabajando
     */
    public VinosDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Abre la base de datos de los vinos. Si no puede ser abierta, Intenta crear
     * una nueva instancia de la base de datos. Si no puede ser creada, lanza una
     * excepcion para señalar el fallo.
     *
     * @return this (auto-referencia, permitiendo encadenar esto en la llamada de inicializacion.
     * @throws SQLException si la base de datos no puede ser abierta ni creada
     */
    public VinosDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Cierra la base de datos de los vinos.
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Consulta y devuelve el siguiente id libre de la tabla Vino
     * @return siguiente id libre de la tabla Vino.
     */
    private long getSiguienteId(){

        Cursor c = mDb.rawQuery("SELECT MAX("+KEY_VINO_ID+") as max FROM "+DATABASE_NAME_VINO, null);
        c.moveToFirst();
        return c.getLong(c.getColumnIndex("max"))+1;
    }

    /**
     * Consulta si existe un vino en la base de datos con nombre y año dados
     *
     * @param nombre es el nombre del vino
     * @param año es el año del vino
     * @return devuelve true si existe un vino con las caracteristicas dadas, false en caso contrario.
     */
    private boolean existeVino(String nombre, long año){
        String nombreUpper = nombre.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_VINO, new String[] {KEY_VINO_ID},
                        new String(KEY_VINO_NOMBRE+"='"+nombreUpper+"' AND "+KEY_VINO_AÑO+"="+año),
                        null, null, null, null);
        return c.getCount()>0;
    }

    /**
     * Consulta si existe una uva en la base de datos con el nombre dado.
     *
     * @param uva es el nombre de la uva
     * @return devuelve true si existe una uva con el nombre dado, false en caso contrario.
     */
    private boolean existeUva(String uva){
        String uvaUpper = uva.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_UVA, new String[]{KEY_UVA_NOMBRE},
                new String(KEY_UVA_NOMBRE + "='" + uvaUpper + "'"), null, null, null, null);
        return c.getCount()>0;
    }

    /**
     * Consulta si existe un premio en la base de datos con el nombre dado.
     *
     * @param premio es el nombre del premio
     * @return devuelve true si existe un premio con el nombre dado, false en caso contrario.
     */
    private boolean existePremio(String premio){
        String premioUpper = premio.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_PREMIO, new String[]{KEY_PREMIO_NOMBRE},
                new String(KEY_PREMIO_NOMBRE+"='"+premioUpper+"'"), null, null, null, null);
        return c.getCount()>0;
    }

    /**
     * Consulta si existe una denominacion en la base de datos con el nombre dado.
     *
     * @param denominacion es el nombre de la denominacion
     * @return devuelve true si existe una denominacion con el nombre dado, false en caso contrario.
     */
    private boolean existeDenominacion(String denominacion){
        String denominacionUpper = denominacion.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_DENOMINACION, new String[]{KEY_DENOMINACION_NOMBRE},
                new String(KEY_DENOMINACION_NOMBRE+"='"+denominacionUpper+"'"), null, null, null, null);
        return c.getCount()>0;
    }

    /**
     * Consulta si existe un tipo en la base de datos con el nombre dado.
     *
     * @param tipo es el nombre de la denominacion
     * @return devuelve true si existe un tipo con el nombre dado, false en caso contrario.
     */
    private boolean existeTipo(String tipo){
        String tipoUpper = tipo.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_TIPO, new String[]{KEY_TIPO_NOMBRE},
                new String(KEY_TIPO_NOMBRE+"='"+tipoUpper+"'"), null, null, null, null);
        return c.getCount()>0;
    }

    /**
     * Inserta en la tabla vino el vino si no existe.
     *
     * @params atributos de la tabla vino (null en caso de no tener alguno de ellos
     * @return devuelve true si se ha creado, false si ya estaba.
     */
    public boolean crearVino(String nombre,long posicion,long año,long valoracion,String nota){
        if(!existeVino(nombre,año)){

            // Usamos las cadenas en mayusculas
            String nombreUpper=nombre.toUpperCase();
            String notaUpper=nota.toUpperCase();

            // Calculamos el siguiente id
            long id = getSiguienteId();

            ContentValues valores = new ContentValues();
            valores.put(KEY_VINO_ID, id);
            valores.put(KEY_VINO_NOMBRE, nombreUpper);
            valores.put(KEY_VINO_POSICION, posicion);
            valores.put(KEY_VINO_AÑO, año);
            valores.put(KEY_VINO_VALORACION, valoracion);
            valores.put(KEY_VINO_NOTA, notaUpper);

            mDb.insert(DATABASE_NAME_VINO,null,valores);

            return true;
        }
        else{
            return false;
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    /*-----------------------------------    HASTA AQUI HECHO    ---------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/





    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body, String categoria) {
        if(title!= null && title.compareToIgnoreCase("") == 0){
            return -1;
        } else {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_TITLE, title);
            initialValues.put(KEY_BODY, body);
            initialValues.put(KEY_NAME,categoria);

            return mDb.insert(DATABASE_TABLE1, null, initialValues);
        }
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE1, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE1, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY,KEY_NAME}, null, null, null, null, KEY_TITLE + " ASC");
    }

    public Cursor fetchAllNotesCat(){
        return mDb.query(DATABASE_TABLE1, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY,KEY_NAME}, null, null, null, null, KEY_NAME + " ASC");
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE1, new String[] {KEY_ROWID,
                                KEY_TITLE, KEY_BODY,KEY_NAME}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body, String categoria) {
        if(title!= null && title.compareToIgnoreCase("") == 0){
            return false;
        } else if(title == null || body == null){
            return false;
        } else{
            ContentValues args = new ContentValues();
            args.put(KEY_TITLE, title);
            args.put(KEY_BODY, body);
            args.put(KEY_NAME,categoria);

            return mDb.update(DATABASE_TABLE1, args, KEY_ROWID + "=" + rowId, null) > 0;
        }
    }

    public int getIdMaximo(){
        Cursor cursor = fetchAllNotes();
        return cursor.getCount();
    }

    public long createCategory(String name) {
        if(name!= null && name.compareToIgnoreCase("") == 0){
            return -1;
        } else{
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NAME, name);

            return mDb.insert(DATABASE_TABLE2, null, initialValues);
        }
    }

    public boolean deleteCategory(long rowId) {

        return mDb.delete(DATABASE_TABLE2, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllCategories() {

        return mDb.query(DATABASE_TABLE2, new String[] {KEY_ROWID, KEY_NAME},
                null, null, null, null, KEY_NAME + " ASC");
    }

    public Cursor fetchCategory(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE2, new String[] {KEY_ROWID,
                                KEY_NAME}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean updateCategory(long rowId, String name) {
        if(name!= null && name.compareToIgnoreCase("") == 0){
            return false;
        } else if(name == null){
            return false;
        } else{

            ContentValues args = new ContentValues();
            args.put(KEY_NAME, name);

            return mDb.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowId, null) > 0;
        }
    }
}