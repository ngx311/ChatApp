package com.example.thiago.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends Activity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    ImageButton buttonStory, homebtn, btnAddFriends, btnFriends, btnKeyboard;
    Intent intent;
    private String mCurrent_user_id;
    int check = 0;

    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user);
        buttonStory = findViewById(R.id.addStory);
        homebtn = findViewById(R.id.btnlogo);
        btnAddFriends = findViewById(R.id.addFriendHome);
        btnFriends = findViewById(R.id.btnfriends);
        btnKeyboard = findViewById(R.id.btnkeyboard);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrent_user_id);

        buttonLogout = findViewById(R.id.btnlogOut2);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(UserActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, FindActivity.class);
                startActivity(i);
            }
        });

        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, ContactsActivity.class);
                startActivity(i);
            }
        });

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, UserPage.class);
                startActivity(i);
            }
        });

        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, KeyboardActivity.class);
                startActivity(i);
            }
        });

        buttonStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, StoryActivity.class);
                startActivity(i);
            }
        });
    }
}
