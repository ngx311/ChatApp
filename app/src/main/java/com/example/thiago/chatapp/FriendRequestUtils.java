package com.example.thiago.chatapp;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendRequestUtils {

    public static void acceptFriendRequest(String currentUserID, String targetUserID) {
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
}
