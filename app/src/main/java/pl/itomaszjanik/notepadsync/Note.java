package pl.itomaszjanik.notepadsync;

import java.io.Serializable;

public class Note implements Serializable {

    private long date; //creation time of the note
    private String title; //title of the note
    private String content; //content of the note

    Note(long dateInMillis, String title, String content) {
        this.date = dateInMillis;
        this.title = title;
        this.content = content;
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

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}
