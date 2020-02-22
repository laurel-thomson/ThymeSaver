package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;

public class RecipeWebsiteClient {
    public Recipe importRecipe(Document doc) {
        String recipeName = doc.select("title").text().split("[.#$\\\\/]")[0];
        Recipe recipe = new Recipe(recipeName, "Entree");

        Elements images = doc.select("[class*=content] [class*=recipe] img");
        if (images.size() == 0) {
            images = doc.select("[class*=content] img[class*=recipe], [class*=content] img[class*=image]");
        }
        if (images.size() == 0) {
            images = doc.select("img[class*=recipe] img[class*=image]");
        }
        if (images.size() == 0) {
            images = doc.select("[class*=content] img");
        }
        if (images.size() == 0) {
            images = doc.select("img");
        }
        String imageURL = images.last().attr("src");
        if (imageURL != null && !imageURL.equals("")) {
            recipe.setImageURL(imageURL);
        }

        Elements recipeIngredients = doc.select("ul[class*='ingredient'] li");
        if (recipeIngredients.size() == 0) {
            recipeIngredients = doc.select("[class*=ingredient] li");
        }
        for (Element ri : recipeIngredients) {
            String ingText = ri.text();

            String quantityString = ingText.split(" ")[0];
            Double quantity = getQuantity(quantityString);

            String unit = getUnit(quantityString, ingText);
            String name = cleanIngredientName(getName(unit, ingText));
            RecipeQuantity rq = new RecipeQuantity(unit, quantity);
            recipe.addOrUpdateIngredient(name, rq);
        }

        Elements instructions = doc.select("ol[class*=instruction], [class*=instruction] ol");
        if (instructions.size() == 0) {
            instructions = doc.select("ol[class*=direction], [class*=direction] ol");
        }
        if (instructions.size() == 0) {
            instructions = doc.select("ul[class*=direction], [class*=direction] ul, ul[class*=instruction], [class*=instruction] ul");
        }
        for (Element instruction : instructions) {
            Elements steps = instruction.select("li");
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
        String ingTextRemoved;
        if (ingText.equals(quantity)) {
            return "";
        }
        else if (ingText.contains(quantity)) {
            ingTextRemoved = ingText.substring(quantity.length() + 1);
        } else {
            ingTextRemoved = ingText;
        }
        if (ingTextRemoved.indexOf(')') == -1) {
            if (ingTextRemoved.split(" ").length > 0) {
                return ingTextRemoved.split(" ")[0];
            }
            return "";
        }
        else {
            if (ingTextRemoved.split("\\) ").length > 0) {
                return ingTextRemoved.split("\\) ")[0] + ")";
            }
            return "";
        }
    }

    private String getName(String unit, String ingText) {
        String[] pieces = ingText.split(Pattern.quote(unit));
        if (pieces.length > 1) {
            return pieces[1].split("[.#$\\[\\]]")[0];
        }
        else {
            return "unknown";
        }
    }
    public Double parseQuantity(String quantity) {
        try {
            if (quantity.indexOf('/') != -1) {
                String[] splitString = quantity.split("/");
                Double first = Double.parseDouble(splitString[0]);
                Double second = Double.parseDouble(splitString[1]);
                return Math.floor(first/second * 100)/100; //truncates to 2 decimal places
            }
            String[] splitString = quantity.split("-");
            return Math.floor(Double.parseDouble(splitString[0]) * 100)/100;
        }
        catch (NumberFormatException e) {
            return 1.0;
        }
    }

    public String cleanIngredientName(String name) {
        return name.split("[.#$\\\\/]")[0];
    }
}
