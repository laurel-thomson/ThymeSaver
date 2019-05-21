package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class Follower {
    private String userName;
    private String uID;
    private String email;

    public Follower() {}

    public Follower(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
