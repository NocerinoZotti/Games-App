package com.example.gamesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBHelper {

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

    DatabaseReference onlineDb = FirebaseDatabase.getInstance().getReference();

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

    public void insertOnline(String username, String game, Integer points)
    {
        Score score = new Score(username, game, points);
        String userId = onlineDb.push().getKey();
        onlineDb.child(userId).setValue(score);
    }


    public boolean deleteRow(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }

    public void deleteAll()
    {
        db.delete(DATABASE_TABLE, null, null);
    }


    public Cursor getRanking()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_USERNAME, KEY_GAME, KEY_POINTS}, null, null, null, null, KEY_POINTS + " DESC");
    }


    public Cursor getGameRanking(String game) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_USERNAME, KEY_GAME, KEY_POINTS}, KEY_GAME + "=" + game, null, null, null, KEY_POINTS + " DESC", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}