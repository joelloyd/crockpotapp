package com.example.crockpot3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper {

    final protected static String DATABASE_NAME = "CPDB"; //define db name as CrockPotDB

    public SQLiteDB(Context context) { //constructor
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //when database is first created run function and create tables
        db.execSQL("CREATE TABLE Ingredients (ID INTEGER, Name TEXT, PRIMARY KEY(ID));"); //create the ingredients table
        db.execSQL("CREATE TABLE Saved_Recipes (ID INTEGER, Title TEXT, ThumbnailUrl TEXT, PRIMARY KEY(ID));");

        db.execSQL("INSERT INTO Ingredients VALUES (0, 'Sugar')"); //add default ingredient table entries
        db.execSQL("INSERT INTO Ingredients VALUES (1, 'Milk')");
        db.execSQL("INSERT INTO Ingredients VALUES (2, 'Flour')");
        db.execSQL("INSERT INTO Ingredients VALUES (3, 'Apple')");
        db.execSQL("INSERT INTO Ingredients VALUES (4, 'Potato')");
        db.execSQL("INSERT INTO Ingredients VALUES (5, 'Blackberries')");
        db.execSQL("INSERT INTO Ingredients VALUES (6, 'Strawberries')");
        db.execSQL("INSERT INTO Ingredients VALUES (7, 'Banana')");
        db.execSQL("INSERT INTO Ingredients VALUES (8, 'Raisins')");
        db.execSQL("INSERT INTO Ingredients VALUES (9, 'Baking Soda')");
        db.execSQL("INSERT INTO Ingredients VALUES (10, 'Egg')");
        db.execSQL("INSERT INTO Ingredients VALUES (11, 'Tomato')");
        db.execSQL("INSERT INTO Ingredients VALUES (12, 'Cinnamon')");
        db.execSQL("INSERT INTO Ingredients VALUES (13, 'Pork')");
        db.execSQL("INSERT INTO Ingredients VALUES (14, 'Chicken')");
        db.execSQL("INSERT INTO Ingredients VALUES (15, 'Beef')");
        db.execSQL("INSERT INTO Ingredients VALUES (16, 'Duck')");
        db.execSQL("INSERT INTO Ingredients VALUES (17, 'Lamb')");
        db.execSQL("INSERT INTO Ingredients VALUES (18, 'Bacon')");
        db.execSQL("INSERT INTO Ingredients VALUES (19, 'Pasta')");
        db.execSQL("INSERT INTO Ingredients VALUES (20, 'Rice')");
        db.execSQL("INSERT INTO Ingredients VALUES (21, 'Cheese')");
        db.execSQL("INSERT INTO Ingredients VALUES (22, 'Pepper')");
        db.execSQL("INSERT INTO Ingredients VALUES (23, 'Salt')");
        db.execSQL("INSERT INTO Ingredients VALUES (24, 'Butter')");
        db.execSQL("INSERT INTO Ingredients VALUES (25, 'Lettuce')");
        db.execSQL("INSERT INTO Ingredients VALUES (26, 'Beans')");
        db.execSQL("INSERT INTO Ingredients VALUES (27, 'Orange')");
        db.execSQL("INSERT INTO Ingredients VALUES (28, 'Ham')");
        db.execSQL("INSERT INTO Ingredients VALUES (29, 'Onion')");
        db.execSQL("INSERT INTO Ingredients VALUES (30, 'Red Onion')");
        db.execSQL("INSERT INTO Ingredients VALUES (31, 'Spring Onion')");
        db.execSQL("INSERT INTO Ingredients VALUES (32, 'Noodles')");
        db.execSQL("INSERT INTO Ingredients VALUES (33, 'Kiwi')");
        db.execSQL("INSERT INTO Ingredients VALUES (34, 'Mango')");
        db.execSQL("INSERT INTO Ingredients VALUES (35, 'Bell Pepper')");
        db.execSQL("INSERT INTO Ingredients VALUES (36, 'Coffee')");
        db.execSQL("INSERT INTO Ingredients VALUES (37, 'Chorizo')");
        db.execSQL("INSERT INTO Ingredients VALUES (38, 'Cream')");
        db.execSQL("INSERT INTO Ingredients VALUES (39, 'Salmon')");
        db.execSQL("INSERT INTO Ingredients VALUES (40, 'Cod')");
        db.execSQL("INSERT INTO Ingredients VALUES (41, 'Chips')");
        db.execSQL("INSERT INTO Ingredients VALUES (42, 'Fries')");
        db.execSQL("INSERT INTO Ingredients VALUES (43, 'Bread')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // handle database upgrade
        if(newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS Ingredients");
            db.execSQL("DROP TABLE IF EXISTS Saved_Recipes");
            onCreate(db);
        }
    }

    public void saveNewRecipe(SQLiteDatabase db, String recipeId, String recipeTitle, String recipeThumbnailUrl){ //save a new recipe to the sqlite db
        int id = Integer.parseInt(recipeId);
        recipeTitle = "\'" + recipeTitle + "\'";
        recipeThumbnailUrl = "\'" + recipeThumbnailUrl + "\'";
        String sql = "INSERT INTO Saved_Recipes VALUES (" + id + ", " + recipeTitle + ", " + recipeThumbnailUrl + ")";
        db.execSQL(sql);
    }

    public void deleteSavedRecipe(SQLiteDatabase db, String recipeId){ //delete a previously saved recipe from the sqlite db
        int id = Integer.parseInt(recipeId);
        String sql = "DELETE FROM Saved_Recipes WHERE ID = " + id;
        db.execSQL(sql);
    }
}
