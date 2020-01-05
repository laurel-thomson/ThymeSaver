package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import org.jsoup.nodes.Document;

import thomson.laurel.beth.thymesaver.Models.Recipe;

public class RecipeWebsiteClient {
    public Recipe importRecipe(Document document) {
     return null;
    }

    public Double parseQuantity(String quantity) {
        try {
            if (quantity.indexOf('/') != -1) {
                String[] splitString = quantity.split("/");
                Double first = Double.parseDouble(splitString[0]);
                Double second = Double.parseDouble(splitString[1]);
                return first/second;
            }
            String[] splitString = quantity.split("-");
            return Double.parseDouble(splitString[0]);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public String cleanIngredientName(String name) {
        return name.split("/")[0];
    }
}
