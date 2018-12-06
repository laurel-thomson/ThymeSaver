package com.example.laure.thymesaver.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    void insertRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Update
    void updateRecipe(Recipe recipe);

    @Query("SELECT * FROM recipe_table ORDER BY name ASC")
    LiveData<List<Recipe>> getAllRecipes();
}
