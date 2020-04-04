package com.example.gamesapp;

public class Score {
    public String username;
    public String game;
    public int points;

    public Score(String username, String game, int points) {
        this.username = username;
        this.game = game;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public String getGame() {
        return game;
    }

    public int getPoints() {
        return points;
    }

    public Score() {
    }
}