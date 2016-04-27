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
     * *     Palabras clave de la base de datos
     **/
    // Palabras clave de la tabla Vino
    private static final String DATABASE_NAME_VINO = "vino";
    public static final String KEY_VINO_ID = "_id";
    public static final String KEY_VINO_NOMBRE = "nombre";
    public static final String KEY_VINO_POSICION = "posicion";
    public static final String KEY_VINO_AÑO = "año";
    public static final String KEY_VINO_VALORACION = "valoracion";
    public static final String KEY_VINO_NOTA = "nota";

    // Atributos de la tabla Uva
    private static final String DATABASE_NAME_UVA = "uva";
    public static final String KEY_UVA_NOMBRE = "nombre";

    // Atributos de la tabla Premio
    private static final String DATABASE_NAME_PREMIO = "premio";
    public static final String KEY_PREMIO_NOMBRE = "nombre";

    // Atributos de la tabla Denominacion
    private static final String DATABASE_NAME_DENOMINACION = "denominacion";
    public static final String KEY_DENOMINACION_NOMBRE = "nombre";

    // Atributos de la tabla Grupo
    private static final String DATABASE_NAME_GRUPO = "grupo";
    public static final String KEY_GRUPO_NOMBRE = "nombre";

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

    // Atributos de la tabla Pertenece
    private static final String DATABASE_NAME_PERTENECE = "pertenece";
    public static final String KEY_PERTENECE_VINO = "vino";
    public static final String KEY_PERTENECE_GRUPO = "grupo";

    private static final String TAG = "VinosDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * *     Sentencias de creacion de las tablas de la base de datos
     **/
    private static final String DATABASE_CREATE_VINO =
            "create table " + DATABASE_NAME_VINO + " ( " +
                    KEY_VINO_ID + " integer primary key, " +
                    KEY_VINO_NOMBRE + " text not null, " +
                    KEY_VINO_POSICION + " integer, " +
                    KEY_VINO_AÑO + " integer, " +
                    KEY_VINO_VALORACION + " integer, " +
                    KEY_VINO_NOTA + " text);";

    private static final String DATABASE_CREATE_UVA =
            "create table " + DATABASE_NAME_UVA + " (" +
                    KEY_UVA_NOMBRE + " text primary key); ";

    private static final String DATABASE_CREATE_PREMIO =
            "create table " + DATABASE_NAME_PREMIO + " (" +
                    KEY_PREMIO_NOMBRE + " text primary key); ";

    private static final String DATABASE_CREATE_DENOMINACION =
            "create table " + DATABASE_NAME_DENOMINACION + " (" +
                    KEY_DENOMINACION_NOMBRE + " text primary key); ";

    private static final String DATABASE_CREATE_GRUPO =
            "create table " + DATABASE_NAME_GRUPO + " (" +
                    KEY_GRUPO_NOMBRE + " text primary key); ";

    private static final String DATABASE_CREATE_TIPO =
            "create table " + DATABASE_NAME_TIPO + " (" +
                    KEY_TIPO_NOMBRE + " text primary key); ";

    private static final String DATABASE_CREATE_COMPUESTO =
            "create table " + DATABASE_NAME_COMPUESTO + " (" +
                    KEY_COMPUESTO_VINO + " integer, " +
                    KEY_COMPUESTO_UVA + " text, " +
                    KEY_COMPUESTO_PORCENTAJE + " real, " +
                    "foreign key (" + KEY_COMPUESTO_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_COMPUESTO_UVA + ") references " + DATABASE_NAME_UVA + "(" + KEY_UVA_NOMBRE + "), " +
                    "primary key (" + KEY_COMPUESTO_VINO + "," + KEY_COMPUESTO_UVA + "));";

    private static final String DATABASE_CREATE_GANA =
            "create table " + DATABASE_NAME_GANA + " (" +
                    KEY_GANA_VINO + " integer, " +
                    KEY_GANA_PREMIO + " text, " +
                    KEY_GANA_AÑO + " integer, " +
                    "foreign key (" + KEY_GANA_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_GANA_PREMIO + ") references " + DATABASE_NAME_PREMIO + "(" + KEY_PREMIO_NOMBRE + "), " +
                    "primary key (" + KEY_GANA_VINO + "," + KEY_GANA_PREMIO + "," + KEY_GANA_AÑO + "));";

    private static final String DATABASE_CREATE_POSEE =
            "create table " + DATABASE_NAME_POSEE + " (" +
                    KEY_POSEE_VINO + " integer, " +
                    KEY_POSEE_DENOMINACION + " text, " +
                    "foreign key (" + KEY_POSEE_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_POSEE_DENOMINACION + ") references " + DATABASE_NAME_DENOMINACION + "(" + KEY_DENOMINACION_NOMBRE + "), " +
                    "primary key (" + KEY_POSEE_VINO + "," + KEY_POSEE_DENOMINACION + "));";

    private static final String DATABASE_CREATE_ES =
            "create table " + DATABASE_NAME_ES + " (" +
                    KEY_ES_VINO + " integer, " +
                    KEY_ES_TIPO + " text, " +
                    "foreign key (" + KEY_ES_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_ES_TIPO + ") references " + DATABASE_NAME_TIPO + "(" + KEY_TIPO_NOMBRE + "), " +
                    "primary key (" + KEY_ES_VINO + "," + KEY_ES_TIPO + "));";

    private static final String DATABASE_CREATE_PERTENECE =
            "create table " + DATABASE_NAME_PERTENECE + " (" +
                    KEY_PERTENECE_VINO + " integer, " +
                    KEY_PERTENECE_GRUPO + " text, " +
                    "foreign key (" + KEY_PERTENECE_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_PERTENECE_GRUPO + ") references " + DATABASE_NAME_GRUPO + "(" + KEY_GRUPO_NOMBRE + "), " +
                    "primary key (" + KEY_PERTENECE_VINO + "," + KEY_PERTENECE_GRUPO + "));";

    /**
     * *     Sentencias de creacion de los triggers
     **/
    private static final String TRIGGER_DB_UPDATE_UVA =
            "CREATE TRIGGER actualizar_uva\n" +
                    "AFTER UPDATE ON " + DATABASE_NAME_UVA + " FOR EACH ROW BEGIN " +
                    "UPDATE " + DATABASE_NAME_TIPO + " SET " + KEY_COMPUESTO_UVA + " = new." + KEY_UVA_NOMBRE +
                    " WHERE " + KEY_COMPUESTO_UVA + " = old." + KEY_UVA_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_UVA =
            "CREATE TRIGGER borrar_uva\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_UVA + " FOR EACH ROW BEGIN " +
                    "DELETE FROM " + DATABASE_NAME_TIPO + " WHERE " + KEY_COMPUESTO_UVA + " = old." + KEY_UVA_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_PREMIO =
            "CREATE TRIGGER actualizar_premio\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_PREMIO + " FOR EACH ROW BEGIN " +
                    "UPDATE " + DATABASE_NAME_GANA + " SET " + KEY_GANA_PREMIO + " = new." + KEY_PREMIO_NOMBRE +
                    " WHERE " + KEY_GANA_PREMIO + " = old." + KEY_PREMIO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_PREMIO =
            "CREATE TRIGGER borrar_premio\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_PREMIO + " FOR EACH ROW BEGIN " +
                    "DELETE FROM " + DATABASE_NAME_GANA + " WHERE " + KEY_GANA_PREMIO + " = old." + KEY_PREMIO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_DENOMINACION =
            "CREATE TRIGGER actualizar_denominacion\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_DENOMINACION + " FOR EACH ROW BEGIN " +
                    "UPDATE " + DATABASE_NAME_POSEE + " SET " + KEY_POSEE_DENOMINACION + " = new." + KEY_DENOMINACION_NOMBRE +
                    " WHERE " + KEY_POSEE_DENOMINACION + " = old." + KEY_DENOMINACION_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_DENOMINACION =
            "CREATE TRIGGER borrar_denominacion\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_DENOMINACION + " FOR EACH ROW BEGIN " +
                    "DELETE FROM " + DATABASE_NAME_POSEE + " WHERE " + KEY_POSEE_DENOMINACION + " = old." + KEY_DENOMINACION_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_TIPO =
            "CREATE TRIGGER actualizar_tipo\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_TIPO + " FOR EACH ROW BEGIN " +
                    "UPDATE " + DATABASE_NAME_ES + " SET " + KEY_ES_TIPO + " = new." + KEY_TIPO_NOMBRE +
                    " WHERE " + KEY_ES_TIPO + " = old." + KEY_TIPO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_TIPO =
            "CREATE TRIGGER borrar_tipo\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_TIPO + " FOR EACH ROW BEGIN " +
                    "DELETE FROM " + DATABASE_NAME_ES + " WHERE " + KEY_ES_TIPO + " = old." + KEY_TIPO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_GRUPO =
            "CREATE TRIGGER actualizar_grupo\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_GRUPO + " FOR EACH ROW BEGIN " +
                    "UPDATE " + DATABASE_NAME_PERTENECE + " SET " + KEY_PERTENECE_GRUPO + " = new." + KEY_GRUPO_NOMBRE +
                    " WHERE " + KEY_PERTENECE_GRUPO + " = old." + KEY_GRUPO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_GRUPO =
            "CREATE TRIGGER borrar_grupo\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_GRUPO + " FOR EACH ROW BEGIN " +
                    "DELETE FROM " + DATABASE_NAME_PERTENECE + " WHERE " + KEY_PERTENECE_GRUPO + " = old." + KEY_GRUPO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_VINO =
            "CREATE TRIGGER borrar_vino\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_VINO + " FOR EACH ROW BEGIN " +
                    "DELETE FROM " + DATABASE_NAME_COMPUESTO + " WHERE " + KEY_COMPUESTO_VINO + " = old." + KEY_VINO_ID + "; " +
                    "DELETE FROM " + DATABASE_NAME_GANA + " WHERE " + KEY_GANA_VINO + " = old." + KEY_VINO_ID + "; " +
                    "DELETE FROM " + DATABASE_NAME_POSEE + " WHERE " + KEY_POSEE_VINO + " = old." + KEY_VINO_ID + "; " +
                    "DELETE FROM " + DATABASE_NAME_ES + " WHERE " + KEY_ES_VINO + " = old." + KEY_VINO_ID + "; " +
                    "DELETE FROM " + DATABASE_NAME_PERTENECE + " WHERE " + KEY_PERTENECE_VINO + " = old." + KEY_VINO_ID + "; " +
                    "END;";

    /**
     * *     Sentencias de borrado de las tablas
     **/
    private static final String DATABASE_DROP_VINO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_VINO + ";";

    private static final String DATABASE_DROP_UVA =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_UVA + ";";

    private static final String DATABASE_DROP_PREMIO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_PREMIO + ";";

    private static final String DATABASE_DROP_DENOMINACION =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_DENOMINACION + ";";

    private static final String DATABASE_DROP_GRUPO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_GRUPO + ";";

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

    private static final String DATABASE_DROP_PERTENECE =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_PERTENECE + ";";


    /**
     * *     Propiedades de la base de datos
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
            db.execSQL(DATABASE_CREATE_GRUPO);
            db.execSQL(DATABASE_CREATE_TIPO);
            db.execSQL(DATABASE_CREATE_COMPUESTO);
            db.execSQL(DATABASE_CREATE_GANA);
            db.execSQL(DATABASE_CREATE_POSEE);
            db.execSQL(DATABASE_CREATE_ES);
            db.execSQL(DATABASE_CREATE_PERTENECE);

            db.execSQL(TRIGGER_DB_UPDATE_UVA);
            db.execSQL(TRIGGER_DB_DELETE_UVA);
            db.execSQL(TRIGGER_DB_UPDATE_PREMIO);
            db.execSQL(TRIGGER_DB_DELETE_PREMIO);
            db.execSQL(TRIGGER_DB_UPDATE_DENOMINACION);
            db.execSQL(TRIGGER_DB_DELETE_DENOMINACION);
            db.execSQL(TRIGGER_DB_UPDATE_GRUPO);
            db.execSQL(TRIGGER_DB_DELETE_GRUPO);
            db.execSQL(TRIGGER_DB_UPDATE_TIPO);
            db.execSQL(TRIGGER_DB_DELETE_TIPO);
            db.execSQL(TRIGGER_DB_DELETE_VINO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL(DATABASE_DROP_PERTENECE);
            db.execSQL(DATABASE_DROP_ES);
            db.execSQL(DATABASE_DROP_POSEE);
            db.execSQL(DATABASE_DROP_GANA);
            db.execSQL(DATABASE_DROP_COMPUESTO);
            db.execSQL(DATABASE_DROP_TIPO);
            db.execSQL(DATABASE_DROP_DENOMINACION);
            db.execSQL(DATABASE_DROP_GRUPO);
            db.execSQL(DATABASE_DROP_PREMIO);
            db.execSQL(DATABASE_DROP_UVA);
            db.execSQL(DATABASE_DROP_VINO);
            onCreate(db);
        }
    }

    /**
     * Constructor - Toma el Context para permitir la creacion/apertura de la base de datos.
     * takes the context to allow the database to be
     *
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
     *
     * @return siguiente id libre de la tabla Vino.
     */
    private long getSiguienteId() {

        Cursor c = mDb.rawQuery("SELECT MAX(" + KEY_VINO_ID + ") as max FROM " + DATABASE_NAME_VINO, null);
        c.moveToFirst();
        return c.getLong(c.getColumnIndex("max")) + 1;
    }

    /**
     * Busca el vino con el nombre y año dados
     *
     * @param nombre es el nombre del vino
     * @param año    es el año del vino
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getVino(String nombre, long año) {
        String nombreUpper = nombre.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_VINO, null,
                new String(KEY_VINO_NOMBRE + "='" + nombreUpper + "' AND " + KEY_VINO_AÑO + "=" + año),
                null, null, null, null);
        return c;
    }

    /**
     * Busca el vino con el id dados
     *
     * @param id es el id del vino
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    public Cursor getVino(long id){
        Cursor c = mDb.query(DATABASE_NAME_VINO, null,
                new String(KEY_VINO_ID + "=" + id),null, null, null, null);
        return c;
    }

    public Cursor getUvas(long id){
        Cursor c = mDb.query(DATABASE_NAME_COMPUESTO, null,
                new String(KEY_COMPUESTO_VINO + "=" + id),null, null, null, null);
        return c;
    }

    public Cursor getPremios(long id){
        Cursor c = mDb.query(DATABASE_NAME_GANA, null,
                new String(KEY_GANA_VINO + "=" + id),null, null, null, null);
        return c;
    }

    public Cursor getDenominacion(long id){
        Cursor c = mDb.query(DATABASE_NAME_POSEE, null,
                new String(KEY_POSEE_VINO + "=" + id),null, null, null, null);
        return c;
    }

    public Cursor getTipo(long id){
        Cursor c = mDb.query(DATABASE_NAME_ES, null,
                new String(KEY_ES_VINO + "=" + id),null, null, null, null);
        return c;
    }

    /**
     * Busca la uva con el nombre dado
     *
     * @param nombre es el nombre de la uva
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getUva(String nombre) {
        String uvaUpper = nombre.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_UVA, null,
                new String(KEY_UVA_NOMBRE + "='" + uvaUpper + "'"), null, null, null, null);
        return c;
    }

    /**
     * Busca el premio con el nombre dado
     *
     * @param nombre es el nombre del premio
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getPremio(String nombre) {
        String premioUpper = nombre.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_PREMIO, null,
                new String(KEY_PREMIO_NOMBRE + "='" + premioUpper + "'"), null, null, null, null);
        return c;
    }

    /**
     * Busca la denominacion con el nombre dado
     *
     * @param nombre es el nombre de la denominacion
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getDenominacion(String nombre) {
        String denominacionUpper = nombre.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_DENOMINACION, new String[]{KEY_DENOMINACION_NOMBRE},
                new String(KEY_DENOMINACION_NOMBRE + "='" + denominacionUpper + "'"), null, null, null, null);
        return c;
    }

    /**
     * Busca el tipo con el nombre dado
     *
     * @param nombre es el nombre del tipo
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getTipo(String nombre) {
        String tipoUpper = nombre.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_TIPO, new String[]{KEY_TIPO_NOMBRE},
                new String(KEY_TIPO_NOMBRE + "='" + tipoUpper + "'"), null, null, null, null);
        return c;
    }

    /**
     * Busca la composicion de un vino con una uva dados
     *
     * @param vino es el id del vino
     * @param uva  es el nombre de la uva
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getCompuesto(long vino, String uva) {
        String uvaUpper = uva.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_COMPUESTO, null,
                new String(KEY_COMPUESTO_VINO + "=" + vino + " AND " + KEY_COMPUESTO_UVA + "='" + uvaUpper + "'"),
                null, null, null, null);
        return c;
    }

    /**
     * Busca las victorias de un vino en un premio dados
     *
     * @param vino   es el id del vino
     * @param premio es el nombre del premio
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getGana(long vino, String premio, long año) {
        String premioUpper = premio.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_GANA, null,
                new String(KEY_GANA_VINO + "=" + vino + " AND " + KEY_GANA_PREMIO + "='" + premioUpper + "' AND " +
                        KEY_GANA_AÑO + "=" + año), null, null, null, null);
        return c;
    }

    /**
     * Busca la posesion de un vino con una denominacion dados
     *
     * @param vino         es el id del vino
     * @param denominacion es el nombre de la denominacion
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getPosee(long vino, String denominacion) {
        String denominacionUpper = denominacion.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_POSEE, null,
                new String(KEY_POSEE_VINO + "=" + vino + " AND " + KEY_POSEE_DENOMINACION + "='" + denominacionUpper + "'"),
                null, null, null, null);
        return c;
    }

    /**
     * Busca la existencia de un vino en un tipo
     *
     * @param vino es el id del vino
     * @param tipo es el nombre de un tipo
     * @return devuelve un cursor con el resultado de la búsqueda
     */
    private Cursor getEs(long vino, String tipo) {
        String tipoUpper = tipo.toUpperCase();
        Cursor c = mDb.query(DATABASE_NAME_ES, null,
                new String(KEY_ES_VINO + "=" + vino + " AND " + KEY_ES_TIPO + "='" + tipoUpper + "'"),
                null, null, null, null);
        return c;
    }

    /**
     * Inserta en la tabla vino el vino si no existe.
     *
     * @return devuelve true si se ha creado, false si ya estaba.
     * @params atributos de la tabla vino (null en caso de no tener alguno de ellos
     */
    public long crearVino(String nombre, long posicion, long año, long valoracion, String nota) {

        //Si no existe el vino se crea
        if (getVino(nombre, año).getCount() == 0) {

            // Usamos las cadenas en mayusculas
            String nombreUpper = nombre.toUpperCase();
            String notaUpper = nota.toUpperCase();

            // Calculamos el siguiente id
            long id = getSiguienteId();

            ContentValues valores = new ContentValues();
            valores.put(KEY_VINO_ID, id);
            valores.put(KEY_VINO_NOMBRE, nombreUpper);
            valores.put(KEY_VINO_POSICION, posicion);
            valores.put(KEY_VINO_AÑO, año);
            valores.put(KEY_VINO_VALORACION, valoracion);
            valores.put(KEY_VINO_NOTA, notaUpper);

            mDb.insert(DATABASE_NAME_VINO, null, valores);
            return id;
        } else {
            return -1;
        }
    }

    /**
     * Inserta en la tabla uva la uva si no existe.
     *
     * @return devuelve true si se ha creado, false si ya estaba.
     * @params atributos de la tabla uva
     */
    public boolean crearUva(String nombre) {

        //Si no existe el vino se crea
        if (getUva(nombre).getCount() == 0) {

            // Usamos las cadenas en mayusculas
            String nombreUpper = nombre.toUpperCase();

            ContentValues valores = new ContentValues();
            valores.put(KEY_UVA_NOMBRE, nombreUpper);

            return mDb.insert(DATABASE_NAME_UVA, null, valores) > 0;
        } else {
            return false;
        }
    }

    /**
     * Inserta en la tabla premio el premio si no existe.
     *
     * @return devuelve true si se ha creado, false si ya estaba.
     * @params atributos de la tabla premio
     */
    public boolean crearPremio(String nombre) {

        //Si no existe el vino se crea
        if (getPremio(nombre).getCount() == 0) {

            // Usamos las cadenas en mayusculas
            String nombreUpper = nombre.toUpperCase();

            ContentValues valores = new ContentValues();
            valores.put(KEY_PREMIO_NOMBRE, nombreUpper);

            return mDb.insert(DATABASE_NAME_PREMIO, null, valores) > 0;
        } else {
            return false;
        }
    }

    /**
     * Inserta en la tabla denominacion la denominacion si no existe.
     *
     * @return devuelve true si se ha creado, false si ya estaba.
     * @params atributos de la tabla denominacion
     */
    public boolean crearDenominacion(String nombre) {

        //Si no existe el vino se crea
        if (getDenominacion(nombre).getCount() == 0) {

            // Usamos las cadenas en mayusculas
            String nombreUpper = nombre.toUpperCase();

            ContentValues valores = new ContentValues();
            valores.put(KEY_DENOMINACION_NOMBRE, nombreUpper);

            return mDb.insert(DATABASE_NAME_DENOMINACION, null, valores) > 0;
        } else {
            return false;
        }
    }

    /**
     * Inserta en la tabla tipo el tipo si no existe.
     *
     * @return devuelve true si se ha creado, false si ya estaba.
     * @params atributos de la tabla tipo
     */
    public boolean crearTipo(String nombre) {

        //Si no existe el vino se crea
        if (getTipo(nombre).getCount() == 0) {

            // Usamos las cadenas en mayusculas
            String nombreUpper = nombre.toUpperCase();

            ContentValues valores = new ContentValues();
            valores.put(KEY_TIPO_NOMBRE, nombreUpper);

            return mDb.insert(DATABASE_NAME_TIPO, null, valores) > 0;
        } else {
            return false;
        }
    }

    /**
     * Enlaza una uva y un vino dado con un porcentaje.
     *
     * @param uva        nombre de una uva
     * @param porcentaje porcentaje de la uva en el vino
     * @param id          id del vino
     * @return devuelve true existe el vino y la uva, false si no existen.
     */
    public boolean añadirUva(String uva, double porcentaje, long id) {

        Cursor cU = getUva(uva);
        Cursor cV = getVino(id);
        //Si existe la uva y el vino, se relacionan.
        if (cU.getCount() > 0 && cV.getCount() > 0) {

            cU.moveToFirst();
            cV.moveToFirst();

            Cursor cC = getCompuesto(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)),
                    cU.getString(cU.getColumnIndex(KEY_UVA_NOMBRE)));

            if (cC.getCount() == 0) {
                cU.moveToFirst();
                cV.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_COMPUESTO_VINO, cV.getLong(cV.getColumnIndex(KEY_VINO_ID)));
                valores.put(KEY_COMPUESTO_UVA, cU.getString(cU.getColumnIndex(KEY_UVA_NOMBRE)));
                valores.put(KEY_COMPUESTO_PORCENTAJE, porcentaje);

                return mDb.insert(DATABASE_NAME_COMPUESTO, null, valores) > 0;
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Enlaza un premio y un vino dado en un año dado.
     *
     * @param premio  nombre de un premio
     * @param añoP    año en el que se gano
     * @param id        id del vino
     * @return devuelve true si existe el vino y el premio, false si no existen.
     */
    public boolean añadirPremio(String premio, long añoP, long id) {

        Cursor cP = getPremio(premio);
        Cursor cV = getVino(id);
        //Si existe el premio y el vino, se relacionan.
        if (cP.getCount() > 0 && cV.getCount() > 0) {

            cP.moveToFirst();
            cV.moveToFirst();

            Cursor cG = getGana(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)),
                    cP.getString(cP.getColumnIndex(KEY_PREMIO_NOMBRE)), añoP);

            if (cG.getCount() == 0) {
                cP.moveToFirst();
                cV.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_GANA_VINO, cV.getLong(cV.getColumnIndex(KEY_VINO_ID)));
                valores.put(KEY_GANA_PREMIO, cP.getString(cP.getColumnIndex(KEY_PREMIO_NOMBRE)));
                valores.put(KEY_GANA_AÑO, añoP);


                return mDb.insert(DATABASE_NAME_GANA, null, valores) > 0;
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Enlaza una denominacion y un vino dado.
     *
     * @param denominacion nombre de una denominacion
     * @param id            id del vino
     * @return devuelve true si existe el vino y la denominacion, false si no existen.
     */
    public boolean añadirDenominacion(String denominacion, long id) {

        Cursor cD = getDenominacion(denominacion);
        Cursor cV = getVino(id);
        //Si existe el premio y el vino, se relacionan.
        if (cD.getCount() > 0 && cV.getCount() > 0) {

            cD.moveToFirst();
            cV.moveToFirst();

            Cursor cP = getPosee(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)),
                    cD.getString(cD.getColumnIndex(KEY_DENOMINACION_NOMBRE)));

            if (cP.getCount() == 0) {
                cD.moveToFirst();
                cV.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_POSEE_VINO, cV.getLong(cV.getColumnIndex(KEY_VINO_ID)));
                valores.put(KEY_POSEE_DENOMINACION, cD.getString(cD.getColumnIndex(KEY_DENOMINACION_NOMBRE)));

                return mDb.insert(DATABASE_NAME_POSEE, null, valores) > 0;
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Enlaza un tipo y un vino dado.
     *
     * @param tipo    nombre de un tipo
     * @param id       id del vino
     * @return devuelve true si existe el vino y el tipo, false si no existen.
     */
    public boolean añadirTipo(String tipo, long id) {

        Cursor cT = getTipo(tipo);
        Cursor cV = getVino(id);
        //Si existe el premio y el vino, se relacionan.
        if (cT.getCount() > 0 && cV.getCount() > 0) {

            cT.moveToFirst();
            cV.moveToFirst();

            Cursor cE = getEs(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)),
                    cT.getString(cT.getColumnIndex(KEY_TIPO_NOMBRE)));

            if (cE.getCount() == 0) {
                cT.moveToFirst();
                cV.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_ES_VINO, cV.getLong(cV.getColumnIndex(KEY_VINO_ID)));
                valores.put(KEY_ES_TIPO, cT.getString(cT.getColumnIndex(KEY_TIPO_NOMBRE)));

                return mDb.insert(DATABASE_NAME_ES, null, valores) > 0;
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Elimina un vino dado.
     *
     * @param id del vino
     * @return devuelve true si existe el vino y es borrado, false si no existe o no se puede eliminar.
     */
    public boolean borrarVino(long id) {

        Cursor cV = getVino(id);

        if (cV.getCount() > 0) {

            cV.moveToFirst();

            return mDb.delete(DATABASE_NAME_VINO,
                    new String(KEY_VINO_NOMBRE + "='" + cV.getString(cV.getColumnIndex(KEY_VINO_NOMBRE)) +
                            "' AND " + KEY_VINO_AÑO + "=" + cV.getLong(cV.getColumnIndex(KEY_VINO_AÑO))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Elimina una uva dada.
     *
     * @param nombre nombre de una uva
     * @return devuelve true si existe la uva y es borrada, false si no existe o no se puede eliminar.
     */
    public boolean borrarUva(String nombre) {

        Cursor cU = getUva(nombre);

        if (cU.getCount() > 0) {

            cU.moveToFirst();

            return mDb.delete(DATABASE_NAME_UVA,
                    new String(KEY_UVA_NOMBRE + "=" + cU.getString(cU.getColumnIndex(KEY_UVA_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Elimina un premio dado.
     *
     * @param nombre nombre de un premio
     * @return devuelve true si existe el premio y es borrado, false si no existe o no se puede eliminar.
     */
    public boolean borrarPremio(String nombre) {

        Cursor cP = getPremio(nombre);

        if (cP.getCount() > 0) {

            cP.moveToFirst();

            return mDb.delete(DATABASE_NAME_PREMIO,
                    new String(KEY_PREMIO_NOMBRE + "=" + cP.getString(cP.getColumnIndex(KEY_PREMIO_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Elimina una denominacion dada.
     *
     * @param nombre nombre de una denominacion
     * @return devuelve true si existe la denominacion y es borrada, false si no existe o no se puede eliminar.
     */
    public boolean borrarDenominacion(String nombre) {

        Cursor cD = getDenominacion(nombre);

        if (cD.getCount() > 0) {

            cD.moveToFirst();

            return mDb.delete(DATABASE_NAME_DENOMINACION,
                    new String(KEY_DENOMINACION_NOMBRE + "=" + cD.getString(cD.getColumnIndex(KEY_DENOMINACION_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Elimina un tipo dado.
     *
     * @param nombre nombre de un tipo
     * @return devuelve true si existe el tipo y es borrado, false si no existe o no se puede eliminar.
     */
    public boolean borrarTipo(String nombre) {

        Cursor cT = getTipo(nombre);

        if (cT.getCount() > 0) {

            cT.moveToFirst();

            return mDb.delete(DATABASE_NAME_TIPO,
                    new String(KEY_TIPO_NOMBRE + "=" + cT.getString(cT.getColumnIndex(KEY_TIPO_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    public boolean borrarCompuesto(long id, String nombre) {

        Cursor cC = getCompuesto(id, nombre);

        if (cC.getCount() > 0) {

            cC.moveToFirst();

            return mDb.delete(DATABASE_NAME_COMPUESTO,
                    new String(KEY_COMPUESTO_VINO+"="+id+" AND "+KEY_COMPUESTO_UVA+"='"+nombre+"'"), null) > 0;
        } else {
            return false;
        }
    }

    public boolean borrarGana(long id, String nombre, long año) {

        Cursor cG = getGana(id, nombre, año);

        if (cG.getCount() > 0) {

            cG.moveToFirst();

            return mDb.delete(DATABASE_NAME_GANA,
                    new String(KEY_GANA_VINO+"="+id+" AND "+KEY_GANA_PREMIO+"='"+nombre+"' AND "+
                    KEY_GANA_AÑO+"="+año), null) > 0;
        } else {
            return false;
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    /*-----------------------------------    HASTA AQUI HECHO    ---------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    /**
     * Actualiza un vino dado.
     *
     * @param id        id del vino a actualizar
     * @param nuevoNom  nuevo nombre(null para mantener el anterior)
     * @param nuevoAño  nuevo año(-1 para matener el anterior)
     * @param nuevaPos  nueva posicion(-1 para mantener la anterior)
     * @param nuevaVal  nueva valoracion(-1 para mantener la anterior)
     * @param nuevaNota nueva nora(null para mantener la anterior)
     * @return devuelve true si existe el vino y se ha actualizado, false en caso contrario.
     */
    public boolean actualizarVino(long id, String nuevoNom, long nuevoAño, long nuevaPos,
                                  long nuevaVal, String nuevaNota) {

        Cursor cV = getVino(id);

        if (cV.getCount() > 0) {

            cV.moveToFirst();

            ContentValues valores = new ContentValues();
            valores.put(KEY_VINO_ID, id);
            valores.put(KEY_VINO_NOMBRE, nuevoNom.toUpperCase());
            valores.put(KEY_VINO_POSICION, nuevaPos);
            valores.put(KEY_VINO_AÑO, nuevoAño);
            valores.put(KEY_VINO_VALORACION, nuevaVal);
            valores.put(KEY_VINO_NOTA, nuevaNota.toUpperCase());

            return mDb.update(DATABASE_NAME_VINO, valores,
                    new String(KEY_VINO_ID + "=" + id), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Actualiza una uva dada.
     *
     * @param nombre      nombre de la uva
     * @param nuevoNombre nuevo nombre(null para mantener la anterior)
     * @return devuelve true si existe la uva y se ha actualizado, false en caso contrario.
     */
    public boolean actualizarUva(String nombre, String nuevoNombre) {

        Cursor cU = getUva(nombre);

        if (cU.getCount() > 0) {

            cU.moveToFirst();

            ContentValues valores = new ContentValues();
            valores.put(KEY_UVA_NOMBRE, nuevoNombre.toUpperCase());

            return mDb.update(DATABASE_NAME_UVA, valores,
                    new String(KEY_UVA_NOMBRE + "=" + cU.getString(cU.getColumnIndex(KEY_UVA_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Actualiza un premio dado.
     *
     * @param nombre      nombre del premio
     * @param nuevoNombre nuevo nombre(null para mantener la anterior)
     * @return devuelve true si existe el premio y se ha actualizado, false en caso contrario.
     */
    public boolean actualizarPremio(String nombre, String nuevoNombre) {

        Cursor cP = getPremio(nombre);

        if (cP.getCount() > 0) {

            cP.moveToFirst();

            ContentValues valores = new ContentValues();
            valores.put(KEY_PREMIO_NOMBRE, nuevoNombre.toUpperCase());

            return mDb.update(DATABASE_NAME_PREMIO, valores,
                    new String(KEY_PREMIO_NOMBRE + "=" + cP.getString(cP.getColumnIndex(KEY_PREMIO_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Actualiza una denominacion dada.
     *
     * @param nombre      nombre de la denominacion
     * @param nuevoNombre nuevo nombre(null para mantener la anterior)
     * @return devuelve true si existe la denominacion y se ha actualizado, false en caso contrario.
     */
    public boolean actualizarDenominacion(String nombre, String nuevoNombre) {

        Cursor cD = getDenominacion(nombre);

        if (cD.getCount() > 0) {

            cD.moveToFirst();

            ContentValues valores = new ContentValues();
            valores.put(KEY_DENOMINACION_NOMBRE, nuevoNombre.toUpperCase());

            return mDb.update(DATABASE_NAME_DENOMINACION, valores,
                    new String(KEY_DENOMINACION_NOMBRE + "=" + cD.getString(cD.getColumnIndex(KEY_DENOMINACION_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Actualiza un tipo dado.
     *
     * @param nombre      nombre del tipo
     * @param nuevoNombre nuevo nombre(null para mantener la anterior)
     * @return devuelve true si existe el tipo y se ha actualizado, false en caso contrario.
     */
    public boolean actualizarTipo(String nombre, String nuevoNombre) {

        Cursor cT = getTipo(nombre);

        if (cT.getCount() > 0) {

            cT.moveToFirst();

            ContentValues valores = new ContentValues();
            valores.put(KEY_TIPO_NOMBRE, nuevoNombre.toUpperCase());

            return mDb.update(DATABASE_NAME_TIPO, valores,
                    new String(KEY_TIPO_NOMBRE + "=" + cT.getString(cT.getColumnIndex(KEY_TIPO_NOMBRE))), null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Cambia la relacion vino-uva por vino-nuevaU con porcentaje nuevoP.
     *
     * @param id id del vino
     * @param uva    nombre de la uva
     * @param nuevaU nombre de la nueva uva
     * @param nuevoP nuevo porcentaje
     * @return devuelve true si existen los elementos y se ha cambiado, false en caso contrario.
     */
    public boolean cambiarUva(long id, String uva, String nuevaU, double nuevoP) {

        Cursor cV = getVino(id);
        Cursor cU = getUva(uva);

        //Si existe el vino, la uva y la nueva uva
        if (cV.getCount() > 0 && cU.getCount() > 0) {

            cV.moveToFirst();
            cU.moveToFirst();

            Cursor cC = getCompuesto(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)), cU.getString(cU.getColumnIndex(KEY_UVA_NOMBRE)));

            //Si existe la relacion vino-uva
            if (cC.getCount() > 0) {

                cC.moveToFirst();

                Cursor cNU = getUva(nuevaU);
                if(cNU.getCount() > 0){
                    crearUva(nuevaU.toUpperCase());
                    cNU = getUva(nuevaU.toUpperCase());
                }

                cNU.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_COMPUESTO_VINO, id);
                valores.put(KEY_COMPUESTO_UVA, nuevaU.toUpperCase());
                valores.put(KEY_COMPUESTO_VINO, nuevoP);

                return mDb.update(DATABASE_NAME_COMPUESTO, valores,
                        new String(KEY_COMPUESTO_VINO + "=" + cV.getInt(cV.getColumnIndex(KEY_VINO_ID)) +
                                " AND " + KEY_COMPUESTO_UVA + "='" + cU.getString(cU.getColumnIndex(KEY_UVA_NOMBRE))+"'"), null) > 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Cambia la relacion vino-premio por vino-nuevoP con porcentaje nuevoAP.
     *
     * @param id del vino
     * @param premio  nombre del premio
     * @param añoP    año el que se gano el premio
     * @param nuevoP  nombre del nuevo premio
     * @param nuevoAP año en el que se gano el nuevo premio
     * @return devuelve true si existen los elementos y se ha cambiado, false en caso contrario.
     */
    public boolean cambiarPremio(long id, String premio, long añoP, String nuevoP, long nuevoAP) {

        Cursor cV = getVino(id);
        Cursor cP = getPremio(premio);

        //Si existe el vino, el premio y el nuevo premio
        if (cV.getCount() > 0 && cP.getCount() > 0) {

            cV.moveToFirst();
            cP.moveToFirst();

            Cursor cG = getGana(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)),
                    cP.getString(cP.getColumnIndex(KEY_PREMIO_NOMBRE)), añoP);

            //Si existe la relacion vino-premio
            if (cG.getCount() > 0) {

                cG.moveToFirst();

                Cursor cNP = getPremio(nuevoP);
                if(cNP.getCount() > 0){
                    crearPremio(nuevoP.toUpperCase());
                    cNP = getPremio(nuevoP.toUpperCase());
                }

                cNP.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_GANA_VINO, id);
                valores.put(KEY_GANA_PREMIO, nuevoP.toUpperCase());
                valores.put(KEY_GANA_AÑO, nuevoAP);

                return mDb.update(DATABASE_NAME_GANA, valores,
                        new String(KEY_GANA_VINO + "=" + cV.getInt(cV.getColumnIndex(KEY_VINO_ID)) +
                                " AND " + KEY_GANA_PREMIO + "='" + cP.getString(cP.getColumnIndex(KEY_PREMIO_NOMBRE))+"'"), null) > 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Cambia la relacion vino-denominacion por vino-nuevaD.
     *
     * @param id       id del vino
     * @param denominacion nombre de la denominacion
     * @param nuevaD       nombre de la nueva denominacion
     * @return devuelve true si existen los elementos y se ha cambiado, false en caso contrario.
     */
    public boolean cambiarDenominacion(long id, String denominacion, String nuevaD) {

        Cursor cV = getVino(id);
        Cursor cD = getDenominacion(denominacion);

        //Si existe el vino y la denominacion
        if (cV.getCount() > 0 && cD.getCount() > 0) {

            cV.moveToFirst();
            cD.moveToFirst();

            Cursor cP = getPosee(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)),
                    cD.getString(cD.getColumnIndex(KEY_DENOMINACION_NOMBRE)));

            //Si existe la relacion vino-uva
            if (cP.getCount() > 0) {

                cP.moveToFirst();

                Cursor cND = getDenominacion(nuevaD);
                if(cND.getCount() > 0){
                    crearDenominacion(nuevaD.toUpperCase());
                    cND = getDenominacion(nuevaD.toUpperCase());
                }

                cND.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_POSEE_VINO, id);
                valores.put(KEY_POSEE_DENOMINACION, nuevaD.toUpperCase());

                return mDb.update(DATABASE_NAME_POSEE, valores,
                        new String(KEY_POSEE_VINO + "=" + cV.getInt(cV.getColumnIndex(KEY_VINO_ID)) +
                                " AND " + KEY_POSEE_DENOMINACION + "='" + cD.getString(cD.getColumnIndex(KEY_DENOMINACION_NOMBRE))+"'"), null) > 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Cambia la relacion vino-tipo por vino-nuevoT.
     *
     * @param id id del vino
     * @param tipo   nombre del tipo
     * @param nuevoT nombre del nuevo tipo
     * @return devuelve true si existen los elementos y se ha cambiado, false en caso contrario.
     */
    public boolean cambiarTipo(long id, String tipo, String nuevoT) {

        Cursor cV = getVino(id);
        Cursor cT = getTipo(tipo);

        //Si existe el vino, el tipo y el nuevo tipo
        if (cV.getCount() > 0 && cT.getCount() > 0) {

            cV.moveToFirst();
            cT.moveToFirst();

            Cursor cE = getEs(cV.getLong(cV.getColumnIndex(KEY_VINO_ID)),
                    cT.getString(cT.getColumnIndex(KEY_TIPO_NOMBRE)));

            //Si existe la relacion vino-uva
            if (cE.getCount() > 0) {

                cE.moveToFirst();

                Cursor cNT = getTipo(nuevoT);
                if(cNT.getCount() > 0){
                    crearTipo(nuevoT.toUpperCase());
                    cNT = getTipo(nuevoT.toUpperCase());
                }

                cNT.moveToFirst();

                ContentValues valores = new ContentValues();
                valores.put(KEY_ES_VINO, id);
                valores.put(KEY_ES_TIPO, nuevoT.toUpperCase());

                return mDb.update(DATABASE_NAME_ES, valores,
                        new String(KEY_ES_VINO + "=" + cV.getInt(cV.getColumnIndex(KEY_VINO_ID)) +
                                " AND " + KEY_ES_TIPO + "='" + cT.getString(cT.getColumnIndex(KEY_TIPO_NOMBRE))+"'"), null) > 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Devuelve un cursor con todos los vinos almacenados.
     *
     * @return devuelve un cursor con los vinos.
     */
    public Cursor obtenerVinos() {
        return mDb.query(DATABASE_NAME_VINO,null,null,null,null,null,null);
    }

    public long numeroVinos(){
        return mDb.query(DATABASE_NAME_VINO,null,null,null,null,null,null).getCount();
    }
}