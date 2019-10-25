package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class ImportClient {

    public void importRecipe() {

        new getWebpageTask().execute("https://minimalistbaker.com/1-pot-chickpea-tomato-peanut-stew-west-african-inspired/");
    }

    private class getWebpageTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... url) {
            Document doc = null;
            try {
                doc = Jsoup.connect(url[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            Elements recipeIngredients = doc.select(".wprm-recipe-ingredient");
            for (Element ri : recipeIngredients) {
                String quantity = ri.select(".wprm-recipe-ingredient-amount").text();
                String unit = ri.select(".wprm-recipe-ingredient-unit").text();
                String ingredient = ri.select(".wprm-recipe-ingredient-name").text();
                Log.i("mytag", "quantity: " + quantity + " unit: " + unit + " ingredient: " + ingredient);
            }
        }
    }
}
