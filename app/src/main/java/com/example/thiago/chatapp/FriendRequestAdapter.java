package com.example.thiago.chatapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CustomContact> contactList;
    private LayoutInflater mInflater;
    private OnItemClickListener listener;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = mDatabase.getReference("users");
    private String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    DatabaseReference currentUserRef = usersRef.child(currentUserID);
    DatabaseReference receivedRequestsRef = currentUserRef.child("receivedRequests");
    DatabaseReference sentRequestsRef = currentUserRef.child("sentRequests");

    private ValueEventListener receivedRequestsListener;
    private ValueEventListener sentRequestsListener;

    public interface OnItemClickListener {
        void onAddFriendClicked(CustomContact contact);
    }

    public FriendRequestAdapter(Context context, ArrayList<CustomContact> contactList, OnItemClickListener listener) {
        this.context = context;
        this.contactList = contactList != null ? contactList : new ArrayList<>();
        this.mInflater = LayoutInflater.from(context);
        this.listener = listener;

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (receivedRequestsListener == null) {
            receivedRequestsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Handle received requests data change
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            };
            receivedRequestsRef.addValueEventListener(receivedRequestsListener);
        }

        if (sentRequestsListener == null) {
            sentRequestsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Handle sent requests data change
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            };
            sentRequestsRef.addValueEventListener(sentRequestsListener);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseReference currentUserRef = mDatabase.getReference("users").child(currentUserID);
        currentUserRef.child("receivedRequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userKey = snapshot.getKey();
                    String username = snapshot.getValue(String.class);
                    CustomContact contact = new CustomContact(username, null, userKey);
                    int currentPosition = holder.getAdapterPosition();
                    if (currentPosition == RecyclerView.NO_POSITION) {
                        // This can happen if the ViewHolder was removed from the RecyclerView.
                        // In this case, simply return to skip the update.
                        return;
                    }
                    contactList.set(currentPosition, contact);

                    holder.usernameTextView.setText(contact.getUsername());
                    Glide.with(context)
                            .load(contact.getImageURL())
                            .placeholder(R.drawable.placeholder_image)
                            .into(holder.profilePicture);

                    holder.imageAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acceptFriendRequest(currentUserID, contact.getUserKey(), username, contact.getUsername());
                        }
                    });

                    holder.imageDenyFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rejectFriendRequest(currentUserID, contact.getUserKey());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FriendRequestAdapter", "Data loading cancelled/error: " + databaseError.getMessage());
            }
        });
    }









    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePicture;
        TextView usernameTextView;
        TextView sendRequest;
        ImageView imageAddFriend, imageDenyFriend;

        ViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            sendRequest = itemView.findViewById(R.id.send_request);
            imageAddFriend = itemView.findViewById(R.id.image_addfriend);
            imageDenyFriend = itemView.findViewById(R.id.image_denyfriend);
        }
    }

    private void rejectFriendRequest(String currentUserID, String targetUserID) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(currentUserID).child("receivedRequests").child(targetUserID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("rejectFriendRequest", "Could not remove received request: " + databaseError.getMessage());
                } else {
                    mDatabase.child("users").child(targetUserID).child("sentRequests").child(currentUserID).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("rejectFriendRequest", "Could not remove sent request: " + databaseError.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void acceptFriendRequest(String currentUserID, String targetUserID, String currentUserName, String senderUserName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(currentUserID).child("receivedRequests").child(targetUserID).removeValue((databaseError, databaseReference) -> {
            if (databaseError != null) {
                Log.d("acceptFriendRequest", "Could not remove received request: " + databaseError.getMessage());
            } else {
                mDatabase.child("users").child(targetUserID).child("sentRequests").child(currentUserID).removeValue((databaseError1, databaseReference1) -> {
                    if (databaseError1 != null) {
                        Log.d("acceptFriendRequest", "Could not remove sent request: " + databaseError1.getMessage());
                    } else {
                        mDatabase.child("users").child(currentUserID).child("friends").child(targetUserID).setValue(true);
                        mDatabase.child("users").child(targetUserID).child("friends").child(currentUserID).setValue(true);
                    }
                });
            }
        });
    }

    private void checkIfRequestsEmpty() {
        if (contactList.isEmpty()) {
            MaterialButton hidePendingButton = ((Activity) context).findViewById(R.id.hide_pending_button);
            hidePendingButton.setVisibility(View.INVISIBLE);
        }
    }


}