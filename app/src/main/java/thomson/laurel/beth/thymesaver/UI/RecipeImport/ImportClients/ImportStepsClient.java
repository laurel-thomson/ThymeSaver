package thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import thomson.laurel.beth.thymesaver.Models.Step;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

//TODO: the FindRecipesFragment needs to listen for when a recipe is clicked, use this import steps client to get the steps,
//and launch the FixIngredients activity

public class ImportStepsClient {
    private ValueCallback<List<Step>> mStepsCallback;
    private String mUrl;

    public void importRecipe(String url, ValueCallback<List<Step>> stepsCallback) {
        mStepsCallback = mStepsCallback;
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
                mStepsCallback.onError(e.toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                mStepsCallback.onError(e.toString());
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            if (doc == null) {
                mStepsCallback.onError("Could not find recipe.");
                return;
            }
            try {
                List<Step> steps = getSteps(doc);
                mStepsCallback.onSuccess(steps);
            } catch (Exception e) {
                mStepsCallback.onError(e.toString());
            }

        }
    }

    private List<Step> getSteps(Document doc) {
        List<Step> stepList = new ArrayList<>();
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
                stepList.add(new Step(stepText));
            }
        }
        return stepList;
    }
}
