package com.sajal.notemakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Notes note = notes.get(position);
        Picasso.get().load(note.getImage()).into(holder.imageView);
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
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                Log.d("Adapter", "onMenuItemClick: delete clicked");
                                if (note.getImage()!=null) {
                                    Log.d("Adapter", "onMenuItemClick: in if");
                                    StorageReference delete = FirebaseStorage.getInstance().getReferenceFromUrl(note.getImage());
                                    delete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Notes deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Unable to delete file\nContact the Devs", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                DatabaseReference mDatabaserefernce = FirebaseDatabase.getInstance().getReference("notes/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()).child(String.valueOf(note.getTimestamp()));
                                mDatabaserefernce.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Log.d("Delete Success", "onSuccess: ");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Delete Failed", "onFailure: "+e.getMessage());
                                    }
                                });
                                notifyDataSetChanged();
                                notes.remove(position);
                                break;
                            case R.id.update:
                                Log.d("Adapter", "onMenuItemClick: updating");
                                Intent intent = new Intent(context, CreateNote.class);
                                intent.putExtra("update",note.getTimestamp());
                                context.startActivity(intent);
                                break;
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

