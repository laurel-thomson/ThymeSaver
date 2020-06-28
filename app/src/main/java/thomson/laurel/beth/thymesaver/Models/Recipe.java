package thomson.laurel.beth.thymesaver.Models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Recipe {
    private String name;

    //for Firebase, keys need to be strings
    private HashMap<String, RecipeQuantity> recipeIngredients = new HashMap<>();

    private List<String> subRecipes = new ArrayList<>();

    private List<Step> steps = new ArrayList<>();

    private List<String> categories = new ArrayList<>();

    private boolean isSubRecipe;

    private String imageURL;

    private String sourceURL;

    private String missingPantryIngredients;

    public Recipe() {
        //required empty constructor for Firebase
    }

    public Recipe(String name) {
        this.name = name;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, RecipeQuantity> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void addOrUpdateIngredient(String ingredientName, RecipeQuantity quantity) {
        recipeIngredients.put(ingredientName, quantity);
    }

    public void setRecipeIngredients(HashMap<String, RecipeQuantity> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public List<String> getCategories() {
        return categories;
    }

    public void addCategory(String category) {
        if (!this.categories.contains(category)) {
            this.categories.add(category);
        }
    }

    public void removeCategory(String category) {
        this.categories.remove(category);
    }

    public boolean isSubRecipe() {
        return isSubRecipe;
    }

    public void setSubRecipe(boolean subRecipe) {
        isSubRecipe = subRecipe;
    }

    public List<String> getSubRecipes() {
        return subRecipes;
    }

    public void setSubRecipes(List<String> subRecipes) {
        this.subRecipes = subRecipes;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    @Exclude
    public String getMissingPantryIngredients() {
        return this.missingPantryIngredients;
    }

    public void setMissingPantryIngredients(String missingText) {
        this.missingPantryIngredients = missingText;
    }

}
