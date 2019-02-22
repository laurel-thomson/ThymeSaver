package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class Ingredient {
    private String name;
    private boolean isBulk;
    private String category;
    private int quantity;

    public Ingredient() {
        //required empty constructor for Firebase
    }

    public Ingredient(String name, String category, boolean isBulk) {
        this.name = name;
        this.category = category;
        this.isBulk = isBulk;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isBulk() {
        return isBulk;
    }

    public void setBulk(boolean bulk) {
        isBulk = bulk;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
