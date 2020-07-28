package com.sajal.notemakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Notes> notes;

    public RecyclerAdapter(Context context, ArrayList<Notes> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_card, parent, false);
        return new RecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notes note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.body.setText(note.getBody());
        int priority = note.getPriority();
        if (priority == 3)
            holder.priority.setText("High");
        else if (priority == 2)
            holder.priority.setText("Moderate");
        else
            holder.priority.setText("Low");

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.priority_menu, popup.getMenu());
                final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("notes");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.high:
                                Notes newNote = new Notes(note.getTitle(), note.getBody(),3,note.getImage(),note.getTimestamp());
                                mDatabaseReference.child(note.getTimestamp() + "").setValue(newNote);

                            case R.id.moderate:
                                Notes newNote2 = new Notes(note.getTitle(), note.getBody(),2,note.getImage(),note.getTimestamp());
                                mDatabaseReference.child(note.getTimestamp() + "").setValue(newNote2);

                            case R.id.low:
                                Notes newNote3 = new Notes(note.getTitle(), note.getBody(),1,note.getImage(),note.getTimestamp());
                                mDatabaseReference.child(note.getTimestamp() + "").setValue(newNote3);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, priority, body;
        ImageView imageView;
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            priority = itemView.findViewById(R.id.priority);
            body = itemView.findViewById(R.id.body);
            imageView = itemView.findViewById(R.id.imageView);
            imageButton = itemView.findViewById(R.id.dropDownButton);
        }
    }
}

