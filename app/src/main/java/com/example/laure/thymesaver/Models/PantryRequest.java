package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class PantryRequest {
    private String userName;
    private String uID;
    private String email;

    public PantryRequest() {}

    public PantryRequest(String userName, String email) {
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

    @Exclude
    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
