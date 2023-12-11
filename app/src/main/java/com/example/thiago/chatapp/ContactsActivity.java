package com.example.thiago.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private DatabaseReference mFriendRequestsDatabase, mFriendsDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ListView contactsListView, pendingRequestsListView;
    private TextView pendingRequestsTextView;
    private ArrayList<Users> contactsList;
    private ArrayList<FriendRequest> pendingRequestsList;
    private ContactsAdapter contactsAdapter;
    private PendingRequestsAdapter requestsAdapter;
    private ValueEventListener contactsListener, requestsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        // Get the current user's id
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Create the adapter, passing in the current user's id
        requestsAdapter = new PendingRequestsAdapter(this, R.layout.pending_request_item, pendingRequestsList, currentUserId);

        mFriendRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests").child(mCurrentUserId);
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrentUserId);

       // contactsListView = findViewById(R.id.contactsListView);
       // pendingRequestsListView = findViewById(R.id.pendingRequestsListView);
        pendingRequestsTextView = findViewById(R.id.pendingRequestsTextView);

        contactsList = new ArrayList<>();
        pendingRequestsList = new ArrayList<>();
       // contactsAdapter = new ContactsAdapter(this, contactsList);

       // contactsListView.setAdapter(contactsAdapter);
        pendingRequestsListView.setAdapter(requestsAdapter);

        loadContacts();
        loadPendingRequests();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (contactsListener != null) {
            mFriendsDatabase.removeEventListener(contactsListener);
        }
        if (requestsListener != null) {
            mFriendRequestsDatabase.removeEventListener(requestsListener);
        }
    }

    private void loadContacts() {
        contactsListener = mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String friendId = snapshot.getKey();
                    DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("users").child(friendId);
                    friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Users user = dataSnapshot.getValue(Users.class);
                            if (user != null) {
                                contactsList.add(user);
                                contactsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Log the error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log the error
            }
        });
    }

    private void loadPendingRequests() {
        requestsListener = mFriendRequestsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pendingRequestsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FriendRequest request = snapshot.getValue(FriendRequest.class);
                    if (request != null) {
                        pendingRequestsList.add(request);
                    }
                }
                pendingRequestsTextView.setVisibility(pendingRequestsList.isEmpty() ? View.GONE : View.VISIBLE);
                requestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log the error
            }
        });
    }
}

