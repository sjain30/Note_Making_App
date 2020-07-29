package com.sajal.notemakingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class CreateNote extends AppCompatActivity {

    private TextView title, body, choose;
    private ImageView imageView;
    private Spinner spinner;
    private Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private static final int GET_FROM_GALLERY = 1;
    private Uri filePath;
    private Notes notes;
    private int priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        title = findViewById(R.id.upload_title);
        body = findViewById(R.id.upload_body);
        imageView = findViewById(R.id.upload_image);
        spinner = findViewById(R.id.spinner);
        button = findViewById(R.id.create);
        choose = findViewById(R.id.chooseImage);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("notes/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (spinner.getSelectedItem().toString().equals("High"))
                    priority = 3;
                else if (spinner.getSelectedItem().toString().equals("Moderate"))
                    priority = 2;
                else
                    priority = 1;
                notes = new Notes(title.getText().toString(), body.getText().toString(), priority, null, System.currentTimeMillis());
                if (filePath!=null)
                    uploadImage(filePath);
                else
                    uploadNote(notes);
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
    }

    private void uploadNote(Notes notes) {
        myRef.child(notes.getTimestamp() + "").setValue(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateNote.this, "Note successfully created!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateNote.this, HomeActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateNote.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getImage() {
        if (ContextCompat.checkSelfPermission(CreateNote.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(CreateNote.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select an image"), GET_FROM_GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == this.RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.d("Home fragment", "onActivityResult: CropImage failed");
            }
        }
    }

    private void uploadImage(Uri resultUri) {
        if (resultUri != null) {

            StorageReference unique = FirebaseStorage.getInstance().getReference();
            final StorageReference imageRef = unique.child(notes.getTimestamp() + "/Image.jpeg");
            imageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            notes.setImage(uri.toString());
                            myRef.child(notes.getTimestamp() + "").setValue(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CreateNote.this, "Note successfully created!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(CreateNote.this, HomeActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateNote.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateNote.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}