package affily.id.mynotesapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import affily.id.mynotesapp.entity.Note;

import static affily.id.mynotesapp.db.DatabaseContract.NoteColumn.DATE;
import static affily.id.mynotesapp.db.DatabaseContract.NoteColumn.DESCRIPTION;
import static affily.id.mynotesapp.db.DatabaseContract.NoteColumn.TITLE;
import static affily.id.mynotesapp.db.DatabaseContract.TABLE_NOTE;
import static android.provider.BaseColumns._ID;

public class NoteHelper {
    private static final String DATABASE_TABLE = TABLE_NOTE;
    private static DatabaseHelper databaseHelper;
    private static NoteHelper INSTANCE;

    public NoteHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    private static SQLiteDatabase database;

    public static  NoteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NoteHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();

        if (database.isOpen()) {
            database.close();
        }
    }

    //metod untuk proses CRUD

    //mendapakan data
    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                _ID + " ASC",
                null);
        cursor.moveToFirst();
        Note note;
        if (cursor.getCount() > 0) {
            do {
                note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                note.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                note.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                arrayList.add(note);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    //menyimpan data
    public long insertNote(Note note) {
        ContentValues args = new ContentValues();
        args.put(TITLE, note.getTitle());
        args.put(DESCRIPTION, note.getDescription());
        args.put(DATE, note.getDate());
        return database.insert(DATABASE_TABLE, null, args);
    }

    //update data
    public int updateNote(Note note) {
        ContentValues args = new ContentValues();
        args.put(TITLE, note.getTitle());
        args.put(DESCRIPTION, note.getDescription());
        args.put(DATE, note.getDate());
        return database.update(DATABASE_TABLE, args, _ID + " = '" + note.getId() + "'",null );
    }

    //hapus data
    public int deleteNote(int id){
        return database.delete(DATABASE_TABLE,_ID + " = '" + id + "'",null);
    }
}
