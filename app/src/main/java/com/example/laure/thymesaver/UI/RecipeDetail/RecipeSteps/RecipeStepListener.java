package com.example.laure.thymesaver.UI.RecipeDetail.RecipeSteps;

import java.util.List;

public interface RecipeStepListener {
    void onStepAdded(String step);
    void onStepDeleted(int position);
    void onStepMoved(List<String> newList);
    void onStepUpdated(String step, int position);
    void onStepClicked(int position);
}