package com.example.gamesapp;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class SnakeOptions extends Activity {

    SharedPreferences userPreferences, speedSetting;
    SharedPreferences.Editor userPreferencesEditor, speedSettingEditor;
    Spinner themeSpinner,controlsSpinner,viewSpinner,speedSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Grab Existing Settings
        // Speed Setting is Stored in a Different File Because It Should Not Be Synced Across Devices
        userPreferences  = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme",0);
        int controls = userPreferences.getInt("controls",0);
        int view  = userPreferences.getInt("view",0);
        speedSetting = getSharedPreferences("speed", 0);
       // int speed = speedSetting.getInt("speed",0);

        // Set Dark Theme
        if(theme == 1) setTheme(android.R.style.Theme_Holo);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake_options);

        // Set Spinner Current Values
        themeSpinner.setSelection(theme);
        controlsSpinner.setSelection(controls);
        viewSpinner.setSelection(view);
        //speedSpinner.setSelection(speed);
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
        int controls = controlsSpinner.getSelectedItemPosition();
        int view = viewSpinner.getSelectedItemPosition();
        int speed = speedSpinner.getSelectedItemPosition();

        // Save in Settings
        // Speed Setting is Stored in a Different File Because It Should Not Be Synced Across Devices
        userPreferencesEditor = userPreferences.edit();
        userPreferencesEditor.putInt("theme", theme);
        userPreferencesEditor.putInt("controls", controls);
        userPreferencesEditor.putInt("view", view);
        speedSettingEditor = speedSetting.edit();
        speedSettingEditor.putInt("speed", speed);
        userPreferencesEditor.commit();
        speedSettingEditor.commit();

        // Call for Backup
        BackupManager backupManager = new BackupManager(this);
        backupManager.dataChanged();

        // Go Home & Close Options Screen
        Intent intent = new Intent(this, SnakeScreen.class);
        startActivity(intent);
        this.finish();
    }
}
