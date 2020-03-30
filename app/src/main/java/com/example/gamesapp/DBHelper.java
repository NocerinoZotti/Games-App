package com.example.gamesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

public class DBHelper  {

    static final String KEY_ID = "id";
    static final String KEY_USERNAME = "username";
    static final String KEY_GAME = "game";
    static final String KEY_POINTS = "points";
    static final String DATABASE_NAME = "TestDB";
    static final String DATABASE_TABLE = "Ranking";
    static final int DATABASE_VERSION = 1;

    static final String RANKING_TABLE =
            "CREATE TABLE ranking (id integer primary key autoincrement, "
                    + "username text not null, game text not null, points integer not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBHelper(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(RANKING_TABLE);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            Log.w(DatabaseHelper.class.getName(),"Update of DB Version " + oldVersion + " to "
                    + newVersion + ". Old data gonna be deleted");
            db.execSQL("DROP TABLE IF EXISTS ranking");
            onCreate(db);
        }

    }


    public DBHelper open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    public void close()
    {
        DBHelper.close();
    }


    public long insertRow(String username, String game, Integer points)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_GAME, game);
        initialValues.put(KEY_POINTS, points);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deleteRow(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }


    public Cursor getRanking()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_USERNAME, KEY_GAME, KEY_POINTS}, null, null, null, null, KEY_POINTS + " DESC");
    }


    public Cursor getUserRanking(String username) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_USERNAME, KEY_GAME, KEY_POINTS}, KEY_USERNAME + "=" + username, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void recordScore(final int score, final String game, final Context context){
        final CharSequence[] choice = {"Yes","No, i want to play again", "No, i want to quit"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("You reached score: "+score+".\nDo you want to save it?");
        builder.setItems(choice, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // Save the result
                    case 0:
                        builder.setMessage("Enter your username");
                        final EditText input = new EditText(context);
                        builder.setView(input);
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String username = input.getText().toString();
                                DBHelper db = new DBHelper(context);
                                db.open();
                                db.insertRow(username, game, score);
                                db.close();
                                final CharSequence[] items = {"Play Again","Go Back"};
                                AlertDialog.Builder build = new AlertDialog.Builder(builder.getContext());
                                build.setTitle("Your score has been saved! What do you want to do?");
                                build.setItems(items, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int item) {
                                        switch(item){
                                            // Play Again
                                            case 0:
                                                return;
                                            // Go Back
                                            default:
                                                Activity a =(Activity) context;
                                                a.finish();
                                        }
                                    }
                                });

                                build.setCancelable(false);
                                build.create().show();
                            }
                        });
                        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                            }
                        });
                        builder.show();
                        break;
                    case 1:

                        break;
                    // Don't save
                    default:
                        Activity a =(Activity) context;
                        a.finish();
                }
            }
        });

        builder.create().show();
    }

}