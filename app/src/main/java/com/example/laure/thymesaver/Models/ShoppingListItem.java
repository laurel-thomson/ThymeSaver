package com.example.laure.thymesaver.Models;

public class ShoppingListItem {
    private String name;
    private int quantity;

    public ShoppingListItem() {};

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
