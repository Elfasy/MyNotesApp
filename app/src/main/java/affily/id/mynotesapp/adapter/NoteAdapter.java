package affily.id.mynotesapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import affily.id.mynotesapp.CustumOnClickListener;
import affily.id.mynotesapp.NoteAddUpdateActivity;
import affily.id.mynotesapp.R;
import affily.id.mynotesapp.entity.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {
    private ArrayList<Note> listNotes = new ArrayList<>();

    public void setListNotes(ArrayList<Note> listNotes) {
        if (listNotes.size() > 0) {
            this.listNotes.clear();
        }
        this.listNotes.addAll(listNotes);
        notifyDataSetChanged();
    }

    public ArrayList<Note> getListNotes() {
        return listNotes;
    }

    public NoteAdapter(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_note,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvTitle.setText(listNotes.get(position).getTitle());
        holder.tvDescription.setText(listNotes.get(position).getDescription());
        holder.tvDate.setText(listNotes.get(position).getDate());
        holder.cvNote.setOnClickListener(new CustumOnClickListener(position, new CustumOnClickListener.OnItemCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION,position);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE,listNotes.get(position));
                activity.startActivityForResult(intent,NoteAddUpdateActivity.REQUEST_UPDATE);
//                Toast.makeText(activity,"Hai",Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvDescription, tvDate;
        final CardView cvNote;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            cvNote = itemView.findViewById(R.id.cv_item_note);
        }
    }

    public void addItem(Note note){
        this.listNotes.add(note);
        notifyItemInserted(listNotes.size() -1);
    }

    public void updateItem(int position, Note note){
        this.listNotes.set(position,note);
        notifyItemChanged(position,note);
    }

    public void removeItem(int position){
        this.listNotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,listNotes.size());
    }
}
