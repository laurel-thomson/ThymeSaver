package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;

public class MinimalistBakerClient extends RecipeWebsiteClient {
    public Recipe importRecipe(Document doc) {
        String recipeName = doc.select(".wprm-recipe-name").text();
        Recipe recipe = new Recipe(recipeName, "Entree");

        String imageURL = doc.select(".wprm-recipe-image img").attr("src");
        recipe.setImageURL(imageURL);
        Elements recipeIngredients = doc.select(".wprm-recipe-ingredient");
        for (Element ri : recipeIngredients) {
            String quantityString = ri.select(".wprm-recipe-ingredient-amount").text();
            if (quantityString.equals("")) continue;
            Double quantity = parseQuantity(quantityString);
            if (quantity == null) continue;
            String unit = ri.select(".wprm-recipe-ingredient-unit").text();
            String ingredient = cleanIngredientName(ri.select(".wprm-recipe-ingredient-name").text());
            RecipeQuantity rq = new RecipeQuantity(unit, quantity);
            recipe.addOrUpdateIngredient(ingredient, rq);
        }

        Elements instructions = doc.select("ol.wprm-recipe-instructions");
        for (Element instruction : instructions) {
            Elements steps = instruction.select(".wprm-recipe-instruction-text");
            for (Element step : steps) {
                String stepText = step.text();
                recipe.addStep(new Step(stepText));
            }
        }
        return recipe;
    }
}
