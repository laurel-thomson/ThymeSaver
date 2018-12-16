package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class Ingredient {
    private String name;
    private String unit;
    private int pantryQuantity;

    public Ingredient() {
        //required empty constructor for Firebase
    }

    public Ingredient(String name, String unit, int pantryQuantity) {
        this.name = name;
        this.unit = unit;
        this.pantryQuantity = pantryQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getPantryQuantity() {
        return pantryQuantity;
    }

    public void setPantryQuantity(int pantryQuantity) {
        this.pantryQuantity = pantryQuantity;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
