package com.example.laure.thymesaver.UI.Settings;

import com.example.laure.thymesaver.Models.PantryRequest;

import java.io.Serializable;

public class Follower implements Serializable {
    private PantryRequest mAcceptedRequest;

    public Follower(PantryRequest request) {
        mAcceptedRequest = request;
    }

    public String getName() {
        return mAcceptedRequest.getUserName();
    }

}
