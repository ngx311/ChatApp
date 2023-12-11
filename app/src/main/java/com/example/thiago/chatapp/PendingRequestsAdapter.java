package com.example.thiago.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PendingRequestsAdapter extends ArrayAdapter<FriendRequest> {

    private Context mContext;
    private int mResource;
    private ArrayList<FriendRequest> mPendingRequestsList;
    private String mCurrentUserId;  // The ID of the current user

    public PendingRequestsAdapter(Context context, int resource, ArrayList<FriendRequest> pendingRequestsList, String currentUserId) {
        super(context, resource, pendingRequestsList != null ? pendingRequestsList : new ArrayList<>());
        mContext = context;
        mResource = resource;
        mPendingRequestsList = pendingRequestsList != null ? pendingRequestsList : new ArrayList<>();
        mCurrentUserId = currentUserId;  // Assign the user id passed into the constructor to the member variable
    }

    @Override
    public int getCount() {
        return mPendingRequestsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        // Get the current friend request
        FriendRequest friendRequest = mPendingRequestsList.get(position);

        // Set the friend's name
        TextView usernameTextView = convertView.findViewById(R.id.username);
        usernameTextView.setText(friendRequest.getFriendName());

        // Set the friend's profile picture
        ImageView profileImageView = convertView.findViewById(R.id.profile_image);
        Picasso.get().load(friendRequest.getFriendPicture()).placeholder(R.drawable.profile_placeholder).into(profileImageView);

        // Set click listeners for accept and deny buttons
        Button acceptButton = convertView.findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get a reference to the friends database
                DatabaseReference friendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");

                // Add each other to friends in the database
                friendsDatabase.child(mCurrentUserId).child(friendRequest.getUserId()).setValue(true);
                friendsDatabase.child(friendRequest.getUserId()).child(mCurrentUserId).setValue(true);

                // Remove the friend request from the friend requests database
                DatabaseReference friendRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests");
                friendRequestsDatabase.child(mCurrentUserId).child(friendRequest.getUserId()).removeValue();
                friendRequestsDatabase.child(friendRequest.getUserId()).child(mCurrentUserId).removeValue();

                // Remove the friend request from the local list and update the UI
                mPendingRequestsList.remove(position);
                notifyDataSetChanged();
            }
        });

        Button denyButton = convertView.findViewById(R.id.deny_button);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the friend request from the friend requests database
                DatabaseReference friendRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests");
                friendRequestsDatabase.child(mCurrentUserId).child(friendRequest.getUserId()).removeValue();
                friendRequestsDatabase.child(friendRequest.getUserId()).child(mCurrentUserId).removeValue();

                // Remove the friend request from the local list and update the UI
                mPendingRequestsList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
