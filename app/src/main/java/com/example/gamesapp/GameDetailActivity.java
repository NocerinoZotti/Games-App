package com.example.gamesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.gamesapp.minesweeper.Minesweeper;
import com.example.gamesapp.snake.SnakeScreen;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.view.MenuItem;

/**
 * An activity representing a single Game detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link GameListActivity}.
 */
public class GameDetailActivity extends AppCompatActivity {

    SharedPreferences userPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Grab Existing Preferences
        userPreferences  = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme",0);

        if(theme == 1) setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(getIntent().getStringExtra(GameDetailFragment.ARG_ITEM_ID));
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            CollapsingToolbarLayout appBar =(CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            Bundle arguments = new Bundle();
            arguments.putString(GameDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(GameDetailFragment.ARG_ITEM_ID));
            switch(getIntent().getStringExtra(GameDetailFragment.ARG_ITEM_ID)){
                case "0":
                    appBar.setForeground(getDrawable(R.drawable.snake));
                    break;
                case "1":
                    appBar.setForeground(getDrawable(R.drawable.mine));
                    break;
                case "2":
                    appBar.setForeground(getDrawable(R.drawable.math));
                    break;
                case "3":
                    appBar.setForeground(getDrawable(R.drawable.flag));
            }
            GameDetailFragment fragment = new GameDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.game_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, GameListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startGame(String id) {

        final Intent start;

        switch (id) {

            case "0":
                start=new Intent(this, SnakeScreen.class);
                startActivity(start);
                break;
            case "1":
                start = new Intent(this, Minesweeper.class);
                startActivity(start);
                break;
            case "2":
                start = new Intent(this, MathGame.class);
                startActivity(start);
                break;
            case "3":
                start = new Intent(this, FlagQuiz.class);
                startActivity(start);
                break;
            default:
                start = new Intent(this, GameListActivity.class);
                startActivity(start);
        }
    }
}