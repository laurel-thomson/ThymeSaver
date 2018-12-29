package com.example.laure.thymesaver.Models;

import java.util.Date;

public class MealPlan {
    private String recipeName;
    private Date scheduledDate;

    public MealPlan() {
        //required empty constructor for Firebase
    }

    public MealPlan(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
