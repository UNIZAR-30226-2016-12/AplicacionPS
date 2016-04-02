package redwinecorp.misvinos.;

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

import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;


public class MisVinos extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int SEND_EMAIL_ID = Menu.FIRST + 3;
    private static final int SEND_SMS_ID = Menu.FIRST + 4;

    private static final int CATEGORY_MODE = Menu.FIRST + 5;
    private static final int NOTES_MODE = Menu.FIRST + 6;

    private static final int CATEGORY_INSERT_ID = Menu.FIRST + 7;
    private static final int CATEGORY_DELETE_ID = Menu.FIRST + 8;
    private static final int CATEGORY_EDIT_ID = Menu.FIRST + 9;

    private static final int TESTS_ID = Menu.FIRST + 10;
    private static final int ORDENAR_POR_TITULO = Menu.FIRST + 11;
    private static final int ORDENAR_POR_CATEGORIA = Menu.FIRST + 12;

    private Menu menu;
    private String modo;
    private String filtrado;
    private int ultima_usada = -1;


    private NotesDbAdapter mDbHelper;
    private Cursor mNotesCursor;
    private ListView mList;
    private ListView desplegable;
    private Bundle savedInstanceState;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadv3);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.list);
        //mDbHelper.createCategory("Default");
        filtrado = "Titulo";
        modo = "Notas";
        fillData();

        registerForContextMenu(mList);

    }

    private void fillData() {
        if(modo.equalsIgnoreCase("Notas")){
            Cursor notesCursor;
            if(filtrado.equalsIgnoreCase("Titulo")){
                notesCursor = mDbHelper.fetchAllNotes();
            } else{
                notesCursor = mDbHelper.fetchAllNotesCat();
            }
            // Get all of the notes from the database and create the item list
            startManagingCursor(notesCursor);

            // Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from = new String[] { NotesDbAdapter.KEY_TITLE};

            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] { R.id.text1};

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);


            mList.setAdapter(notes);
            mList.setSelection(ultima_usada);
        } else{
            // Get all of the notes from the database and create the item list
            Cursor notesCursor = mDbHelper.fetchAllCategories();
            startManagingCursor(notesCursor);

            // Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from = new String[] { NotesDbAdapter.KEY_NAME};

            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] { R.id.text1};

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);


            mList.setAdapter(notes);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        menu.add(Menu.NONE, CATEGORY_MODE, Menu.NONE, R.string.category_mode);
        menu.add(Menu.NONE, TESTS_ID, Menu.NONE, R.string.realizar_tests);
        menu.add(Menu.NONE, ORDENAR_POR_TITULO,Menu.NONE,R.string.ordenar_titulo);
        menu.add(Menu.NONE, ORDENAR_POR_CATEGORIA,Menu.NONE,R.string.ordenar_categoria);
        this.menu = menu;
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case CATEGORY_INSERT_ID:
                createCategory();
                return true;
            case CATEGORY_MODE:
                switchCategory();
                return true;
            case NOTES_MODE:
                switchNotes();
                return true;
            case ORDENAR_POR_TITULO:
                ordenarTitulo();
                return true;
            case ORDENAR_POR_CATEGORIA:
                ordenarCategoria();
                return true;
            case TESTS_ID:
                realizarPruebas();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(modo.equalsIgnoreCase("Notas")){
            menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
            menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
            menu.add(Menu.NONE, SEND_EMAIL_ID, Menu.NONE, "Send email");
            menu.add(Menu.NONE, SEND_SMS_ID, Menu.NONE, "Send SMS");
        } else{
            menu.add(Menu.NONE, CATEGORY_DELETE_ID, Menu.NONE, R.string.menu_category_delete);
            menu.add(Menu.NONE, CATEGORY_EDIT_ID, Menu.NONE, R.string.menu_category_edit);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                ultima_usada = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
            case EDIT_ID:
                ultima_usada = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editNote(info.id);
                return true;
            case CATEGORY_DELETE_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteCategory(info.id);
                fillData();
                return true;
            case CATEGORY_EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editCategory(info.id);
                return true;
            case SEND_EMAIL_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                sendNote(info.id, "");
                return true;
            case SEND_SMS_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                sendNote(info.id, "SMS");
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void ordenarTitulo(){
        filtrado = "Titulo";
        fillData();
    }

    private void ordenarCategoria(){
        filtrado = "Categoria";
        fillData();
    }

    private void createNote() {
        ultima_usada = mList.getCount()+1;
        Intent i = new Intent(this, EditarVino.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void editNote(long id) {
        Intent i = new Intent(this, EditarVino.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);

        startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void sendNote(long id, String tipo){
        SendAbstraction envio = new SendAbstractionImpl(this,tipo);

        Cursor note = mDbHelper.fetchNote(id);
        String titulo = note.getString(
                note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
        String cuerpo = note.getString(
                note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
        envio.send(titulo, cuerpo);
    }

    private void createCategory(){
        Intent i = new Intent(this,CategoryEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void editCategory(long id) {
        Intent i = new Intent(this, CategoryEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void realizarPruebas(){
        Test test = new Test(mDbHelper);
        test.realizarPruebas();
    }

    private void switchCategory(){
        menu.clear();
        menu.add(Menu.NONE, CATEGORY_INSERT_ID, Menu.NONE, R.string.category_insert);
        menu.add(Menu.NONE, NOTES_MODE, Menu.NONE, R.string.note_mode);

        modo = "Categorias";
        fillData();
    }

    private void switchNotes(){
        menu.clear();
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        menu.add(Menu.NONE, CATEGORY_MODE, Menu.NONE, R.string.category_mode);
        menu.add(Menu.NONE, TESTS_ID, Menu.NONE, R.string.realizar_tests);
        menu.add(Menu.NONE, ORDENAR_POR_TITULO,Menu.NONE,R.string.ordenar_titulo);
        menu.add(Menu.NONE, ORDENAR_POR_CATEGORIA,Menu.NONE,R.string.ordenar_categoria);

        modo = "Notas";
        fillData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
