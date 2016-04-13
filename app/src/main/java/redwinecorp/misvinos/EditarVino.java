package redwinecorp.misvinos;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class EditarVino extends AppCompatActivity {

    private EditText nombre;
    private EditText tipo;
    private EditText id;
    private EditText uva;
    private EditText denominacion;
    private EditText year;
    private EditText localizacion;
    private EditText premios;
    private EditText valoracion;
    private EditText nota;
    private Spinner desplegable;
    private Long mRowId;

    private VinosDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new VinosDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        //id = (EditText) findViewById(R.id.id); //El id de la nota no se muestra
        id.setFocusable(false);
        id.setText("***");
        nombre = (EditText) findViewById(R.id.nomVino);
        tipo = (EditText) findViewById(R.id.nomVino);
        uva = (EditText) findViewById(R.id.nomVino);
        denominacion = (EditText) findViewById(R.id.nomVino);
        year = (EditText) findViewById(R.id.nomVino);
        localizacion = (EditText) findViewById(R.id.nomVino);
        premios = (EditText) findViewById(R.id.nomVino);
        valoracion = (EditText) findViewById(R.id.nomVino);
        nota = (EditText) findViewById(R.id.nomVino);

       // desplegable = (Spinner) findViewById(R.id.spinner);

        // Get all of the notes from the database and create the item list
        Cursor categoryCursor = mDbHelper.fetchAllCategories();
        startManagingCursor(categoryCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from2 = new String[] { VinosDbAdapter.KEY_VINO_NOMBRE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to2 = new int[] { R.id.text1};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter categories =
                new SimpleCursorAdapter(this, R.layout.vinos_row, categoryCursor, from2, to2);

        desplegable.setAdapter(categories);



        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(VinosDbAdapter.KEY_VINO_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(VinosDbAdapter.KEY_VINO_ID)
                    : null;
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            id.setText(note.getString(note.getColumnIndexOrThrow(VinosDbAdapter.KEY_VINO_ID)));
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(VinosDbAdapter.KEY_VINO_NOMBRE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(VinosDbAdapter.KEY_BODY)));

            String categoriaDeLaNota = note.getString(note.getColumnIndexOrThrow(VinosDbAdapter.KEY_NAME));

            Cursor allCategories = mDbHelper.fetchAllCategories();
            startManagingCursor(allCategories);

            // Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from2 = new String[] { VinosDbAdapter.KEY_NAME};


            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to2 = new int[] { R.id.text1};

            SimpleCursorAdapter allCategorias =
                    new SimpleCursorAdapter(this, R.layout.notes_row, allCategories, from2, to2);


            desplegable.setAdapter(allCategorias);
            desplegable.setSelection(getIndex(allCategories,categoriaDeLaNota));
        }
    }

    private int getIndex(Cursor cursor,String string){
        //Pseudo code because I dont remember the API

        int index = 0;
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            String temp = cursor.getString(cursor.getColumnIndexOrThrow(VinosDbAdapter.KEY_NAME));

            if (temp.equalsIgnoreCase(string)){
                index = i;
            }

        }

        return index;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(VinosDbAdapter.KEY_ROWID, mRowId);
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
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        //String categoria = desplegable.getItemAtPosition(desplegable.getSelectedItemPosition()).toString();
        Cursor aux = (Cursor) desplegable.getSelectedItem();
        String categoria = null;
        if(aux != null){
            categoria = ((Cursor) desplegable.getSelectedItem()).getString(1);
        }

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, categoria);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body,categoria);
        }
    }


}
