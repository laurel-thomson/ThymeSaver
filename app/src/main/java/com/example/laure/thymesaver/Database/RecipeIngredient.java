package com.example.laure.thymesaver.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "recipe_ingredient_table",
        foreignKeys =
                {
                @ForeignKey(entity = Recipe.class,
                    parentColumns = "name",
                    childColumns = "recipe_name",
                    onDelete = CASCADE),
                @ForeignKey(entity = Ingredient.class,
                    parentColumns = "name",
                    childColumns = "ingredient_name",
                    onDelete = CASCADE)
                })
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_ingredient_id")
    public int id;

    @ColumnInfo(name="recipe_name")
    public String mRecipeName;

    @ColumnInfo(name="ingredient_name")
    public String mIngredientName;

    @ColumnInfo(name="quantity")
    public int mQuantity;

    public RecipeIngredient(String recipeName, String ingredientName, int quantity) {
        mRecipeName = recipeName;
        mIngredientName = ingredientName;
        mQuantity = quantity;
    }
}
