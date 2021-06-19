package com.example.crockpot3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crockpot3.database.SQLiteDB;
import com.example.crockpot3.lists.Recipe;
import com.example.crockpot3.lists.RecipeAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnItemClickListener { // Activity for displaying recipes fetched from API call done by user request
    private ArrayList<Recipe> newRecipeList = new ArrayList<>();
    private RecipeAdapter adapter = new RecipeAdapter(newRecipeList, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_recipes_page);

        RecyclerView recycler_view = findViewById(R.id.recycler_view_recipes);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);

        ArrayList<Recipe> recipeList = MainActivity.recipesToPass;

        for(int index = 0; index < recipeList.size(); index++){ // filling the recyclerview with the recipes
            newRecipeList.add(recipeList.get(index));
            adapter.notifyItemInserted(index);
        }
    }

    @Override
    protected void onDestroy(){ //clears the existing list of recipes so that when the activity is next called the old recipes are no longer there.
        super.onDestroy();
        int size = newRecipeList.size();
        newRecipeList.clear();
        MainActivity.recipesToPass.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }


    @Override
    public void onItemClick(int position) { //handle clicking the recipe card
        Recipe clickedRecipe = newRecipeList.get(position);
        String recipeToQuery = clickedRecipe.getRecipeId();
        Thread thread = new Thread(new Runnable() {
            //new thread for networking task of fetching recipe info
            @Override
            public void run() {
                try {
                    showFullRecipe(recipeToQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void showFullRecipe(String recipeId) throws IOException, JSONException { //requests the url for the full recipe and then follows it within the user's browser.
        StringBuilder sb = new StringBuilder();
        sb.append("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/");
        sb.append(recipeId);
        sb.append("/information");

        String requestUrl = sb.toString();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder() // build up api JSON request
                .url(requestUrl)
                .get()
                .addHeader("x-rapidapi-key", "API-KEY_NEEDED_HERE")
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute(); // fetch JSON response
        String sourceUrl = "https://www.google.com"; // default URL

        if(response.body() != null) {
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            sourceUrl = jsonObject.getString("sourceUrl");
        }

        if(!sourceUrl.startsWith("http://") && !sourceUrl.startsWith("https://")){ //checking that the url will be valid
            sourceUrl = "http://" + sourceUrl; // if invalid URL, add protocol
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl));
        startActivity(browserIntent); // open the user's browser with the given URL
    }

    public void saveRecipe(View view) { // when save icon clicked, save the recipe to the local SQLite DB
        try {
            ViewParent buttonParent = view.getParent();
            TextView recipeNameTextView = ((View) buttonParent).findViewById(R.id.text_view_1);
            String recipeToSave = recipeNameTextView.getText().toString();
            for(Recipe recipe : newRecipeList){
                if(recipe.getRecipeTitle().equals(recipeToSave)){
                    int index = newRecipeList.indexOf(recipe);

                    SQLiteDB recipedb = new SQLiteDB(this);
                    SQLiteDatabase db = recipedb.getWritableDatabase();
                    recipedb.saveNewRecipe(db, recipe.getRecipeId(), recipe.getRecipeTitle(), recipe.getThumbnailUrl());

                    newRecipeList.remove(index);
                    adapter.notifyItemRemoved(index); // once saved, remove item from list

                    Toast.makeText(this, "Recipe Saved!" , Toast.LENGTH_SHORT).show(); // notify of success
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}