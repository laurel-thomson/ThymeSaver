package com.example.laure.thymesaver.UI.Settings;

import com.example.laure.thymesaver.Models.Pantry;

import java.io.Serializable;
import java.util.List;

public class PantryListItem implements Serializable {
    private Pantry mPantry;
    private List<Follower> mFollowers;

    public PantryListItem(Pantry pantry, List<Follower> followers) {
        mPantry = pantry;
        mFollowers = followers;
    }

    public String getName() {
        return mPantry.getName();
    }

    public String getuId() {
        return mPantry.getuId();
    }

    public boolean isMyPantry() {
        return mPantry.isMyPantry();
    }

    public List<Follower> getFollowers() {
        return mFollowers;
    }

    public Pantry getUnderlyingPantry() {
        return mPantry;
    }
}
