package pl.itomaszjanik.notepadsync;

import android.content.Context;

import java.io.*;
import java.util.ArrayList;

public class Utilities {

    public static final String EXTRAS_NOTE_FILENAME = "EXTRAS_NOTE_FILENAME";
    public static final String EXTRAS_NO_OF_FILES = "EXTRAS_NO_OF_FILES";
    public static final String EXTRAS_NOTE_NEW = "EXTRAS_NOTE_NEW";
    public static final String EXTRAS_NOTE_UPDATE = "EXTRAS_NOTE_UPDATE";
    public static final String EXTRAS_NOTE_MODE = "EXTRAS_NOTE_MODE";
    public static final String FILE_EXTENSION = ".txt";
    public static final String FILE_CONFIG = "appdata";
    public static final String FILE_CONFIG_EXTENTION = ".apd";

    /**
     * Save a note on private storage of the app
     * @param context Application's context
     * @param note The note to be saved
     */
    public static boolean saveNote(Context context, Note note) {

        String fileName = String.valueOf(note.getDate()) + FILE_EXTENSION;

        FileOutputStream fos;
        ObjectOutputStream oos;

        try{
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(note);
            fos.close();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    static Appdata getAppdata(Context context){

        File temp = new File(context.getFilesDir(), FILE_CONFIG + FILE_CONFIG_EXTENTION);
        if (temp.exists()){

            FileInputStream fis;
            ObjectInputStream ois;

            try { //load the file
                fis = context.openFileInput(FILE_CONFIG + FILE_CONFIG_EXTENTION);
                ois = new ObjectInputStream(fis);
                Appdata appdata = (Appdata) ois.readObject();
                fis.close();
                ois.close();
                return appdata;

            } catch (Exception e) {
                e.printStackTrace();
                return new Appdata(0);
            }
        }
        return new Appdata(0);
    }

    static boolean addNote(Context context, Appdata appdata){
        String fileName = FILE_CONFIG + FILE_CONFIG_EXTENTION;
        appdata.setNoOfNotes(appdata.getNoOfNotes() + 1);

        FileOutputStream fos;
        ObjectOutputStream oos;

        try{
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(appdata);
            oos.flush();
            fos.close();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static boolean addNote(Context context, int noOfNotes){
        String fileName = FILE_CONFIG + FILE_CONFIG_EXTENTION;

        FileOutputStream fos;
        ObjectOutputStream oos;

        try{
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(new Appdata(noOfNotes + 1));
            oos.flush();
            fos.close();
            oos.close();

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    static boolean removeNote(Context context, Appdata appdata){
        String fileName = FILE_CONFIG + FILE_CONFIG_EXTENTION;
        appdata.setNoOfNotes(appdata.getNoOfNotes() + 1);

        FileOutputStream fos;
        ObjectOutputStream oos;

        try{
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(appdata);
            oos.flush();
            fos.close();
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static boolean removeNote(Context context, int noOfNotes){
        String fileName = FILE_CONFIG + FILE_CONFIG_EXTENTION;

        FileOutputStream fos;
        ObjectOutputStream oos;

        try{
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(new Appdata(noOfNotes - 1));
            oos.flush();
            fos.close();
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Read all saved
     * @param context Application's context
     * @return ArrayList of Note
     */
    static ArrayList<Note> getAllSavedNotes(Context context) {
        ArrayList<Note> notes = new ArrayList<>();
        ArrayList<String> noteFiles = new ArrayList<>();

        File filesDir = context.getFilesDir();

        for(String file : filesDir.list()) {
            if(file.endsWith(FILE_EXTENSION)) {
                noteFiles.add(file);
            }
        }

        //Read objects and add to list of notes
        FileInputStream fis;
        ObjectInputStream ois;

        for (int i = 0; i < noteFiles.size(); i++) {

            try{
                fis = context.openFileInput(noteFiles.get(i));
                ois = new ObjectInputStream(fis);

                notes.add((Note) ois.readObject());
                fis.close();
                ois.close();

            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }
        }


        return notes;
    }

    /**
     * Loads a note file by its name
     * @param context Application's context
     * @param fileName Name of the note file
     * @return A Note object, null if something goes wrong!
     */
    public static Note getNoteByFileName(Context context, String fileName) {

        File file = new File(context.getFilesDir(), fileName);
        if(file.exists() && !file.isDirectory()) { //check if file actually exist

            FileInputStream fis;
            ObjectInputStream ois;

            try { //load the file
                fis = context.openFileInput(fileName);
                ois = new ObjectInputStream(fis);
                Note note = (Note) ois.readObject();
                fis.close();
                ois.close();

                return note;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }
    }

    public static boolean deleteFile(Context context, String fileName) {
        File dirFiles = context.getFilesDir();
        File file = new File(dirFiles, fileName);

        if(file.exists() && !file.isDirectory()) {
            return file.delete();
        }

        return false;
    }
}
