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
    public static final String KEY_VINO_TIPO = "tipo";
    public static final String KEY_VINO_DENOMINACION = "denominacion";
    public static final String KEY_VINO_POSICION = "posicion";
    public static final String KEY_VINO_AÑO = "año";
    public static final String KEY_VINO_VALORACION = "valoracion";

    // Atributos de la tabla Uva
    private static final String DATABASE_NAME_UVA = "uva";
    public static final String KEY_UVA_NOMBRE = "nombre";

    // Atributos de la tabla Premio
    private static final String DATABASE_NAME_PREMIO = "premio";
    public static final String KEY_PREMIO_NOMBRE = "nombre";

    // Atributos de la tabla Nota
    private static final String DATABASE_NAME_NOTA = "nota";
    public static final String KEY_NOTA_TEXTO = "texto";

    // Atributos de la tabla Tipo
    private static final String DATABASE_NAME_TIPO = "tipo";
    public static final String KEY_TIPO_VINO = "vino";
    public static final String KEY_TIPO_UVA = "uva";
    public static final String KEY_TIPO_PORCENTAJE = "porcentaje";

    // Atributos de la tabla Gana
    private static final String DATABASE_NAME_GANA = "gana";
    public static final String KEY_GANA_VINO = "vino";
    public static final String KEY_GANA_PREMIO = "premio";

    // Atributos de la tabla Posee
    private static final String DATABASE_NAME_POSEE = "posee";
    public static final String KEY_POSEE_VINO = "vino";
    public static final String KEY_POSEE_NOTA = "nota";


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
                    KEY_VINO_TIPO           + " text, " +
                    KEY_VINO_DENOMINACION   + " text, " +
                    KEY_VINO_POSICION       + " integer, " +
                    KEY_VINO_AÑO            + " integer, not null" +
                    KEY_VINO_VALORACION     + " integer);";

    private static final String DATABASE_CREATE_UVA =
            "create table " + DATABASE_NAME_UVA + " (" +
                    KEY_UVA_NOMBRE          + " text primary key); ";

    private static final String DATABASE_CREATE_PREMIO =
            "create table " + DATABASE_NAME_PREMIO + " (" +
                    KEY_PREMIO_NOMBRE       + " text primary key); ";

    private static final String DATABASE_CREATE_NOTA =
            "create table " + DATABASE_NAME_NOTA + " (" +
                    KEY_NOTA_TEXTO          + " text primary key); ";

    private static final String DATABASE_CREATE_TIPO =
            "create table " + DATABASE_NAME_TIPO + " (" +
                    KEY_TIPO_VINO           + " integer, " +
                    KEY_TIPO_UVA            + " text, " +
                    KEY_TIPO_PORCENTAJE     + " real, " +
                    "foreign key (" + KEY_TIPO_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_TIPO_UVA + ") references " + DATABASE_NAME_UVA + "(" + KEY_UVA_NOMBRE + "), " +
                    "primary key (" + KEY_TIPO_VINO + "," + KEY_TIPO_UVA + "));";

    private static final String DATABASE_CREATE_GANA =
            "create table " + DATABASE_NAME_GANA + " (" +
                    KEY_GANA_VINO           + " integer, " +
                    KEY_GANA_PREMIO            + " text, " +
                    "foreign key (" + KEY_GANA_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_GANA_PREMIO + ") references " + DATABASE_NAME_PREMIO + "(" + KEY_PREMIO_NOMBRE + "), " +
                    "primary key (" + KEY_GANA_VINO + "," + KEY_GANA_PREMIO + "));";

    private static final String DATABASE_CREATE_POSEE =
            "create table " + DATABASE_NAME_POSEE + " (" +
                    KEY_POSEE_VINO              + " integer, " +
                    KEY_POSEE_NOTA              + " text, " +
                    "foreign key (" + KEY_POSEE_VINO + ") references " + DATABASE_NAME_VINO + "(" + KEY_VINO_ID + "), " +
                    "foreign key (" + KEY_POSEE_NOTA + ") references " + DATABASE_NAME_NOTA + "(" + KEY_NOTA_TEXTO + "), " +
                    "primary key (" + KEY_POSEE_VINO + "," + KEY_POSEE_NOTA + "));";

    /**
     **     Sentencias de creacion de los triggers
     **/
    private static final String TRIGGER_DB_UPDATE_UVA =
            "CREATE TRIGGER actualizar_uva\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_UVA + " BEGIN " +
                    "UPDATE " + DATABASE_NAME_TIPO + " SET " + KEY_TIPO_UVA + " = new." + KEY_UVA_NOMBRE +
                    " WHERE " + KEY_TIPO_UVA + " = old." + KEY_UVA_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_UVA =
            "CREATE TRIGGER borrar_uva\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_UVA + " BEGIN " +
                    "DELETE " + DATABASE_NAME_TIPO + " WHERE " + KEY_TIPO_UVA + " = old." + KEY_UVA_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_PREMIO =
            "CREATE TRIGGER actualizar_uva\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_PREMIO + " BEGIN " +
                    "UPDATE " + DATABASE_NAME_GANA + " SET " + KEY_GANA_PREMIO + " = new." + KEY_PREMIO_NOMBRE +
                    " WHERE " + KEY_GANA_PREMIO + " = old." + KEY_PREMIO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_PREMIO =
            "CREATE TRIGGER borrar_uva\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_PREMIO + " BEGIN " +
                    "DELETE " + DATABASE_NAME_GANA + " WHERE " + KEY_GANA_PREMIO + " = old." + KEY_PREMIO_NOMBRE + "; " +
                    "END;";

    private static final String TRIGGER_DB_UPDATE_NOTA =
            "CREATE TRIGGER actualizar_uva\n" +
                    "BEFORE UPDATE ON " + DATABASE_NAME_NOTA + " BEGIN " +
                    "UPDATE " + DATABASE_NAME_POSEE + " SET " + KEY_POSEE_NOTA + " = new." + KEY_NOTA_TEXTO +
                    " WHERE " + KEY_POSEE_NOTA + " = old." + KEY_NOTA_TEXTO + "; " +
                    "END;";

    private static final String TRIGGER_DB_DELETE_NOTA =
            "CREATE TRIGGER borrar_uva\n" +
                    "BEFORE DELETE ON " + DATABASE_NAME_NOTA + " BEGIN " +
                    "DELETE " + DATABASE_NAME_POSEE + " WHERE " + KEY_POSEE_NOTA + " = old." + KEY_NOTA_TEXTO + "; " +
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

    private static final String DATABASE_DROP_NOTA =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_NOTA + ";";

    private static final String DATABASE_DROP_TIPO =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_TIPO + ";";

    private static final String DATABASE_DROP_GANA =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_GANA + ";";

    private static final String DATABASE_DROP_POSEE =
            "DROP TABLE IF EXISTS " + DATABASE_NAME_POSEE + ";";

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
            db.execSQL(DATABASE_CREATE_NOTA);
            db.execSQL(DATABASE_CREATE_TIPO);
            db.execSQL(DATABASE_CREATE_GANA);
            db.execSQL(DATABASE_CREATE_POSEE);

            db.execSQL(TRIGGER_DB_UPDATE_UVA);
            db.execSQL(TRIGGER_DB_DELETE_UVA);
            db.execSQL(TRIGGER_DB_UPDATE_PREMIO);
            db.execSQL(TRIGGER_DB_DELETE_PREMIO);
            db.execSQL(TRIGGER_DB_UPDATE_NOTA);
            db.execSQL(TRIGGER_DB_DELETE_NOTA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(DATABASE_DROP_POSEE);
            db.execSQL(DATABASE_DROP_GANA);
            db.execSQL(DATABASE_DROP_TIPO);
            db.execSQL(DATABASE_DROP_NOTA);
            db.execSQL(DATABASE_DROP_PREMIO);
            db.execSQL(DATABASE_DROP_UVA);
            db.execSQL(DATABASE_DROP_VINO);
            onCreate(db);
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    /*-----------------------------------    HASTA AQUI HECHO    ---------------------------------*/
    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


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