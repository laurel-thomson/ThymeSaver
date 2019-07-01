package thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeSteps;

import thomson.laurel.beth.thymesaver.Models.Step;

import java.util.List;

public interface RecipeStepListener {
    void onStepAdded(Step step);
    void onStepDeleted(int position);
    void onStepMoved(List<Step> newSteps);
    void onStepUpdated(Step step, int position);
    void onStepClicked(int position);
}
