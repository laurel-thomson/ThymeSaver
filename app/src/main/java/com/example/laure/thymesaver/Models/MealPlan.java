package com.example.laure.thymesaver.Models;

import java.util.Date;

public class MealPlan {
    private String recipeName;
    private String scheduledDay;

    public MealPlan() {
        //required empty constructor for Firebase
    }

    public MealPlan(String recipeName, String scheduledDay) {

        this.recipeName = recipeName;
        this.scheduledDay = scheduledDay;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getScheduledDay() {
        return scheduledDay;
    }

    public void setScheduledDate(String day) {
        this.scheduledDay = day;
    }
}
