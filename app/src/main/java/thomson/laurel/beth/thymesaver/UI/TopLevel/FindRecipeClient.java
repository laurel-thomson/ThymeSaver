package thomson.laurel.beth.thymesaver.UI.TopLevel;

import android.os.AsyncTask;
import android.util.JsonReader;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

public class FindRecipeClient {
    private ValueCallback<List<Recipe>> mRecipesCallback;
    private String APP_IDD = "eda6b7ea";
    private String APP_KEY = "fcddac6702a40b12f38680b862148538";

    public void getRecipes(String query, ValueCallback<List<Recipe>> callback) {
        new GetRecipeTask().execute(query);
        mRecipesCallback = callback;
    }

    private class GetRecipeTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            String query = strings[0];
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(
                        "https://api.edamam.com/search?q=" + query +
                            "&app_id=" + APP_IDD +
                            "&app_key=" + APP_KEY
                );
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                readInput(stream);
            } catch (Exception e) {
                e.printStackTrace();
                mRecipesCallback.onError(e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

    private void readInput(InputStream stream) {
        List<Recipe> recipes = new ArrayList<>();

        JsonReader reader = new JsonReader(new InputStreamReader(stream));
        String recipeName = "";
        String imageUrl = "";
        String sourceUrl = "";

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("hits")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equals("recipe")) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if (name.equals("label")) {
                                        recipeName = reader.nextString();
                                    } else if (name.equals("image")) {
                                        imageUrl = reader.nextString();
                                    } else if (name.equals("url")) {
                                        sourceUrl = reader.nextString();
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                Recipe recipe = new Recipe();
                                recipe.setName(recipeName);
                                recipe.setImageURL(imageUrl);
                                recipe.setSourceURL(sourceUrl);
                                recipes.add(recipe);
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            mRecipesCallback.onError(e.getMessage());
        }
        mRecipesCallback.onSuccess(recipes);
    }
}
