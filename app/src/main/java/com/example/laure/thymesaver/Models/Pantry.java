package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class Pantry {
    private String name;
    private String uId;
    private boolean isMyPantry;

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
}
