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
        new InsertRecipeAsyncTask(mRecipeDao).execute(recipe);
    }

    private class InsertRecipeAsyncTask extends AsyncTask<Recipe,Void,Void> {
        private RecipeDao mRecipeDao;

        InsertRecipeAsyncTask(RecipeDao dao) {
            mRecipeDao = dao;
        }

        @Override
        protected Void doInBackground(final Recipe...params) {
            mRecipeDao.insertRecipe(params[0]);
            return  null;
        }
    }

    public void insertOrUpdateRecipeIngredient(RecipeIngredient recipeIngredient) {
         new InsertOrUpdateRecipeIngredientAsyncTask
                 (mRecipeDao).execute(recipeIngredient);    }


    private class InsertOrUpdateRecipeIngredientAsyncTask extends AsyncTask<RecipeIngredient,Void,Void> {
        private RecipeDao mRecipeDao;

        InsertOrUpdateRecipeIngredientAsyncTask(RecipeDao dao) {
            mRecipeDao = dao;
        }

        @Override
        protected Void doInBackground(final RecipeIngredient...params) {
            long index = mRecipeDao.insertRecipeIngredient(params[0]);
            if (index == -1) { //insert was unsuccessful
                mRecipeDao.updateRecipeIngredient(params[0]);
            }
            return null;
        }
    }
}
