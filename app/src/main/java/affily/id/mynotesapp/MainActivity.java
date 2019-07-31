package affily.id.mynotesapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import affily.id.mynotesapp.adapter.NoteAdapter;
import affily.id.mynotesapp.db.NoteHelper;
import affily.id.mynotesapp.entity.Note;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoadNotesCallback {
    RecyclerView rvNotes;
    ProgressBar progressBar;
    FloatingActionButton fabAdd;
    private static final String EXTRA_STATE = "STATE";
    private NoteAdapter adapter;
    private NoteHelper noteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Notes");

        progressBar = findViewById(R.id.progress_bar);
        rvNotes = findViewById(R.id.rv_note);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        noteHelper = NoteHelper.getInstance(getApplicationContext());
        noteHelper.open();

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        adapter = new NoteAdapter(this);
        rvNotes.setAdapter(adapter);

        if (savedInstanceState == null) {
            new LoadNotesAsync(noteHelper, this).execute();
        } else {
            ArrayList<Note> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListNotes(list);
            }
        }
    }

    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<Note>> {
        private WeakReference<LoadNotesCallback> loadNotesCallbackWeakReference;
        private WeakReference<NoteHelper> noteHelperWeakReference;

        public LoadNotesAsync(NoteHelper noteHelper, LoadNotesCallback loadNotesCallback) {
            loadNotesCallbackWeakReference = new WeakReference<>(loadNotesCallback);
            noteHelperWeakReference = new WeakReference<>(noteHelper);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadNotesCallbackWeakReference.get().preExecute();
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            return noteHelperWeakReference.get().getAllNotes();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            loadNotesCallbackWeakReference.get().postExecute(notes);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListNotes());
    }

    @Override
    public void preExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Note> notes) {
        adapter.setListNotes(notes);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add) {
            Intent intent = new Intent(MainActivity.this, NoteAddUpdateActivity.class);
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == NoteAddUpdateActivity.REQUEST_ADD){
                if (resultCode == NoteAddUpdateActivity.RESULT_ADD){
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    adapter.addItem(note);
                    rvNotes.smoothScrollToPosition(adapter.getItemCount()-1);
                    showSnackbarMessage("Satu item berhasil ditambahkan");
                }
            }else if (requestCode == NoteAddUpdateActivity.REQUEST_UPDATE){
                if (resultCode == NoteAddUpdateActivity.RESULT_UPDATE){
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION,0);
                    adapter.updateItem(position,note);
                    rvNotes.smoothScrollToPosition(position);
                    showSnackbarMessage("Satu item berhasil diubah");
                }else if (resultCode == NoteAddUpdateActivity.RESULT_DELETE){
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION,0);
                    adapter.removeItem(position);
                    showSnackbarMessage("Satu item berhasil dihapus");
                }
            }
        }
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(rvNotes,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelper.close();
    }
}
