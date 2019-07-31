package affily.id.mynotesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import affily.id.mynotesapp.db.NoteHelper;
import affily.id.mynotesapp.entity.Note;

public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_POSITION = "POSITION";
    public static final String EXTRA_NOTE = "NOTE";

    public static final int REQUEST_ADD = 123;
    public static final int RESULT_ADD = 124;
    public static final int REQUEST_UPDATE = 125;
    public static final int RESULT_UPDATE = 126;
    public static final int RESULT_DELETE = 127;

    boolean isEdit = false;

    private Note note;
    private NoteHelper noteHelper;
    private int position;

    EditText edtTitle, edtDescription;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);
        edtDescription = findViewById(R.id.edt_description);
        edtTitle = findViewById(R.id.edt_title);
        btnSubmit = findViewById(R.id.submit);
        btnSubmit.setOnClickListener(this);

        noteHelper = NoteHelper.getInstance(getApplicationContext());

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
            edtTitle.setText(note.getTitle());
            edtDescription.setText(note.getDescription());
        } else {
            note = new Note();
        }

        String ActionBar, btnTitle;
        if (isEdit) {
            ActionBar = "Ubah";
            btnTitle = "Update";
        } else {
            ActionBar = "Tambah";
            btnTitle = "Simpan";
        }

        getSupportActionBar().setTitle(ActionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit) {
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Harus diisi");
                return;
            }

            note.setTitle(title);
            note.setDescription(description);

            Intent intent = new Intent(); //karena ini intent Activity for Result maka Intent() dibiarkan kosong
            intent.putExtra(EXTRA_NOTE, note);
            intent.putExtra(EXTRA_POSITION, position);

            if (isEdit) {
                long result = noteHelper.updateNote(note);
                if (result > 0) {
                    setResult(RESULT_UPDATE, intent);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal update data", Toast.LENGTH_SHORT).show();
                }
            } else {
                note.setDate(getCurrentDate());
                long result = noteHelper.insertNote(note);
                if (result > 0) {
                    setResult(RESULT_ADD, intent);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal menambah data", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.meu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete :
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private final int ALERT_DIALOG_DELETE = 1;
    private final int ALERT_DIALOG_CLOSE = 2;

    private void showAlertDialog(int type){
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle,dialogMessage;

        if (isDialogClose){
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        }else{
            dialogTitle = "Hapus note";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose){
                            finish();
                        }else {
                            long result = noteHelper.deleteNote(note.getId());
                            if (result > 0){
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_POSITION,position);
                                setResult(RESULT_DELETE,intent);
                                finish();
                            }else {
                                Toast.makeText(NoteAddUpdateActivity.this,"Gagal menghapus",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return  dateFormat.format(date);
    }
}
