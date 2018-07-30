package pl.itomaszjanik.notepadsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    public static final int WRAP_CONTENT_LENGTH = 115;
    public static final int WRAP_TITLE_LENGTH = 30;

    public NoteAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_note_item, null);
        }

        Note note = getItem(position);

        if(note != null) {
            TextView title = convertView.findViewById(R.id.list_note_title);
            TextView content = convertView.findViewById(R.id.list_note_content_preview);

            //impossible to both be true
            if (note.getTitle().length() == 0){
                title.setVisibility(View.GONE);
            }
            if (note.getContent().length() == 0){
                content.setVisibility(View.GONE);
            }

            //todo give option to show creation time
            //TextView date = (TextView) convertView.findViewById(R.id.list_note_date);
            //date.setText(note.getDateTimeFormatted(getContext()));

            //set proper text wrapping
            title.setText(wrapper(note.getTitle(), WRAP_TITLE_LENGTH));
            content.setText(wrapper(note.getContent(), WRAP_CONTENT_LENGTH));
        }

        return convertView;
    }

    private String wrapper(String text, int wrapLenght){
        int toWrap = text.length();
        int lineBreakIndex = text.indexOf('\n');

        String output = text;

        if (toWrap > wrapLenght){
            if (lineBreakIndex > wrapLenght || lineBreakIndex < 0){
                toWrap = wrapLenght;
            }
            else {
                toWrap = lineBreakIndex;
            }
            output = text.substring(0,toWrap) + "...";
        }

        return output;
    }
}
