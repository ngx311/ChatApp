package com.example.thiago.chatapp.fragments;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.thiago.chatapp.ContactsAdapter;
import com.example.thiago.chatapp.FriendRequestAdapter;
import com.example.thiago.chatapp.CustomContact;
import com.example.thiago.chatapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.transition.TransitionManager;


import java.util.ArrayList;

public class ContactsFragment extends Fragment implements FriendRequestAdapter.OnItemClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private RecyclerView contactsRecyclerView;
    private RecyclerView pendingRequestsRecyclerView;
    private ArrayList<CustomContact> contactsList;
    private ArrayList<CustomContact> cachedFriendsList;
    private ArrayList<CustomContact> filteredFriendsList;
    private ArrayList<CustomContact> pendingRequestsList;
    private ContactsAdapter contactsAdapter;
    private FriendRequestAdapter requestsAdapter;
    private ValueEventListener commonListener;

    private EditText searchboxC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contacts, container, false);

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        pendingRequestsRecyclerView = view.findViewById(R.id.pendingRequestsRecyclerView);

        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        contactsList = new ArrayList<>();
        cachedFriendsList = new ArrayList<>();
        filteredFriendsList = new ArrayList<>();
        pendingRequestsList = new ArrayList<>();

        searchboxC = view.findViewById(R.id.searchBox);

        contactsAdapter = new ContactsAdapter(getContext(), contactsList);
        contactsRecyclerView.setAdapter(contactsAdapter);

        requestsAdapter = new FriendRequestAdapter(getContext(), pendingRequestsList, ContactsFragment.this);
        pendingRequestsRecyclerView.setAdapter(requestsAdapter);

        commonListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String node = dataSnapshot.getRef().getKey();
                if (node != null) {
                    switch (node) {
                        case "friends":
                            handleFriendsListChange(dataSnapshot);
                            break;
                        case "receivedRequests":
                            handleReceivedRequestsChange(dataSnapshot);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error handling
            }
        };

        mDatabase.child("users").child(mCurrentUserId).child("friends").addValueEventListener(commonListener);
        mDatabase.child("users").child(mCurrentUserId).child("receivedRequests").addValueEventListener(commonListener);

        searchboxC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        MaterialButton hidePendingButton = view.findViewById(R.id.hide_pending_button);
        Drawable hideDrawable = getResources().getDrawable(R.drawable.hide_request_button);
        Drawable showDrawable = getResources().getDrawable(R.drawable.show_request_button);

        Drawable scaledHideDrawable = scaleDrawable(hideDrawable, 152, 14);
        Drawable scaledShowDrawable = scaleDrawable(showDrawable, 152, 14);

        hidePendingButton.setIcon(scaledHideDrawable);

        hidePendingButton.setOnClickListener(v -> {
            // Create a transition set to group multiple transitions
            TransitionSet transitionSet = new TransitionSet();

            // Add a change bounds transition for the RecyclerView and the button
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.addTarget(pendingRequestsRecyclerView);
            changeBounds.addTarget(hidePendingButton);
            transitionSet.addTransition(changeBounds);

            // Set the order of operation
            transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);

            // Set the duration of the transition
            transitionSet.setDuration(1150);  // 1150 milliseconds = 1.15 seconds

            // Start the transition
            TransitionManager.beginDelayedTransition((ViewGroup) v.getRootView(), transitionSet);

            // Toggle the visibility of the RecyclerView and the image in the button
            if (pendingRequestsRecyclerView.getVisibility() == View.VISIBLE) {
                pendingRequestsRecyclerView.setVisibility(View.GONE);
                hidePendingButton.setIcon(scaledShowDrawable);
            } else {
                pendingRequestsRecyclerView.setVisibility(View.VISIBLE);
                hidePendingButton.setIcon(scaledHideDrawable);
            }
        });

        return view;
    }

    public void handleSentRequestsChange(DataSnapshot dataSnapshot) {
        pendingRequestsList.clear();
        for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
            String requestId = requestSnapshot.getKey();
            mDatabase.child("users").child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    String imageUrl = userSnapshot.child("image").getValue(String.class);
                    CustomContact request = new CustomContact(username, imageUrl, requestId);
                    if (request != null) {
                        pendingRequestsList.add(request);
                        requestsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error handling
                }
            });
        }
    }

    private void searchUsers(String s) {
        mDatabase.child("users").child(mCurrentUserId).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String friendId = snapshot.getKey();
                    mDatabase.child("users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String username = userSnapshot.child("username").getValue(String.class);
                            String imageUrl = userSnapshot.child("image").getValue(String.class);
                            CustomContact contact = new CustomContact(username, imageUrl, friendId);
                            if (contact.getUsername().toLowerCase().contains(s)) {
                                contactsList.add(contact);
                            }
                            contactsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error handling
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error handling
            }
        });
    }

    public void handleReceivedRequestsChange(DataSnapshot dataSnapshot) {
        pendingRequestsList.clear();
        for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
            String requestId = requestSnapshot.getKey();
            mDatabase.child("users").child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    String imageUrl = userSnapshot.child("image").getValue(String.class);
                    CustomContact request = new CustomContact(username, imageUrl, requestId);
                    if (request != null) {
                        pendingRequestsList.add(request);
                        requestsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error handling
                }
            });
        }
        checkIfRequestsEmpty();
    }

    public void handleFriendsListChange(DataSnapshot dataSnapshot) {
        Log.d("ContactsFragment", "Clearing contacts list");
        contactsList.clear();
        cachedFriendsList.clear();
        for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
            String friendId = friendSnapshot.getKey();
            mDatabase.child("users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    String imageUrl = userSnapshot.child("image").getValue(String.class);
                    CustomContact friend = new CustomContact(username, imageUrl, friendId);
                    cachedFriendsList.add(friend);
                    contactsList.add(friend);
                    Log.d("ContactsFragment", "Added friend: " + friend.getUsername());
                    contactsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error handling
                }
            });
        }
    }

    public void onAddFriendClicked(CustomContact contact) {
        String contactId = contact.getUserKey();

        // First remove the request from both receivedRequests and sentRequests
        mDatabase.child("users").child(mCurrentUserId).child("receivedRequests").child(contactId).removeValue();
        mDatabase.child("users").child(mCurrentUserId).child("sentRequests").child(contactId).removeValue();
        mDatabase.child("users").child(contactId).child("receivedRequests").child(mCurrentUserId).removeValue();
        mDatabase.child("users").child(contactId).child("sentRequests").child(mCurrentUserId).removeValue();

        // Then add the users to each other's friends list
        mDatabase.child("users").child(mCurrentUserId).child("friends").child(contactId).setValue(true);
        mDatabase.child("users").child(contactId).child("friends").child(mCurrentUserId).setValue(true);
    }

    private Drawable scaleDrawable(Drawable drawable, int newWidth, int newHeight) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    private void checkIfRequestsEmpty() {
        MaterialButton hidePendingButton = getView().findViewById(R.id.hide_pending_button);
        if (pendingRequestsList.isEmpty()) {
            hidePendingButton.setVisibility(View.INVISIBLE);
        } else {
            hidePendingButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (commonListener != null) {
            mDatabase.child("users").child(mCurrentUserId).child("friends").removeEventListener(commonListener);
            mDatabase.child("users").child(mCurrentUserId).child("receivedRequests").removeEventListener(commonListener);
        }
    }

}
