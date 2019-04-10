package com.example.laure.thymesaver.UI.RecipeDetail;

import java.util.List;

public interface RecipeStepListener {
    void onStepAdded(String step);
    void onStepDeleted(int position);
    void onStepMoved(List<String> newList);
}
