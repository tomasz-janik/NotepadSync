package pl.itomaszjanik.notepadsync;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ListView noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteList = findViewById(R.id.main_listview);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View view) ->
                createNote()
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //creating new note
            case R.id.action_create:
                createNote();
                break;
            case R.id.action_settings:
                //TODO add settings
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //load saved notes into the listview
        //first, reset the listview
        noteList.setAdapter(null);

        TextView noNotesView = findViewById(R.id.main_no_notes);
        ArrayList<Note> notes = Utilities.getAllSavedNotes(getApplicationContext());

        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note lhs, Note rhs) {
                if(lhs.getDate() > rhs.getDate()) {
                    return -1;
                } else{
                    return 1;
                }
            }
        });


        //if there are any notes
        if(notes != null && notes.size() > 0) {
            noNotesView.setVisibility(View.GONE);
            final NoteAdapter na = new NoteAdapter(this, R.layout.view_note_item, notes);
            noteList.setAdapter(na);

            //set click listener for every note
            //todo when holding note give option to remove
            noteList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) ->{
                String fileName = ((Note) noteList.getItemAtPosition(position)).getDate() + Utilities.FILE_EXTENSION;
                Intent viewNoteIntent = new Intent(getApplicationContext(), NoteActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(Utilities.EXTRAS_NOTE_FILENAME, fileName);
                bundle.putString(Utilities.EXTRAS_NOTE_MODE, Utilities.EXTRAS_NOTE_UPDATE);

                viewNoteIntent.putExtras(bundle);

                startActivity(viewNoteIntent);

            });
        }
        //if there are no notes
        else {
            noNotesView.setVisibility(View.VISIBLE);
        }

    }

    private void createNote(){
        Intent intent = new Intent(this, NoteActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(Utilities.EXTRAS_NOTE_MODE, Utilities.EXTRAS_NOTE_NEW);

        intent.putExtras(bundle);
        startActivity(intent);
    }

}