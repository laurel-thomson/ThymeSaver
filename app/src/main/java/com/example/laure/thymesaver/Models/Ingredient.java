package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class Ingredient {
    private String name;
    private boolean isBulk;
    private String category;
    private String unit;
    private int quantity;

    public Ingredient() {
        //required empty constructor for Firebase
    }

    public Ingredient(String name, String category, boolean isBulk, String unit) {
        this.name = name;
        this.category = category;
        this.isBulk = isBulk;
        this.unit = unit;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Ingredient)) {
            return false;
        }

        Ingredient i = (Ingredient) o;

        return name.equals(i.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode();
    }
}
