package pl.itomaszjanik.notepadsync;

import java.io.Serializable;

public class Appdata implements Serializable {

    private int noOfNotes;

    public Appdata(int noOfNotes){
        this.noOfNotes = noOfNotes;
    }

    public int getNoOfNotes(){
        return noOfNotes;
    }

    public void setNoOfNotes(int noOfNotes){
        this.noOfNotes = noOfNotes;
    }

}
