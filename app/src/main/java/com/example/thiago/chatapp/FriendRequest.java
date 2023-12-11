package com.example.thiago.chatapp;

public class FriendRequest {
    private String userId;
    private String friendName;
    private String friendPicture;

    public FriendRequest(String userId, String friendName, String friendPicture) {
        this.userId = userId;
        this.friendName = friendName;
        this.friendPicture = friendPicture;
    }

    public String getUserId() {
        return userId;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendPicture() {
        return friendPicture;
    }
}


