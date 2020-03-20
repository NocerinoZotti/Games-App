package com.example.gamesapp;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        final TextView rank = findViewById(R.id.rankingView);
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
        db.close();
        //try {
       //     OpenHttpConnection("https://www.fantascelta.altervista.org/api/ranking.php");
        //} catch (IOException e) {
          //  e.printStackTrace();
        //}
       // rank.setText(ReadText("https://www.fantascelta.altervista.org/api/ranking.php"));
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
