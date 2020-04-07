package com.example.gamesapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.HashMap;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.AdapterView.*;

public class RankingActivity extends AppCompatActivity {

    int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Grab Existing Preferences
        SharedPreferences userPreferences = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme", 0);

        if (theme == 1) setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        ImageButton info = findViewById(R.id.button_play);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(RankingActivity.this);
                myAlert.setTitle(R.string.rankings);
                myAlert.setMessage(R.string.rank_desc);
                myAlert.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Spinner games = (Spinner) findViewById(R.id.spinnerGames);
        ArrayList<HashMap<String, String>> rankingList;
        rankingList = new ArrayList<>();
        ListView ranking = (ListView) findViewById(R.id.rankList);
        DBHelper db = new DBHelper(RankingActivity.this);
        db.open();
        Cursor c;

        switch (selected) {
            case 1:
                c = db.getGameRanking("'Snake'");
                break;
            case 2:
                c = db.getGameRanking("'Minesweeper'");
                break;
            case 3:
                c = db.getGameRanking("'Math Game'");
                break;
            case 4:
                c = db.getGameRanking("'Flag Quiz'");
                break;
            default:
                c = db.getRanking();
                break;
        }


        int position = 0;
        if (c.moveToFirst())
            do {
                position++;
                String username = c.getString(1);
                String game = c.getString(2);
                String score = c.getString(3);

                HashMap<String, String> record = new HashMap<>();

                // adding each child node to HashMap key => value
                record.put("pos", position + "");
                record.put("username", username);
                record.put("game", game);
                record.put("score", score);

                // adding record to record list
                rankingList.add(record);
            } while (c.moveToNext());

        db.close();

        games.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selected != games.getSelectedItemId()) {
                    selected = (int) games.getSelectedItemId();
                    onResume();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (!rankingList.isEmpty()) {
            ListAdapter adapter = new SimpleAdapter(RankingActivity.this, rankingList, R.layout.list_item, new String[]{"pos", "username", "game", "score"}, new int[]{R.id.pos, R.id.username, R.id.game, R.id.score});
            ranking.setAdapter(adapter);
        }
    }
}