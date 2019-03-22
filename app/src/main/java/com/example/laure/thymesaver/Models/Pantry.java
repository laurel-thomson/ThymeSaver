package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class Pantry {
    private String name;
    private String uId;
    private boolean isMyPantry;
    private boolean isPreferredPantry;

    public Pantry() {}

    public Pantry(String name, String uId, boolean isMyPantry, boolean isPreferredPantry) {
        this.name = name;
        this.uId = uId;
        this.isMyPantry = isMyPantry;
        this.isPreferredPantry = isPreferredPantry;
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

    public boolean isPreferredPantry() {
        return isPreferredPantry;
    }

    public void setPreferredPantry(boolean preferredPantry) {
        isPreferredPantry = preferredPantry;
    }
}
