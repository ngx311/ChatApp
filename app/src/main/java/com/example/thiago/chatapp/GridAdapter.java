package com.example.thiago.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CustomContact> contactList;

    private ItemClickListener mClickListener;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    public GridAdapter(Context context, ArrayList<CustomContact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_find_hits, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomContact contact = contactList.get(position);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.usernameTextView.setText(contact.getUsername());
        Glide.with(context)
                .load(contact.getImageURL())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.profilePicture);

        holder.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GridAdapter", "User Key: " + contact.getUserKey());
                Intent intent = new Intent(context, SearchedUserProfile.class);
                intent.putExtra("contact", contact); // Pass the CustomContact object
                context.startActivity(intent);
            }
        });

        DatabaseReference usersRef = mDatabase.getReference("users");

        usersRef.child(currentUserID).child("sentRequests").child(contact.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    contact.setSentRequest(true);
                    contact.setReceivedRequest(false);
                    contact.setFriend(false);
                } else {
                    usersRef.child(currentUserID).child("receivedRequests").child(contact.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                contact.setSentRequest(false);
                                contact.setReceivedRequest(true);
                                contact.setFriend(false);
                            } else {
                                usersRef.child(currentUserID).child("friends").child(contact.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            contact.setSentRequest(false);
                                            contact.setReceivedRequest(false);
                                            contact.setFriend(true);
                                        } else {
                                            contact.setSentRequest(false);
                                            contact.setReceivedRequest(false);
                                            contact.setFriend(false);
                                        }
                                        updateUIBasedOnFriendship(holder, contact);
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
                updateUIBasedOnFriendship(holder, contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.d("GridAdapter", "Data loading cancelled/error: " + databaseError.getMessage());
            }

        });

        holder.imageAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference currentUserRef = mDatabase.getReference("users").child(currentUserID);
                currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Users currentUser = dataSnapshot.getValue(Users.class);
                            String currentUsername = currentUser.getname();

                            if (contact.isReceivedRequest()) {
                                // Accept friend request
                                acceptFriendRequest(currentUserID, contact.getUserKey(), currentUsername, contact.getSenderUsername());
                                contact.setFriend(true);
                                contact.setSentRequest(false);
                                contact.setReceivedRequest(false);
                            } else if (contact.isSentRequest()) {
                                // Cancel friend request
                                cancelFriendRequest(currentUserID, contact.getUserKey());
                                contact.setFriend(false);
                                contact.setSentRequest(false);
                                contact.setReceivedRequest(false);
                            } else if (contact.isFriend()) {
                                // Reject friend request
                                rejectFriendRequest(currentUserID, contact.getUserKey());
                                contact.setFriend(false);
                                contact.setSentRequest(false);
                                contact.setReceivedRequest(false);
                            } else {
                                // Send friend request
                                sendFriendRequest(currentUserID, contact.getUserKey(), currentUsername);
                                contact.setFriend(false);
                                contact.setSentRequest(true);
                                contact.setReceivedRequest(false);
                            }

                            updateUIBasedOnFriendship(holder, contact);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Error handling
                    }
                });
            }
        });
    }

    private void updateUIBasedOnFriendship(ViewHolder holder, CustomContact contact) {
        CircleImageView profilePicture = holder.itemView.findViewById(R.id.find_profile_picture);
        if (contact.isSentRequest()) {
            holder.sendRequest.setText("Cancel Request");
            holder.imageAddFriend.setImageResource(R.drawable.red_square_close_x_button_icon_transparent_background);
            holder.imageAddFriend.setClickable(true);
            profilePicture.setBorderColor(Color.parseColor("#30df3b")); // Default border color or any color you want
        } else if (contact.isFriend()) {
            holder.sendRequest.setText("Message");
            holder.imageAddFriend.setImageResource(R.drawable.messagebox);
            holder.imageAddFriend.setClickable(false);
            profilePicture.setBorderColor(Color.parseColor("#38c9ff")); // Change border color when friends
        } else {
            holder.sendRequest.setText(contact.isReceivedRequest() ? "Accept Request" : "Send Request");
            holder.imageAddFriend.setImageResource(contact.isReceivedRequest() ? R.drawable.friends : R.drawable.add_friend_icon_final2);
            holder.imageAddFriend.setClickable(true);
            profilePicture.setBorderColor(Color.parseColor("#30df3b")); // Default border color or any color you want
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView usernameTextView;
        TextView sendRequest;
        ImageView imageAddFriend;

        ViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.find_profile_picture);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            sendRequest = itemView.findViewById(R.id.send_request);
            imageAddFriend = itemView.findViewById(R.id.image_addfriend);
        }
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


    private void cancelFriendRequest(String currentUserID, String targetUserID) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(currentUserID).child("sentRequests").child(targetUserID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("cancelFriendRequest", "Could not remove sent request: " + databaseError.getMessage());
                } else {
                    mDatabase.child("users").child(targetUserID).child("receivedRequests").child(currentUserID).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("cancelFriendRequest", "Could not remove received request: " + databaseError.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendFriendRequest(String currentUserID, String targetUserID, String currentUserName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(currentUserID).child("sentRequests").child(targetUserID).setValue(currentUserName, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("sendFriendRequest", "Could not send friend request: " + databaseError.getMessage());
                } else {
                    mDatabase.child("users").child(targetUserID).child("receivedRequests").child(currentUserID).setValue(currentUserName, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("sendFriendRequest", "Could not record received friend request: " + databaseError.getMessage());
                            }
                        }
                    });
                }
            }
        });
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onSendFriendRequestClick(View view, int position);
    }
}