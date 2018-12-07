package com.example.laure.thymesaver.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface IngredientDao {
    @Insert
    void insertIngredient(Ingredient i);

    @Delete
    void deleteIngredient(Ingredient i);

    @Update
    void updateIngredient(Ingredient i);

    @Query("SELECT * FROM ingredient_table ORDER BY name ASC")
    LiveData<List<Ingredient>> getAllIngredients();
}
