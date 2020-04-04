package com.example.gamesapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FireBaseOffline extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
