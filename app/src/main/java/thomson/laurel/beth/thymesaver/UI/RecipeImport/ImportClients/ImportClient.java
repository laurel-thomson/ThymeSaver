package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

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
    private RecipeWebsiteClient mRecipeWebsiteClient;

    public void importRecipe(String url, ValueCallback<Recipe> recipeCallback) {
        mRecipeCallback = recipeCallback;
        mRecipeWebsiteClient = getWebsiteClient(url);
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
            mRecipeCallback.onSuccess(mRecipeWebsiteClient.importRecipe(doc));
        }
    }

    private RecipeWebsiteClient getWebsiteClient(String url) {
        if (url.contains("minimalistbaker")) {
            return new MinimalistBakerClient();
        }
        else {
            return new RecipeWebsiteClient();
        }
    }
}
