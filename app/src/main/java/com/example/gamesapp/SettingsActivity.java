package com.example.gamesapp;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class SettingsActivity extends Activity {

    SharedPreferences userPreferences;
    SharedPreferences.Editor userPreferencesEditor;
    Spinner themeSpinner, languageSpinner, gameSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Grab Existing Settings
        userPreferences  = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme",0);
        int language = userPreferences.getInt("language",0);
        int game  = userPreferences.getInt("game",0);

        // Set Dark Theme
        if(theme == 1) setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Grab Settings Spinners
        themeSpinner = (Spinner) findViewById(R.id.spinnerTheme);
        languageSpinner = (Spinner) findViewById(R.id.spinnerLanguage);
        gameSpinner = (Spinner) findViewById(R.id.spinnerGame);

        // Set Spinner Current Values
        themeSpinner.setSelection(theme);
        languageSpinner.setSelection(language);
        gameSpinner.setSelection(game);
    }

    // Back Button in View
    public void back(View view){
        onBackPressed();
    }

    // Go Back to Title Screen
    @Override
    public void onBackPressed(){

        // Get New Values
        int theme = themeSpinner.getSelectedItemPosition();
        int language = languageSpinner.getSelectedItemPosition();
        int game = gameSpinner.getSelectedItemPosition();

        // Save in Settings
        // Speed Setting is Stored in a Different File Because It Should Not Be Synced Across Devices
        userPreferencesEditor = userPreferences.edit();
        userPreferencesEditor.putInt("theme", theme);
        userPreferencesEditor.putInt("language", language);
        userPreferencesEditor.putInt("game", game);
        userPreferencesEditor.apply();

        // Call for Backup
        BackupManager backupManager = new BackupManager(this);
        backupManager.dataChanged();

        // Go Home & Close Options Screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}