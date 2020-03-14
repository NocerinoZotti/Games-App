/*
Snake - an Android Game
Copyright 2012 Nick Eyre <nick@nickeyre.com>

Snake is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Snake is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Snake.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.example.gamesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SnakeScreen extends Activity {

    private SnakeGame snakeGame;
    private FrameLayout frameView;
    private TextView score;
    private Activity mActivity;
    SharedPreferences userPreferences, speedSetting;
    private boolean darkTheme=false,snakeOriented=false,classicMode=false;
    private int speed;

    // Initialize Game Screen
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Set Theme, Controls Mode, View Mode & Speed According to Settings
        // Speed Setting is Stored in a Different File Because It Should Not Be Synced Across Devices
        userPreferences = getSharedPreferences("settings", 0);
        speedSetting = getSharedPreferences("speed", 0);
        if(userPreferences.getInt("theme",0) == 1){
            setTheme(android.R.style.Theme_Holo);
            darkTheme=true;
        }
        if(userPreferences.getInt("view",0) == 1)  classicMode = true;
        if(userPreferences.getInt("controls",0) == 1)  snakeOriented = true;
        speed = speedSetting.getInt("speed", 0);

        // Create Game View & Add Handler to Current Activity
        super.onCreate(savedInstanceState);
        if(snakeOriented)
            setContentView(R.layout.snake);
        else
            setContentView(R.layout.snake);
        mActivity = this;

        // Grab Score TextView Handle, Create Game Object & Add Game to Frame
        score = (TextView) findViewById(R.id.score);
        snakeGame = new SnakeGame(this,this,score,darkTheme,classicMode,snakeOriented,speed);
        frameView = (FrameLayout) findViewById(R.id.gameFrame);
        frameView.addView(snakeGame);

    }

    // On Left Arrow Click, Snake Turns Left
    // Called from Button in View
    public void leftClick(View view){
        snakeGame.snake.turnLeft();
    }

    // On Right Arrow Click, Snake Turns Right
    // Called from Button in View
    public void rightClick(View view){
        snakeGame.snake.turnRight();
    }

    // On Down Arrow Click, Snake Turns Down (Four Direction Only)
    // Called from Button in View
    public void downClick(View view){
        snakeGame.snake.turnDown();
    }

    // On Up Arrow Click, Snake Turns Up (Four Direction Only)
    // Called from Button in View
    public void upClick(View view){
        snakeGame.snake.turnUp();
    }

    // On Game Over, Make Alert Dialog with Two Options
    // Called from Game Object
    public void gameOver(){

        final CharSequence[] items = {"Play Again","Go Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You reached score: "+score.getText());
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // Play Again
                    case 0:
                        snakeGame.setup();
                        snakeGame.invalidate();
                        break;

                    // Go Back
                    default:
                        mActivity.finish();
                }
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    // On Game Pause, Stop Snake & Make Alert Dialog
    // Called from Hardware Button Handler and Activity Pause
    public void pauseGame(){

        // Do Nothing if Game Over
        if(snakeGame.gameOver) return;

        snakeGame.snake.stopped = true;

        final CharSequence[] items = {"Continue","Start Over","Go Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.paused);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // New Game (Start Over)
                    case 1:
                        snakeGame.setup();
                        snakeGame.invalidate();
                        break;

                    // End Game (Go Back)
                    case 2:
                        mActivity.finish();
                        break;

                    // Continue Game
                    default:
                        snakeGame.snake.stopped=false;
                        snakeGame.invalidate();
                }
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    // Hardware Button Presses
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        // On Menu or Back Press, Pause Game
        if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode ==  KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0)
            pauseGame();

        // On Left D-Pad Button, Snake Turns Left
        if((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && event.getRepeatCount()==0)
            snakeGame.snake.turnLeft();

        // On Right D-Pad Button, Snake Turns Right
        if((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && event.getRepeatCount()==0)
            snakeGame.snake.turnRight();

        return true;
    }

    // Pause Game when Activity Paused
    @Override
    public void onPause(){
        super.onPause();
        pauseGame();
    }

}