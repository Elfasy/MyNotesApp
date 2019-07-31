package affily.id.mynotesapp;

import java.util.ArrayList;

import affily.id.mynotesapp.entity.Note;

public interface LoadNotesCallback {
    void preExecute();
    void postExecute(ArrayList<Note> notes);
}
