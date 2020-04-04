package com.example.gamesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Grab Existing Preferences
        userPreferences  = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme",0);
        int language = userPreferences.getInt("language",0);
        final int game  = userPreferences.getInt("game",0);

        // Set Dark Theme
        if(theme == 1) setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        final Button games = findViewById(R.id.games);
        final Button ranking = findViewById(R.id.rankings);
        final Button settings = findViewById(R.id.settings);

        games.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                gamesList(view);
            }
        });

        ranking.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                ranking(view);
            }
        });

        settings.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                settings(view);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFavGame(game);
            }
        });
    }

    public void gamesList(View view) {
        final Intent gamesList = new Intent(this, GameListActivity.class);
        startActivity(gamesList);
    }

    public void ranking(View view) {
        final Intent rank = new Intent(this, RankingActivity.class);
        startActivity(rank);
    }

    public void settings(View view) {
        final Intent sett = new Intent(this, SettingsActivity.class);
        startActivity(sett);
    }

    public void startFavGame(int id) {

        final Intent start;

        switch (id) {

            case 0:
                start = new Intent(this, SnakeScreen.class);
                startActivity(start);
                break;
            case 1:
                start = new Intent(this, Minesweeper.class);
                startActivity(start);
                break;
            case 2:
                start = new Intent(this, MathGame.class);
                startActivity(start);
                break;
            case 3:
                start = new Intent(this, FlagQuiz.class);
                startActivity(start);
                break;
            default:
                start = new Intent(this, GameListActivity.class);
                startActivity(start);
        }

    }
}