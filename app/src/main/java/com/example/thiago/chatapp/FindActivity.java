package com.example.thiago.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

import java.util.ArrayList;

public class FindActivity extends AppCompatActivity  {

    private DatabaseReference mFriendDatabase, mRequestsDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private RecyclerView contactsGrid;
    private EditText searchBox;

    private ArrayList<CustomContact> contactList = new ArrayList<>();
    private ArrayList<CustomContact> cachedResults = new ArrayList<>();
    private GridAdapter adapter;

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_findfriend);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrent_user_id);
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("requests").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);

        searchBox = findViewById(R.id.searchBox);
        contactsGrid = findViewById(R.id.contactsGrid);
        contactsGrid.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GridAdapter(this, contactList);
        //adapter.setClickListener(this);
        contactsGrid.setAdapter(adapter);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        gestureDetector = new GestureDetector(this, new SwipeListener());

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot friendKeySnapshot : dataSnapshot.getChildren()) {
                    String friendKey = friendKeySnapshot.getKey();

                    usersRef.child(friendKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot friendSnapshot) {
                            String friendName = friendSnapshot.child("username").getValue(String.class);
                            String friendPicture = friendSnapshot.child("image").getValue(String.class);
                            String friendUserKey = friendSnapshot.getKey();

                            if (!friendKey.equals(mCurrent_user_id)) {
                                // check if a request has been sent to this user
                                mRequestsDatabase.child(friendUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean requestSent = snapshot.exists();
                                        CustomContact customContact = new CustomContact(friendName, friendPicture, friendUserKey, requestSent);
                                        cachedResults.add(customContact);
                                        contactList.add(customContact);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        throw error.toException();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString().toLowerCase();
                contactList.clear();
                if (searchText.isEmpty()) {
                    contactList.addAll(cachedResults);
                } else {
                    for (CustomContact result : cachedResults) {
                        if (result.getUsername().toLowerCase().contains(searchText)) {
                            contactList.add(result);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    //@Override
    public void onSendFriendRequestClick(View view, int position) {
        CustomContact customContact = contactList.get(position);
        String friendName = customContact.getUsername();
        //boolean requestSent = customContact.isRequestSent();

        mFriendDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(friendName)) {
                    Toast.makeText(FindActivity.this, "Friend is already added", Toast.LENGTH_SHORT).show();
//                } else if (requestSent) {
                    Toast.makeText(FindActivity.this, "You've already sent a friend request to " + friendName, Toast.LENGTH_SHORT).show();
                } else {
                    // Send friend request logic
                    Toast.makeText(FindActivity.this, "Friend request sent to " + friendName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }


    private class SwipeListener extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
        Intent intent = new Intent(FindActivity.this, UserActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void onSwipeLeft() {
        // Implement if you need to perform action on left swipe
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear the search result cache to prevent memory leakage
        cachedResults.clear();
    }
}
