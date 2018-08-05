package pl.itomaszjanik.notepadsync;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Note implements Serializable {

    private long date; //creation time of the note
    private String title; //title of the note
    private String content; //content of the note

    public Note(long dateInMillis, String title, String content) {
        this.date = dateInMillis;
        this.title = title;
        this.content = content;
    }

    public void setDateTime(long date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    /**
     * Get date time as a formatted string
     * @param context The context is used to convert the string to user set locale
     * @return String containing the date and time of the creation of the note
     */
    public String getDateTimeFormatted(Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , context.getResources().getConfiguration().locale);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(date));
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}
