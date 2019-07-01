package thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeIngredients;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;

public interface AddRecipeIngredientListener {
    void onIngredientAddedOrUpdated(Ingredient ing, RecipeQuantity quantity);
    void onDeleteClicked(Ingredient i, RecipeQuantity quantity);
    void onIngredientClicked(Ingredient i, RecipeQuantity quantity);
    void onIngredientChecked(String parentRecipe, String ingName, RecipeQuantity quantity);
    void onIngredientLongClicked(Ingredient i);
    void onSubRecipeDeleteClicked(String subRecipeName);
    void onSubRecipeClicked(String subRecipeName);
}
