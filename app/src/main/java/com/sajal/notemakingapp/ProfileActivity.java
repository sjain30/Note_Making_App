package com.sajal.notemakingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.security.spec.ECField;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ImageView imageView;
    private TextView name, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = findViewById(R.id.imageView2);
        name = findViewById(R.id.textView2);
        id = findViewById(R.id.textView3);
        Uri uri = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            uri = user.getPhotoUrl();
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
        
        String uri2;
        if (user.getProviderId().contains("facebook"))
            uri2 = String.format("https://graph.facebook.com/%s/picture?type=large", user.getProviderData().get(1).getUid());
        else
            uri2 = uri.toString();

        Picasso.get().load(uri2).into(imageView);
        name.setText(user.getDisplayName());
        id.setText(user.getEmail());
    }
}