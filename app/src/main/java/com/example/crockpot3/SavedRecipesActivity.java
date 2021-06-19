package com.example.crockpot3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crockpot3.database.SQLiteDB;
import com.example.crockpot3.lists.Recipe;
import com.example.crockpot3.lists.SavedRecipeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SavedRecipesActivity extends AppCompatActivity implements SavedRecipeAdapter.OnItemClickListener { // activity for displaying recipes saved by the user in the SQLite DB
    private ArrayList<Recipe> savedRecipeList = new ArrayList<>();
    private SavedRecipeAdapter adapter = new SavedRecipeAdapter(savedRecipeList, this);
    private SQLiteDB recipedb;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_recipes_page);

        RecyclerView recycler_view = findViewById(R.id.recycler_view_saved_recipes);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);
    }

    @Override
    protected void onStart(){ // when activity is started, load all saved recipes from db into recyclerview list
        super.onStart();
        recipedb = new SQLiteDB(this);
        db = recipedb.getWritableDatabase();

        String sql = "SELECT * FROM Saved_Recipes";
        Cursor cr = db.rawQuery(sql, null); //set up cursor for accessing table
        if(cr != null && cr.getCount() > 0) {
            cr.moveToFirst();
            int i = 0;
            do {
                Recipe savedRecipeFromDb = new Recipe();
                savedRecipeFromDb.setRecipeId(Integer.toString(cr.getInt(0)));
                savedRecipeFromDb.setRecipeTitle(cr.getString(1));
                savedRecipeFromDb.setThumbnailUrl(cr.getString(2));
                savedRecipeList.add(savedRecipeFromDb);
                adapter.notifyItemInserted(i);
                i++;
            } while (cr.moveToNext());
            cr.close(); //close the cursor
        }
    }

    @Override
    protected void onDestroy(){ //clears the existing list of recipes so that when the activity is next called there are no duplicates
        super.onDestroy();
        int size = savedRecipeList.size();
        savedRecipeList.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }

    @Override
    protected void onStop(){ //clears the existing list of recipes so that when the activity is next called there are no duplicates
        super.onStop();
        int size = savedRecipeList.size();
        savedRecipeList.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }


    @Override
    public void onItemClick(int position) { //handle clicking the recipe card
        Recipe clickedRecipe = savedRecipeList.get(position);
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

        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .addHeader("x-rapidapi-key", "API-KEY_NEEDED_HERE")
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
        String sourceUrl = "https://www.google.com";

        if(response.body() != null) {
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            sourceUrl = jsonObject.getString("sourceUrl");
        }

        if(!sourceUrl.startsWith("http://") && !sourceUrl.startsWith("https://")){ //checking that the url will be valid
            sourceUrl = "http://" + sourceUrl;
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl));
        startActivity(browserIntent); // following URL into user's browser
    }

    public void deleteSavedRecipe(View view) { // removes a recipe from the user's list of saved recipes and from the database
        try {
            ViewParent buttonParent = view.getParent();
            TextView recipeNameTextView = ((View) buttonParent).findViewById(R.id.text_view_1);
            String recipeToDelete = recipeNameTextView.getText().toString();
            for(Recipe recipe : savedRecipeList){
                if(recipe.getRecipeTitle().equals(recipeToDelete)){
                    int index = savedRecipeList.indexOf(recipe);

                    recipedb = new SQLiteDB(this);
                    db = recipedb.getWritableDatabase();
                    recipedb.deleteSavedRecipe(db, recipe.getRecipeId());

                    savedRecipeList.remove(index);
                    adapter.notifyItemRemoved(index);

                    Toast.makeText(this, "Recipe Deleted!" , Toast.LENGTH_SHORT).show(); // notify user
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}