package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class ShoppingListMod {
    private String name;
    private int quantity;

    public ShoppingListMod() {}

    public ShoppingListMod(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
