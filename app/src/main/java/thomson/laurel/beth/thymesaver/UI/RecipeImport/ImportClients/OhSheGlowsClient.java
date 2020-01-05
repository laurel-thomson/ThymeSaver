package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;

public class OhSheGlowsClient extends RecipeWebsiteClient {
    public Recipe importRecipe(Document doc) {
        String recipeName = doc.select(".recipe-title").text();
        Recipe recipe = new Recipe(recipeName, "Entree");

        String imageURL = doc.select(".entry-content img").first().attr("src");
        recipe.setImageURL(imageURL);
        Elements recipeIngredients = doc.select("ul.ingredients li span");
        for (Element ri : recipeIngredients) {
            String ingText = ri.text();

            String quantityString = ingText.split(" ")[0];
            Double quantity = getQuantity(quantityString);
            if (quantity == null) continue;

            String unit = getUnit(quantityString, ingText);
            String name = getName(unit, ingText);
            RecipeQuantity rq = new RecipeQuantity(unit, quantity);
            recipe.addOrUpdateIngredient(name, rq);
        }

        Elements instructions = doc.select(".instructions ol");
        for (Element instruction : instructions) {
            Elements steps = instruction.select("li.instruction");
            for (Element step : steps) {
                String stepText = step.text();
                recipe.addStep(new Step(stepText));
            }
        }
        return recipe;
    }

    private Double getQuantity(String quantityString) {
        if (quantityString.equals("")) return null;
        return parseQuantity(quantityString);
    }

    private String getUnit(String quantity, String ingText) {
        String ingTextRemoved = ingText.substring(quantity.length() + 1);
        if (ingTextRemoved.indexOf(')') == -1) {
            return ingTextRemoved.split(" ")[1];
        }
        else {
            return ingTextRemoved.split("\\) ")[0] + ")";
        }
    }

    private String getName(String unit, String ingText) {
        return ingText.split(Pattern.quote(unit))[1].split("/")[0];
    }
}
