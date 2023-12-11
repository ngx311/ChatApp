package com.example.thiago.chatapp;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thiago.chatapp.R;
import com.example.thiago.chatapp.CustomContact;
import com.google.firebase.database.DataSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class UIUtils {

    public static void updateUIBasedOnFriendship(View itemView, CustomContact contact, DataSnapshot receivedRequestsSnapshot) {
        ImageView imageAddFriend = itemView.findViewById(R.id.image_addfriend);
        CircleImageView profilePicture = itemView.findViewById(R.id.find_profile_picture);

        if (contact.isSentRequest()) {
            TextView sendRequest = itemView.findViewById(R.id.send_request);
            sendRequest.setText("Cancel Request");
            imageAddFriend.setImageResource(R.drawable.red_square_close_x_button_icon_transparent_background);
            imageAddFriend.setClickable(true);
            profilePicture.setBorderColor(Color.parseColor("#30df3b")); // Default border color or any color you want
        } else if (contact.isFriend()) {
            TextView sendRequest = itemView.findViewById(R.id.send_request);
            sendRequest.setText("Message");
            imageAddFriend.setImageResource(R.drawable.messagebox);
            imageAddFriend.setClickable(false);
            profilePicture.setBorderColor(Color.parseColor("#38c9ff")); // Change border color when friends
        } else {
            TextView sendRequest = itemView.findViewById(R.id.send_request);
            if (receivedRequestsSnapshot != null && receivedRequestsSnapshot.hasChild(contact.getUserKey())) {
                sendRequest.setText("Accept Request");
                imageAddFriend.setImageResource(R.drawable.friends);
                imageAddFriend.setClickable(true);
                profilePicture.setBorderColor(Color.parseColor("#30df3b")); // Default border color or any color you want
            } else {
                sendRequest.setText("Send Request");
                imageAddFriend.setImageResource(R.drawable.add_friend_icon_final2);
                imageAddFriend.setClickable(true);
                profilePicture.setBorderColor(Color.parseColor("#30df3b")); // Default border color or any color you want
            }
        }
    }
}
