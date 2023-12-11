package com.example.thiago.chatapp;

public class AddFriendModel {
    private String username2;
    private String pictureURL;

    public AddFriendModel () {

    }

    public AddFriendModel (String username2, String pictureURL) {
        this.username2 = username2;
        this.pictureURL = pictureURL;

    }

    //getters
    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getPictureURL() {
        return getPictureURL();
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
}
