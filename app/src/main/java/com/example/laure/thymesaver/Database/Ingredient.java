package com.example.laure.thymesaver.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ingredient_table")
public class Ingredient {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    public Ingredient(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {mName = name;}
}
