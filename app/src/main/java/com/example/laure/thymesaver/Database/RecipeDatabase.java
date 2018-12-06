package com.example.laure.thymesaver.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Recipe.class}, version=1)
public abstract class RecipeDatabase extends RoomDatabase {
    public abstract RecipeDao getRecipeDao();

    private static RecipeDatabase mSoleInstance;

    public static RecipeDatabase getDatabase(Context context) {
        if (mSoleInstance == null)
        {
            mSoleInstance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RecipeDatabase.class,
                    "recipe_database")
                .fallbackToDestructiveMigration()
                .build();
        }
        return mSoleInstance;
    }


}
