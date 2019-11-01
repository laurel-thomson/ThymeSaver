package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import thomson.laurel.beth.thymesaver.Models.Recipe;

public class ImportedRecipe {
    private static ImportedRecipe sSoleInstance;
    private Recipe mRecipe;

    public static ImportedRecipe getInstance() {
        if (sSoleInstance != null) {
            return sSoleInstance;
        }
        else {
            sSoleInstance = new ImportedRecipe();
            return sSoleInstance;
        }
    }

    private ImportedRecipe() { }

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public Recipe getRecipe() {
        return mRecipe;
    }
}
