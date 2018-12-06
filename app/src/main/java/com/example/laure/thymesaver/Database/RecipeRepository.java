package com.example.laure.thymesaver.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class RecipeRepository {
    private RecipeDao mRecipeDao;
    private LiveData<List<Recipe>> mAllRecipes;

    public RecipeRepository(Application application) {
        RecipeDatabase db = RecipeDatabase.getDatabase(application);
        mRecipeDao = db.getRecipeDao();
        mAllRecipes = mRecipeDao.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

    public void insertRecipe(Recipe recipe) {
        new InsertAsyncTask(mRecipeDao).execute(recipe);
    }

    private class InsertAsyncTask extends AsyncTask<Recipe,Void,Void> {
        private RecipeDao mRecipeDao;

        InsertAsyncTask(RecipeDao dao) {
            mRecipeDao = dao;
        }

        @Override
        protected Void doInBackground(final Recipe...params) {
            mRecipeDao.insertRecipe(params[0]);
            return  null;
        }
    }
}
