package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pantry implements Serializable{
    private String name;
    private String uId;
    private boolean isMyPantry;
    private List<Follower> followers = new ArrayList<>();

    public Pantry() {}

    public Pantry(String name, boolean isMyPantry) {
        this.name = name;
        this.isMyPantry = isMyPantry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public boolean isMyPantry() {
        return isMyPantry;
    }

    public void setMyPantry(boolean myPantry) {
        isMyPantry = myPantry;
    }

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }
}
