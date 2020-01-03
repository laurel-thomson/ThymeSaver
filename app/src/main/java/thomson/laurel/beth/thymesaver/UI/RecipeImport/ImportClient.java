package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

public class ImportClient {
    private ValueCallback<Recipe> mRecipeCallback;

    public void importRecipe(String url, ValueCallback<Recipe> recipeCallback) {
        mRecipeCallback = recipeCallback;
        new getWebpageTask().execute(url);
    }

    private class getWebpageTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... url) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
                mRecipeCallback.onError(e.toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                mRecipeCallback.onError(e.toString());
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            if (doc == null) {
                mRecipeCallback.onError("Could not find.");
                return;
            }
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
            mRecipeCallback.onSuccess(recipe);
        }

        private Double parseQuantity(String quantity) {
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

        private String cleanIngredientName(String name) {
            return name.split("/")[0];
        }
    }
}
