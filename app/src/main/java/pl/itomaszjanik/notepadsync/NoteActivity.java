package pl.itomaszjanik.notepadsync;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private String noteMode;
    private long noteDate;
    private String fileName;
    private Note note = null;

    private EditText noteTitle;
    private EditText noteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteTitle = findViewById(R.id.note_et_title);
        noteContent = findViewById(R.id.note_et_content);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            fileName = bundle.getString(Utilities.EXTRAS_NOTE_FILENAME, null);
            noteMode = bundle.getString(Utilities.EXTRAS_NOTE_MODE, Utilities.EXTRAS_NOTE_NEW);
        }


        //check if view/edit note bundle is set, otherwise user wants to create new note
        if (noteMode == null || noteMode.equals(Utilities.EXTRAS_NOTE_NEW)){
            noteDate = System.currentTimeMillis();
        }
        else{
            note = Utilities.getNoteByFileName(getApplicationContext(), fileName);
            if (note != null) {
                noteTitle.setText(note.getTitle());
                noteContent.setText(note.getContent());
                noteDate = note.getDate();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //load menu based on the state we are in (new, view/update/delete)

        //creating new note
        if(noteMode == null || noteMode.equals(Utilities.EXTRAS_NOTE_NEW)) {
            getMenuInflater().inflate(R.menu.menu_note_add, menu);
        }
        //viewing/updating note
        else {
            getMenuInflater().inflate(R.menu.menu_note_view, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_update:
                updateNote();
                break;

            case R.id.action_save_note:
                saveNote();
                break;

            case R.id.action_delete:
                actionDelete();
                break;

            case R.id.action_cancel:
                actionCancel();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        actionCancel();
    }

    /**
     * Display AlertDialog about deleting note and handle it
     */
    private void actionDelete() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.note_delete_title))
                .setMessage(getString(R.string.note_delete_info))
                .setPositiveButton(getString(R.string.note_delete_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleted = Utilities.deleteFile(getApplicationContext(), fileName);

                        //if it wasn't possible to delete any note, tell user
                        if(note == null || !deleted) {
                            Toast.makeText(NoteActivity.this, getString(R.string.note_delete_error), Toast.LENGTH_SHORT).show();
                        }

                        //exit activity and go back to main
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.note_delete_no), null);

        dialogDelete.show();
    }

    /**
     * Handle cancel action
     */
    private void actionCancel() {

        //if user hasn't changed anything
        if(!checkNoteChanged()) {
            //exit activity and go back to main
            finish();
        }
        //otherwise ask user if he wants to commit changes
        else {
            AlertDialog.Builder dialogCancel = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.note_discharge_title))
                    .setMessage(getString(R.string.note_delete_info))
                    .setPositiveButton(getString(R.string.note_delete_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //exit activity and go back to main
                            finish();
                        }
                    })
                    //todo add option to save note
                    .setNegativeButton(getString(R.string.note_delete_no), null);
            dialogCancel.show();
        }
    }

    /**
     * Checking if the note has been changed from initial state
     * @return true if note is changed, otherwise false
     */
    private boolean checkNoteChanged() {
        //while creating new note
        if(noteMode == null || noteMode.equals(Utilities.EXTRAS_NOTE_NEW)) {
            return !noteTitle.getText().toString().isEmpty() || !noteContent.getText().toString().isEmpty();
        }
        //while editing note
        else {
            return note != null && (!noteTitle.getText().toString().equalsIgnoreCase(note.getTitle())
                    || !noteContent.getText().toString().equalsIgnoreCase(note.getContent()));
        }
    }

    /**
     * If the note is completely empty don't save
     * @param title of the note
     * @param content of the note
     * @return true if note is not empty, else false
     */
    private boolean validateNote(String title, String content){
        if (title.isEmpty() && content.isEmpty()){
            Toast.makeText(NoteActivity.this, "Can't save empty note :(", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveNote() {

        //get the content of widgets to make a note object
        String title = noteTitle.getText().toString();
        String content = noteContent.getText().toString();

        //checking if note is valid
        if (!validateNote(title, content)) return;

        //setting note's creation time
        noteDate = System.currentTimeMillis();

        //saving the note
        boolean saved = Utilities.saveNote(this, new Note(noteDate, title, content));


        //if it wasn't possible to save any note, tell user
        if(!saved) {
            Toast.makeText(this, "Couldn't save the note", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(getBaseContext(), QueryFoldersWithTitleActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        intent.putExtras(bundle);

        startActivity(intent);

        //returning to MainActivity
        finish();
    }

    private void updateNote() {

        //get the content of widgets to make a note object
        String title = noteTitle.getText().toString();
        String content = noteContent.getText().toString();


        //checking if note is valid
        if (!validateNote(title, content)) return;

        //updating note so the time of creation doesn't change
        noteDate = note.getDate();

        //saving note
        boolean saved = Utilities.saveNote(this, new Note(noteDate, title, content));

        //if it wasn't possible to save any note, tell user
        if(!saved) {
            Toast.makeText(this, "Couldn't save the note", Toast.LENGTH_SHORT).show();
        }

        //returning to MainActivity
        finish();
    }
}
