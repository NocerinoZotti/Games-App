package com.example.gamesapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.gamesapp.minesweeper.Minesweeper;
import com.example.gamesapp.snake.SnakeScreen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SharedPreferences userPreferences;
    DatabaseReference onlineDb;
    List<Score> scoreList;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Grab Existing Preferences
        userPreferences  = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme",0);
        int language = userPreferences.getInt("language",0);
        final int game  = userPreferences.getInt("game",0);

        onlineDb = FirebaseDatabase.getInstance().getReference();
        scoreList = new ArrayList<>();
        db = new DBHelper(this);

        if(theme == 1) setTheme(R.style.AppThemeDark);
        if (language==1) {
            String languageToLoad  = "it";
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        } else {
            String languageToLoad  = "en";
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        ImageButton info = findViewById(R.id.button_play);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(MainActivity.this);
                myAlert.setTitle(R.string.welcome);
                myAlert.setMessage(R.string.app_desc);
                myAlert.show();
            }
        });

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

    @Override
    protected void onStart() {
        super.onStart();
        onlineDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                scoreList.clear();
                for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    Score score = scoreSnapshot.getValue(Score.class);
                    scoreList.add(score);
                }

                db.open();
                db.deleteAll();
                for (Score score : scoreList) {
                    db.insertRow(score.username, score.game, score.points);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

            case 1:
                start = new Intent(this, SnakeScreen.class);
                startActivity(start);
                break;
            case 2:
                start = new Intent(this, Minesweeper.class);
                startActivity(start);
                break;
            case 3:
                start = new Intent(this, MathGame.class);
                startActivity(start);
                break;
            case 4:
                start = new Intent(this, FlagQuiz.class);
                startActivity(start);
                break;
            default:
                start = new Intent(this, GameListActivity.class);
                startActivity(start);
        }

    }
    
}