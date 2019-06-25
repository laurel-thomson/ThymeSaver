package com.example.laure.thymesaver.UI.RecipeDetail.RecipeSteps;

import com.example.laure.thymesaver.Models.Step;

import java.util.List;

public interface RecipeStepListener {
    void onStepAdded(Step step);
    void onStepDeleted(int position);
    void onStepMoved();
    void onStepUpdated(Step step, int position);
    void onStepClicked(int position);
}
