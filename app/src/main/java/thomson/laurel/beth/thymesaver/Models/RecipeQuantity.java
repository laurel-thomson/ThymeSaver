package thomson.laurel.beth.thymesaver.Models;

public class RecipeQuantity {
    private String unit;
    private double recipeQuantity;
    private String subRecipe;
    private boolean isChecked;

    public RecipeQuantity() {}

    public RecipeQuantity(String unit, double recipeQuantity) {
        this.unit = unit;
        this.recipeQuantity = recipeQuantity;
    }

    public double getRecipeQuantity() {
        return recipeQuantity;
    }

    public void setRecipeQuantity(double recipeQuantity) {
        this.recipeQuantity = recipeQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSubRecipe() {
        return subRecipe;
    }

    public void setSubRecipe(String subRecipe) {
        this.subRecipe = subRecipe;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
