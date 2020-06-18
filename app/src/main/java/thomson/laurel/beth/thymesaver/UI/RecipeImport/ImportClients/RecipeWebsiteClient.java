package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;
import java.net.URL;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;

public class RecipeWebsiteClient {
    public Recipe importRecipe(Document doc) {
        String recipeName = doc.select("title").text().split("[^a-z A-Z0-9]")[0];
        Recipe recipe = new Recipe(recipeName, "Entree");

        recipe.setImageURL(getImageUrl(doc));

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

    private String getImageUrl(Document doc) {
        String[] cssSelectors = new String[] {
                "[class*=content] [class*=recipe] img:not([class*=svg])",
                "[class*=content] img[class*=recipe], [class*=content] img[class*=image]:not([class*=svg])",
                "img[class*=recipe] img[class*=image]:not([class*=svg])",
                "[class*=content] img:not([class*=svg])",
                "img:not([class*=svg])"
        };
        Elements images;
        for (String selector : cssSelectors) {
            images = doc.select(selector);
            if (images.size() == 0) { continue; }
            for (Element image : images) {
                String imageUrl = image.attr("src");
                if (isValidUrl(imageUrl) && !imageUrl.matches("svg$")) {
                    return imageUrl;
                }
            }
        }
        return null;
    }

    private boolean isValidUrl(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public Double getQuantity(String quantityString) {
        if (quantityString.equals("")) return null;
        return parseQuantity(quantityString);
    }

    public String getUnit(String quantity, String ingText) {
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

    public String getName(String unit, String ingText) {
        String[] pieces = ingText.split(Pattern.quote(unit));
        if (pieces.length > 1) {
            return pieces[1].split("[^a-z A-Z0-9]")[0];
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
        return name.split("[^a-z A-Z0-9]")[0];
    }
}
