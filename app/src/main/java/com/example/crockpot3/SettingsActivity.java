package com.example.crockpot3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity { // activity for defining user SharedPreferences for API recipe requests
    private SwitchCompat ignorePantrySwitch;
    private SwitchCompat minimiseMissingSwitch;
    private Button saveSettingsBtn;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH_IGNORE = "ignorePantrySwitch";
    public static final String SWITCH_MINIMISE = "minimiseMissingSwitch";

    private boolean ignorePantry;
    private boolean minimiseMissingIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ignorePantrySwitch = (SwitchCompat) findViewById(R.id.switch_ignore_pantry);
        minimiseMissingSwitch = (SwitchCompat) findViewById(R.id.switch_minimise_missing);
        saveSettingsBtn = (Button) findViewById(R.id.save_settings_button);

        saveSettingsBtn.setOnClickListener(new View.OnClickListener() { //saves the settings when save button is clicked
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        loadSettings();
        updateSettingViews();
    }

    public void saveSettings(){ //saves the current settings configuration to SharedPrefs
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SWITCH_IGNORE, ignorePantrySwitch.isChecked());
        editor.putBoolean(SWITCH_MINIMISE, minimiseMissingSwitch.isChecked());

        editor.apply();
        Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show(); // notify user
    }

    public void loadSettings(){ //loads the most recent state of SharedPrefs
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        ignorePantry = sharedPreferences.getBoolean(SWITCH_IGNORE, true);
        minimiseMissingIngredients = sharedPreferences.getBoolean(SWITCH_MINIMISE, false);
    }

    public void updateSettingViews(){ //updates the setting views e.g. switches accordingly
        ignorePantrySwitch.setChecked(ignorePantry);
        minimiseMissingSwitch.setChecked(minimiseMissingIngredients);
    }

}