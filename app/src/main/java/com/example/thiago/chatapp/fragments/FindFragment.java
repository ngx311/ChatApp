package com.example.thiago.chatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thiago.chatapp.CustomContact;
import com.example.thiago.chatapp.GridAdapter;
import com.example.thiago.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFragment extends Fragment implements GridAdapter.ItemClickListener {

    private DatabaseReference mFriendDatabase, mRequestsDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private RecyclerView contactsGrid;
    private EditText searchBox;

    private ArrayList<CustomContact> contactList = new ArrayList<>();
    private ArrayList<CustomContact> cachedResults = new ArrayList<>();
    private GridAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_findfriend, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrent_user_id);
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("requests").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);

        searchBox = view.findViewById(R.id.searchBox);
        contactsGrid = view.findViewById(R.id.contactsGrid);
        contactsGrid.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GridAdapter(getContext(), contactList);
        adapter.setClickListener(this);
        contactsGrid.setAdapter(adapter);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrent_user_id);

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String senderUsername = dataSnapshot.child("username").getValue(String.class);

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
                                        mFriendDatabase.child(friendUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot friendSnap) {
                                                boolean isFriend = friendSnap.exists();

                                                mRequestsDatabase.child(friendUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        boolean requestSent = snapshot.exists();

                                                        DatabaseReference incomingRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("requests").child(friendUserKey);

                                                        incomingRequestsDatabase.child(mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                boolean requestReceived = snapshot.exists();

                                                                CustomContact customContact = new CustomContact(friendName, friendPicture, friendUserKey, senderUsername, requestSent, isFriend, requestReceived);
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

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        throw error.toException();
                                                    }
                                                });
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

        return view;
    }

    @Override
    public void onSendFriendRequestClick(View view, int position) {
        CustomContact customContact = contactList.get(position);
        String friendName = customContact.getUsername();
        boolean isFriend = customContact.isFriend();

        if (isFriend) {
            Toast.makeText(getContext(), "Friend is already added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Friend request sent to " + friendName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cachedResults.clear();
    }
}
