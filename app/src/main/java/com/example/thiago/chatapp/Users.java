package com.example.thiago.chatapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Users {
    public String uid, username, inputEmail, inputPassword, gender, age, image;

    public String getname() {
        return username;
    }

    public String getemail() {
        return inputEmail;
    }

    public String getpassword() {
        return inputPassword;
    }

    public String getgender() {
        return gender;
    }

    public String getage() {
        return age;
    }

    public String getimage() {return image; }

    public String getUid() {
        return uid;
    }

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(Users.class)
    }

    public Users(String uid, String username, String inputEmail, String inputPassword, String gender, String age, String image) {
        this.uid = uid;
        this.username = username;
        this.inputEmail = inputEmail;
        this.inputPassword = inputPassword;
        this.gender = gender;
        this.age = age;
        this.image = image;
    }
}
