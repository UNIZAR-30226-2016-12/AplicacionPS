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

    private EditText mTitleText;
    private EditText mBodyText;
    private EditText id;
    private Spinner desplegable;
    private Long mRowId;

    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        id = (EditText) findViewById(R.id.id);
        id.setFocusable(false);
        id.setText("***");
        mTitleText = (EditText) findViewById(R.id.title);
        desplegable = (Spinner) findViewById(R.id.spinner);
        mBodyText = (EditText) findViewById(R.id.body);

        // Get all of the notes from the database and create the item list
        Cursor categoryCursor = mDbHelper.fetchAllCategories();
        startManagingCursor(categoryCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from2 = new String[] { NotesDbAdapter.KEY_NAME};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to2 = new int[] { R.id.text1};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter categories =
                new SimpleCursorAdapter(this, R.layout.notes_row, categoryCursor, from2, to2);

        desplegable.setAdapter(categories);



        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_ROWID)
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
            id.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID)));
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));

            String categoriaDeLaNota = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NAME));

            Cursor allCategories = mDbHelper.fetchAllCategories();
            startManagingCursor(allCategories);

            // Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from2 = new String[] { NotesDbAdapter.KEY_NAME};


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
            String temp = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_NAME));

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
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
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
