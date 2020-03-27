package com.example.gamesapp;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class RankingActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    ArrayList<HashMap<String, String>> rankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        /*final TextView rank = findViewById(R.id.rankingView);
        DBHelper db = new DBHelper(RankingActivity.this);
        db.open();
        Cursor c = db.getRanking();
        int position=0;
        String ranking = ("# \t username \t game \t points \n");
        if (c.moveToFirst()) {
            do {
                position++;
                ranking = ranking + position + " \t " + c.getString(1) + " \t " +
                        c.getString(2) + " \t " + c.getString(3) + " \n";
                rank.setText(ranking);
            } while (c.moveToNext());
        }
        db.close();*/
        rankingList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.rankList);

        DBHelper db = new DBHelper(RankingActivity.this);
        db.open();
        Cursor c = db.getRanking();
        if (c.moveToFirst()) {
             String pos=c.getString(0);
             String username=c.getString(1);
             String game=c.getString(2);
             String score=c.getString(3);

            HashMap<String, String> record = new HashMap<>();

            // adding each child node to HashMap key => value
            record.put("pos", pos);
            record.put("username", username);
            record.put("game", game);
            record.put("score", score);

            // adding record to record list
            rankingList.add(record);
            } while (c.moveToNext());
        db.close();

       new GetGames().execute();
    }

    private class GetGames extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(RankingActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://www.fantascelta.altervista.org/api/ranking.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray ranking = jsonObj.getJSONArray("ranking");

                    // looping through All Contacts
                    for (int i = 0; i < ranking.length(); i++) {
                        JSONObject c = ranking.getJSONObject(i);
                        String pos = c.getString("pos");
                        String username = c.getString("username");
                        String game = c.getString("game");
                        String score = c.getString("score");

                        // tmp hash map for single record
                        HashMap<String, String> record = new HashMap<>();

                        // adding each child node to HashMap key => value
                        record.put("pos", pos);
                        record.put("username", username);
                        record.put("game", game);
                        record.put("score", score);

                        // adding record to record list
                        rankingList.add(record);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(RankingActivity.this, rankingList, R.layout.list_item, new String[]{ "pos", "username", "game","score"}, new int[]{R.id.pos,R.id.username,R.id.game,R.id.score});
            lv.setAdapter(adapter);
        }
    }

    public InputStream OpenHttpConnection(String urlString) throws IOException
    {
        InputStream in = null;
        int risposta = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("No HTTP Connection");

        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            risposta = httpConn.getResponseCode();
            if (risposta == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Connection", ex.getLocalizedMessage());
            throw new IOException("Error of connection");
        }

        return in;
    }

    public String ReadText(String URL)
    {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
        } catch (IOException e) {
            Log.d("Web service", e.getLocalizedMessage());
            return "";
        }
        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[BUFFER_SIZE];
        try {
            while ((charRead = isr.read(inputBuffer))>0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            Log.d("Web service", e.getLocalizedMessage());
            return "";
        }
        return str;
    }
}