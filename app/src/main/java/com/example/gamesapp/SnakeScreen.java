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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;


    public class SnakeScreen extends AppCompatActivity {

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

        setContentView(R.layout.snake);

        Toolbar toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        ImageButton options= findViewById(R.id.button_play);
        options.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                optionsSnake(view);
            }
        });

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

    // On Down Arrow Click, Snake Turns Down
    // Called from Button in View
    public void downClick(View view){
        snakeGame.snake.turnDown();
    }

    // On Up Arrow Click, Snake Turns Up
    // Called from Button in View
    public void upClick(View view){
        snakeGame.snake.turnUp();
    }

    // On Game Over, Make Alert Dialog with Two Options
    // Called from Game Object
    public void gameOver(){

        final CharSequence[] choice = {"Yes","No, i want to play again", "No, i want to quit"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You reached score: "+score.getText()+".\nDo you want to save it?");
        builder.setItems(choice, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // Save the result
                    case 0:
                        builder.setMessage("Enter your username");
                        final EditText input = new EditText(SnakeScreen.this);
                        builder.setView(input);
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String username = input.getText().toString();
                                DBHelper db = new DBHelper(SnakeScreen.this);
                                db.open();
                                db.insertRow(username,"Snake", snakeGame.getScore());
                                db.close();
                                final CharSequence[] items = {"Play Again","Go Back"};
                                AlertDialog.Builder build = new AlertDialog.Builder(builder.getContext());
                                build.setTitle("What do you want to do?");
                                build.setItems(items, new DialogInterface.OnClickListener() {

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
                        snakeGame.setup();
                        snakeGame.invalidate();
                        break;
                    // Don't save
                    default:
                        mActivity.finish();
                }
            }
        });

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

    public void optionsSnake(View view) {
        final Intent options = new Intent(this, SnakeOptions.class);
        startActivity(options);
    }

}