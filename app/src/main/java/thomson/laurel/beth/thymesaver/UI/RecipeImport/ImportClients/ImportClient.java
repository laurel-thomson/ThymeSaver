package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import thomson.laurel.beth.thymesaver.Models.Recipe;
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
                mRecipeCallback.onError("Could not find recipe.");
                return;
            }
            RecipeWebsiteClient client = getWebsiteClient(doc);
            try {
                mRecipeCallback.onSuccess(client.importRecipe(doc));
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
