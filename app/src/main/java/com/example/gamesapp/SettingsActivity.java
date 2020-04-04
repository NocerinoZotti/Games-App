package com.example.gamesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class SettingsActivity extends Activity {

    SharedPreferences userPreferences;
    SharedPreferences.Editor userPreferencesEditor;
    Spinner themeSpinner, languageSpinner, gameSpinner;
    TextInputEditText usernameInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Grab Existing Settings
        userPreferences  = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme",0);
        int language = userPreferences.getInt("language",0);
        int game  = userPreferences.getInt("game",0);
        String username = userPreferences.getString("username", null);

        // Set Dark Theme
        if(theme == 1) setTheme(R.style.AppThemeDark);
        if (language==1) {
            String languageToLoad  = "it";
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton info = findViewById(R.id.button_play);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(SettingsActivity.this);
                myAlert.setTitle(R.string.settings);
                myAlert.setMessage(R.string.sett_desc);
                myAlert.show();
            }
        });

        // Grab Settings Spinners
        themeSpinner = (Spinner) findViewById(R.id.spinnerTheme);
        languageSpinner = (Spinner) findViewById(R.id.spinnerLanguage);
        gameSpinner = (Spinner) findViewById(R.id.spinnerGame);

        usernameInput = (TextInputEditText) findViewById(R.id.inputUsername);

        // Set Spinner Current Values
        themeSpinner.setSelection(theme);
        languageSpinner.setSelection(language);
        gameSpinner.setSelection(game);
        usernameInput.setText(username);
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
        String username = usernameInput.getText().toString();

        // Save in Settings
        userPreferencesEditor = userPreferences.edit();
        userPreferencesEditor.putInt("theme", theme);
        userPreferencesEditor.putInt("language", language);
        userPreferencesEditor.putInt("game", game);
        userPreferencesEditor.putString("username", username);
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