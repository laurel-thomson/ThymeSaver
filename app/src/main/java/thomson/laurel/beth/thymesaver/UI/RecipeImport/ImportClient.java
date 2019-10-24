package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class ImportClient {

    public void importRecipe() {

        new getWebpageTask().execute("http://en.wikipedia.org");
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
            Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
            }
        }
    }
}
