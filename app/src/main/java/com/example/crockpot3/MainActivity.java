package com.example.crockpot3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewParent;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crockpot3.database.SQLiteDB;
import com.example.crockpot3.lists.Ingredient;
import com.example.crockpot3.lists.IngredientAdapter;
import com.example.crockpot3.lists.Recipe;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crockpot3.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private AutoCompleteTextView ingredients;
    private SQLiteDB ingredientsdb;
    private SQLiteDatabase db;
    private ArrayList<Ingredient> ingredientList = new ArrayList<>();
    private IngredientAdapter adapter = new IngredientAdapter(ingredientList);
    private ArrayList<Recipe> recipeResults = new ArrayList<>();
    private CrockPotBroadcastReceiver broadcastReceiver = new CrockPotBroadcastReceiver();

    public static ArrayList<Recipe> recipesToPass = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) { // handles activity creation
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupAutocomplete();
        addIngredient();

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);

        setAlarm();
    }

    @Override
    protected void onStart(){ // upon starting the activity, start the broadcast receiver
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop(){ // when the activity is stopped, suspend the broadcast receiver
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void setAlarm(){ // set the alarm to go off at 5pm
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        startAlarm(cal);
    }

    private void startAlarm(Calendar cal){ // use alarmManager and pendingIntent to set up alarm notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if(cal.before(Calendar.getInstance())){
            cal.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    private void setupAutocomplete() { //link the local database to the autocomplete text box
        ingredients = findViewById(R.id.autoCompleteIngredients);
        ingredientsdb = new SQLiteDB(this);
        db = ingredientsdb.getWritableDatabase();

        String[] dbData;
        String sql = "SELECT * FROM Ingredients";
        Cursor cr = db.rawQuery(sql, null); //set up cursor for accessing table
        cr.moveToFirst();
        dbData = new String[cr.getCount()];
        int i = 0;
        do {
            dbData[i] = cr.getString(1);
            i++;
        } while (cr.moveToNext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, dbData);
        ingredients.setAdapter(adapter);
        cr.close(); //close the cursor
    }

    private void addIngredient() { // adds a user-specified ingredient to the query
        Button button = findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String ingredient = ingredients.getText().toString().trim().toLowerCase();
                if(!ingredient.equals("")) {
                    // getting typed ingredient from text box and formatting, then adding to list
                    ingredients.setText(""); // clearing the text box
                    int index = ingredientList.size();
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setIngredientName(ingredient);
                    ingredientList.add(index, newIngredient);
                    adapter.notifyItemInserted(index);
                }
            }
        });
    }

    private void getRecipes() throws IOException, JSONException { // creates an API recipe query with the user's chosen parameters
        SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.SHARED_PREFS, MODE_PRIVATE); // pulling in user preferences
        boolean ignorePantry = sharedPreferences.getBoolean(SettingsActivity.SWITCH_IGNORE, true);
        boolean minimiseMissingIngredients = sharedPreferences.getBoolean(SettingsActivity.SWITCH_MINIMISE, false);
        String ignorePantryParam = "";
        String minimiseMissingParam = "";

        if(ignorePantry){
            ignorePantryParam = "ignorePantry=true";
        }
        else{
            ignorePantryParam = "ignorePantry=false";
        }

        if(minimiseMissingIngredients){
            minimiseMissingParam = "&ranking=2";
        }
        else{
            minimiseMissingParam = "&ranking=1";
        }

        StringBuilder sb = new StringBuilder(); //build up the request url string using the user's ingredients
        sb.append("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/findByIngredients?ingredients=");
        sb.append(ingredientList.get(0).getIngredientName());
        for(int i=1; i<ingredientList.size(); i++){
            sb.append("%2C");
            sb.append(ingredientList.get(i).getIngredientName());
        }
        sb.append("&number=5&");
        sb.append(ignorePantryParam); // add user preference parameters
        sb.append(minimiseMissingParam);
        String requestUrl = sb.toString(); // convert to String

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder() //build the api request
                .url(requestUrl) // use the just-constructed URL
                .get()
                .addHeader("x-rapidapi-key", "API-KEY_NEEDED_HERE") //APIKEY
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com") //HOST
                .build();

        Response response = client.newCall(request).execute(); // fetch response

        if(response.body() != null) { // parse the JSON response for relevant data
            String responseBody = response.body().string();
            JSONArray jsonarray = new JSONArray(responseBody);
            int size = jsonarray.length();
            for (int i = 0; i < size; i++) { // loop through fetched JSON response
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                String title = jsonobject.getString("title");
                String url = jsonobject.getString("image");
                String id = jsonobject.getString("id");

                Recipe recipe = new Recipe(); // instantiate new recipe
                recipe.setRecipeTitle(title);
                recipe.setThumbnailUrl(url); // specify recipe attributes
                recipe.setRecipeId(id);
                recipeResults.add(recipe); // add recipe to arrayList
            }
        }

    }

    public void showGuide(MenuItem menuItem){ // opens the user guide webview activity
        Intent intent = new Intent(this, UserGuideActivity.class);
        startActivity(intent);
    }

    public void removeItem(View view) { //removes an ingredient from the recylerview list
        try {
            ViewParent buttonParent = view.getParent();
            TextView ingredientTextView = ((View) buttonParent).findViewById(R.id.text_view_1);
            String ingredientToRemove = ingredientTextView.getText().toString();
            for (Ingredient item : ingredientList) {
                if (item.getIngredientName().equals(ingredientToRemove)) {
                    int index = ingredientList.indexOf(item);
                    ingredientList.remove(index);
                    adapter.notifyItemRemoved(index);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void findRecipes(View view) { // uses the getRecipes() function and threading to gather recipes JSON while starting new activity
        if(ingredientList.size() > 0) {
            try {
                Thread thread = new Thread(new Runnable() {
                    //new thread for networking task of fetching recipes
                    @Override
                    public void run() {
                        try {
                            getRecipes();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

                Thread.sleep(1000);

                recipesToPass = recipeResults;
                Intent intent = new Intent(this, NewRecipesActivity.class); // start activity to show recipes
                startActivity(intent);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        else{ // if the user has given no ingredients, do not call API and prompt for ingredients
            Toast.makeText(this, "Please add some ingredients." , Toast.LENGTH_SHORT).show();
        }
    }

    public void showSavedRecipes(MenuItem menuItem){ //starts the Saved Recipes activity
        Intent intent = new Intent(this, SavedRecipesActivity.class);
        startActivity(intent);
    }

    public void showSettings(MenuItem menuItem){ //starts the Settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}