package com.example.thiago.chatapp;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class ChatActivity extends Activity {

    private ChatModel chatMessageModel;


    public class ChatModel {
        public String chatID = "";
        public String messageID = "";
        public String senderID = "";
        public String userMessage = "";
        public String messageTYPE = "";
        public String medialURL = "";
        public long timestamp;

        @Exclude
        public String localMedialUrl = "";

        @Exclude
        public int id = 0;

        @Exclude
        public long BlockTime = 0;

        public ChatModel(){
        }

        public java.util.Map<String, String> getTimeStamp() { return ServerValue.TIMESTAMP; }
        public void setTimeStamp(long timestamp) { this.timestamp = timestamp; }

        @Exclude
        public long getTimestampLong() { return timestamp; }

        //getters
        public String messageId() { return messageID; }
    }
}
