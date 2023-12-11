package com.example.thiago.chatapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class CustomContact implements Parcelable {

    private String username;
    private String imageURL;
    private String userKey;
    private String senderUsername;  // Added new field
    private boolean sentRequest;
    private boolean receivedRequest;
    private boolean isFriend;

    // No-argument constructor
    public CustomContact() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomContact that = (CustomContact) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(imageURL, that.imageURL) &&
                Objects.equals(userKey, that.userKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, imageURL, userKey);
    }

    public CustomContact(String username, String imageURL, String userKey) {
        this.username = username;
        this.imageURL = imageURL;
        this.userKey = userKey;
        this.sentRequest = false;
        this.receivedRequest = false;
        this.isFriend = false;
    }

    public CustomContact(String username, String imageURL, String userKey, boolean sentRequest) {
        this.username = username;
        this.imageURL = imageURL;
        this.userKey = userKey;
        this.sentRequest = sentRequest;
        this.receivedRequest = false;
        this.isFriend = false;
    }

    public CustomContact(String username, String imageURL, String userKey, boolean sentRequest, boolean isFriend) {
        this.username = username;
        this.imageURL = imageURL;
        this.userKey = userKey;
        this.sentRequest = sentRequest;
        this.receivedRequest = false;
        this.isFriend = isFriend;
    }

    public CustomContact(String username, String imageURL, String userKey, String senderUsername, boolean sentRequest, boolean receivedRequest, boolean isFriend) {
        this.username = username;
        this.imageURL = imageURL;
        this.userKey = userKey;
        this.senderUsername = senderUsername;  // Added new field
        this.sentRequest = sentRequest;
        this.receivedRequest = receivedRequest;
        this.isFriend = isFriend;
    }

    protected CustomContact(Parcel in) {
        username = in.readString();
        imageURL = in.readString();
        userKey = in.readString();
        senderUsername = in.readString();  // Added new field
        sentRequest = in.readByte() != 0;
        receivedRequest = in.readByte() != 0;
        isFriend = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(imageURL);
        dest.writeString(userKey);
        dest.writeString(senderUsername);  // Added new field
        dest.writeByte((byte) (sentRequest ? 1 : 0));
        dest.writeByte((byte) (receivedRequest ? 1 : 0));
        dest.writeByte((byte) (isFriend ? 1 : 0));
    }

    public Users toUser() {
        return new Users(userKey, username, null, null, null, null, imageURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomContact> CREATOR = new Creator<CustomContact>() {
        @Override
        public CustomContact createFromParcel(Parcel in) {
            return new CustomContact(in);
        }

        @Override
        public CustomContact[] newArray(int size) {
            return new CustomContact[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getSenderUsername() {
        return senderUsername;  // Added new getter
    }

    public boolean isSentRequest() {
        return sentRequest;
    }

    public void setSentRequest(boolean sentRequest) {
        this.sentRequest = sentRequest;
    }

    public boolean isReceivedRequest() {
        return receivedRequest;
    }

    public void setReceivedRequest(boolean receivedRequest) {
        this.receivedRequest = receivedRequest;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
