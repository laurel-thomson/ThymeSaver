package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import thomson.laurel.beth.thymesaver.R;

public class ImportActivity extends AppCompatActivity {
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        setUpActionBar();
    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Import Recipe");
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

}
