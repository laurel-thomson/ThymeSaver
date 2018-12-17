package com.example.laure.thymesaver.Models;

public class MeasuredIngredient extends Ingredient {
    private int mMeasuredQuantity;

    public MeasuredIngredient(Ingredient i, int quantity) {
        super(i.getName(), i.getUnit(), i.getPantryQuantity());
        mMeasuredQuantity = quantity;
    }

    public int getMeasuredQuantity() {
        return mMeasuredQuantity;
    }

    public void setMeasuredQuantity(int measuredQuantity) {
        mMeasuredQuantity = measuredQuantity;
    }
}
