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

package com.example.gamesapp.snake;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gamesapp.DBHelper;
import com.example.gamesapp.R;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;


public class SnakeScreen extends AppCompatActivity {

    private SnakeGame snakeGame;
    private FrameLayout frameView;
    private TextView score;
    private Activity mActivity;
    SharedPreferences userPreferences;
    private boolean darkTheme=false,snakeOriented=false,classicMode=false;
    ImageButton info;

    // Initialize Game Screen
    @Override
    public void onCreate(Bundle savedInstanceState) {

        userPreferences = getSharedPreferences("settings", 0);
        if(userPreferences.getInt("theme",0) == 1){
            setTheme(R.style.AppThemeDark);
            darkTheme=true;
        }

        // Create Game View & Add Handler to Current Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake);
        mActivity = this;

        Toolbar toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        info = (ImageButton)findViewById(R.id.button_play);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
                AlertDialog.Builder myAlert = new AlertDialog.Builder(SnakeScreen.this);
                myAlert.setTitle(R.string.tutorial);
                myAlert.setMessage(R.string.snake_desc);
                myAlert.show();
            }
        });

        // Grab Score TextView Handle, Create Game Object & Add Game to Frame
        score = (TextView) findViewById(R.id.score);
        snakeGame = new SnakeGame(this,this,score,darkTheme,classicMode,snakeOriented);
        frameView = (FrameLayout) findViewById(R.id.gameFrame);
        frameView.addView(snakeGame);

    }

    // On Left Arrow Click, Snake Turns Left
    // Called from Button in View
    public void leftClick(View view) {
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

        final String username = userPreferences.getString("username", null);

        final CharSequence[] choice = {getResources().getString(R.string.yes),getResources().getString(R.string.play_again), getResources().getString(R.string.quit)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.reach)+score.getText()+'\n'+getResources().getString(R.string.save));
        builder.setItems(choice, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // Save the result
                    case 0:
                        builder.setMessage(getResources().getString(R.string.username));
                        final TextInputEditText input = new TextInputEditText(SnakeScreen.this);
                        input.setText(username);
                        builder.setView(input);
                        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String username = input.getText().toString();
                                DBHelper db = new DBHelper(SnakeScreen.this);
                                db.open();
                                db.insertRow(username,"Snake", snakeGame.getScore());
                                db.insertOnline(username,"Snake", snakeGame.getScore());
                                db.close();
                                final CharSequence[] items = {getResources().getString(R.string.restart),getResources().getString(R.string.exit)};
                                AlertDialog.Builder build = new AlertDialog.Builder(builder.getContext());
                                build.setTitle(getResources().getString(R.string.saveDone));
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
                        builder.setNegativeButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                gameOver();
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

        builder.setCancelable(false);
        builder.create().show();
    }

    // On Game Pause, Stop Snake & Make Alert Dialog
    // Called from Hardware Button Handler and Activity Pause
    public void pauseGame(){

        // Do Nothing if Game Over
        if(snakeGame.gameOver) return;

        snakeGame.snake.stopped = true;

        final CharSequence[] items = {getResources().getString(R.string.cont),getResources().getString(R.string.restart),getResources().getString(R.string.exit)};
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
        pauseGame();
        super.onPause();
    }
}