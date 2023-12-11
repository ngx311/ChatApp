package com.example.thiago.chatapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SearchedUserProfile extends Activity {
    private CustomContact contact;
    private TextView user_name;
    private DatabaseReference mDatabase, mUserBase, mSearchedUser;
    private CircleImageView profile_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcheduserprofile);

        // Get the contact object from the intent
        contact = getIntent().getParcelableExtra("contact");

        if(contact != null) {
            Log.d("SearchedUserProfile", "Received contact: " + contact.getUsername());
            mSearchedUser = FirebaseDatabase.getInstance().getReference().child("users").child(contact.getUserKey());
        } else {
            Log.d("SearchedUserProfile", "Received null contact");
            return; // We can't continue if there's no contact, so return here
        }

        user_name = findViewById(R.id.text_view_nickname2);
        profile_picture = findViewById(R.id.image_view_profile2);

        // No need for a ValueEventListener here, we can just use the data from the contact object
        user_name.setText(contact.getUsername());
        Glide.with(SearchedUserProfile.this).load(contact.getImageURL()).into(profile_picture);
    }
}
