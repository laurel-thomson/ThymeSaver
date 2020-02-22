package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

public class ImportClient {
    private ValueCallback<Recipe> mRecipeCallback;
    private String mUrl;

    public void importRecipe(String url, ValueCallback<Recipe> recipeCallback) {
        mRecipeCallback = recipeCallback;
        mUrl = url;
        if (!url.contains("http")) {
            mUrl = "http://" + url;
        }
        new getWebpageTask().execute(mUrl);
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
                mRecipeCallback.onError("Could not find recipe.");
                return;
            }
            RecipeWebsiteClient client = getWebsiteClient(doc);
            try {
                Recipe recipe = client.importRecipe(doc);
                recipe.setSourceURL(mUrl.split("http[s]?://")[1].split("/")[0]);
                mRecipeCallback.onSuccess(recipe);
            } catch (Exception e) {
                mRecipeCallback.onError(e.toString());
            }

        }
    }

    private RecipeWebsiteClient getWebsiteClient(Document doc) {
        if (doc.select(".wprm-recipe-ingredient").size() > 0) {
            return new WPRMRecipeClient();
        } else {
            return new RecipeWebsiteClient();
        }
    }
}
