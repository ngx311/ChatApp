package com.example.thiago.chatapp;

public class UserDetails {
    static String username = "";
    static String password = "";
    static String email = "";
    static String chatWith = "";

    public UserDetails() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public String getUsername() {
        return  username;
    }

    public String getEmail() {
        return  email;
    }
}
