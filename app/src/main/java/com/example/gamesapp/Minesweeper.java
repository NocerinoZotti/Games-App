package com.example.gamesapp;

import java.util.Locale;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Minesweeper extends AppCompatActivity
{
    private TextView txtScore;
    private TextView txtTimer;
    private ImageButton btnSmile;
    private ImageButton info;

    private TableLayout mineField; // table layout to add mines to

    private Block blocks[][]; // blocks for mine field
    private int blockDimension = 48; // width of each block
    private int blockPadding = 2; // padding between blocks

    private int numberOfRowsInMineField = 18;
    private int numberOfColumnsInMineField = 18;
    private int totalNumberOfMines = 20;

    // timer to keep track of time elapsed
    private Handler timer = new Handler();
    private int secondsPassed = 0;

    private boolean isTimerStarted; // check if timer already started or not
    private boolean areMinesSet; // check if mines are planted in blocks
    private boolean isGameOver;
    private int score=0;

    SharedPreferences userPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // Grab Existing Preferences
        userPreferences  = getSharedPreferences("settings", 0);
        int theme = userPreferences.getInt("theme",0);
        int language = userPreferences.getInt("language",0);

        if(theme == 1) setTheme(R.style.AppThemeDark);
        if (language==1) {
            String languageToLoad  = "it";
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper);

        info = (ImageButton)findViewById(R.id.button_play);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(Minesweeper.this);
                myAlert.setTitle(R.string.tutorial);
                myAlert.setMessage(R.string.mine_desc);
                myAlert.show();
            }
        });

        txtScore = (TextView) findViewById(R.id.ScoreCount);
        txtTimer = (TextView) findViewById(R.id.Timer);

        btnSmile = (ImageButton) findViewById(R.id.Smiley);
        btnSmile.setOnClickListener(new OnClickListener()
        {

            public void onClick(View view)
            {
                endExistingGame();
                startNewGame();
            }
        });

        Toolbar toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        mineField = (TableLayout)findViewById(R.id.MineField);

        showDialog("Click smiley to start New Game", 2000, true, false);
    }

    private void startNewGame()
    {
        // plant mines and do rest of the calculations
        createMineField();
        // display all blocks in UI
        showMineField();

        score = 0;
        isGameOver = false;
        secondsPassed = 0;
    }

    private void showMineField()
    {
        // remember we will not show 0th and last Row and Columns
        // they are used for calculation purposes only
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams((blockDimension + 2 * blockPadding) * numberOfColumnsInMineField, blockDimension + 2 * blockPadding));

            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                blocks[row][column].setLayoutParams(new LayoutParams(
                        blockDimension + 2 * blockPadding,
                        blockDimension + 2 * blockPadding));
                blocks[row][column].setPadding(blockPadding, blockPadding, blockPadding, blockPadding);
                tableRow.addView(blocks[row][column]);
            }
            mineField.addView(tableRow,new TableLayout.LayoutParams(
                    (blockDimension + 2 * blockPadding) * numberOfColumnsInMineField, blockDimension + 2 * blockPadding));
        }
    }

    private void endExistingGame()
    {
        stopTimer(); // stop if timer is running
        txtTimer.setText("000"); // revert all text
        txtScore.setText("000"); // revert mines count
        btnSmile.setBackgroundResource(R.drawable.smile);

        // remove all rows from mineField TableLayout
        mineField.removeAllViews();

        // set all variables to support end of game
        isTimerStarted = false;
        areMinesSet = false;
        isGameOver = false;
    }

    private void createMineField()
    {
        // we take one row extra row for each side
        // overall two extra rows and two extra columns
        // first and last row/column are used for calculations purposes only
        //	 x|xxxxxxxxxxxxxx|x
        //	 ------------------
        //	 x|              |x
        //	 x|              |x
        //	 ------------------
        //	 x|xxxxxxxxxxxxxx|x
        // the row and columns marked as x are just used to keep counts of near by mines

        blocks = new Block[numberOfRowsInMineField + 2][numberOfColumnsInMineField + 2];

        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                blocks[row][column] = new Block(this);
                blocks[row][column].setDefaults();

                // pass current row and column number as final int's to event listeners
                // this way we can ensure that each event listener is associated to
                // particular instance of block only
                final int currentRow = row;
                final int currentColumn = column;

                // add Click Listener
                // this is treated as Left Mouse click
                blocks[row][column].setOnClickListener(new OnClickListener()
                {

                    public void onClick(View view)
                    {
                        // start timer on first click
                        if (!isTimerStarted)
                        {
                            startTimer();
                            isTimerStarted = true;
                        }

                        // set mines on first click
                        if (!areMinesSet)
                        {
                            areMinesSet = true;
                            setMines(currentRow, currentColumn);
                        }

                        // this is not first click
                        // check if current block is flagged
                        // if flagged the don't do anything
                        // as that operation is handled by LongClick
                        // if block is not flagged then uncover nearby blocks
                        // till we get numbered mines
                        if (!blocks[currentRow][currentColumn].isFlagged())
                        {

                            // open nearby blocks till we get numbered blocks
                            rippleUncover(currentRow, currentColumn);

                            // did we clicked a mine
                            if (blocks[currentRow][currentColumn].hasMine())
                            {
                                // Oops, game over
                                finishGame(currentRow,currentColumn);
                            }

                            // check if we win the game
                            if (checkGameWin())
                            {
                                // mark game as win
                                winGame();
                            }
                        }
                    }
                });

                // add Long Click listener
                // this is treated as right mouse click listener
                blocks[row][column].setOnLongClickListener(new OnLongClickListener()
                {
                    public boolean onLongClick(View view)
                    {
                        // simulate a left-right (middle) click
                        // if it is a long click on an opened mine then
                        // open all surrounding blocks
                        if (!blocks[currentRow][currentColumn].isCovered() && (blocks[currentRow][currentColumn].getNumberOfMinesInSorrounding() > 0) && !isGameOver)
                        {
                            int nearbyFlaggedBlocks = 0;
                            for (int previousRow = -1; previousRow < 2; previousRow++)
                            {
                                for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                {
                                    if (blocks[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                    {
                                        nearbyFlaggedBlocks++;
                                    }
                                }
                            }

                            // if flagged block count is equal to nearby mine count
                            // then open nearby blocks
                            if (nearbyFlaggedBlocks == blocks[currentRow][currentColumn].getNumberOfMinesInSorrounding())
                            {
                                for (int previousRow = -1; previousRow < 2; previousRow++)
                                {
                                    for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                    {
                                        // don't open flagged blocks
                                        if (!blocks[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                        {
                                            // open blocks till we get numbered block
                                            rippleUncover(currentRow + previousRow, currentColumn + previousColumn);

                                            // did we clicked a mine
                                            if (blocks[currentRow + previousRow][currentColumn + previousColumn].hasMine())
                                            {
                                                // oops game over
                                                finishGame(currentRow + previousRow, currentColumn + previousColumn);
                                            }

                                            // did we win the game
                                            if (checkGameWin())
                                            {
                                                // mark game as win
                                                winGame();
                                            }
                                        }
                                    }
                                }
                            }

                            // as we no longer want to judge this gesture so return
                            // not returning from here will actually trigger other action
                            // which can be marking as a flag or question mark or blank
                            return true;
                        }

                        // if clicked block is enabled, clickable or flagged
                        if (blocks[currentRow][currentColumn].isClickable() &&
                                (blocks[currentRow][currentColumn].isEnabled() || blocks[currentRow][currentColumn].isFlagged()))
                        {


                            // for long clicks set:
                            // 1. empty blocks to flagged
                            // 2. flagged to question mark
                            // 3. question mark to blank

                            // case 1. set blank block to flagged
                            if (!blocks[currentRow][currentColumn].isFlagged() && !blocks[currentRow][currentColumn].isQuestionMarked())
                            {
                                blocks[currentRow][currentColumn].setBlockAsDisabled(false);
                                blocks[currentRow][currentColumn].setFlagIcon(true);
                                blocks[currentRow][currentColumn].setFlagged(true);
                            }
                            // case 2. set flagged to question mark
                            else if (!blocks[currentRow][currentColumn].isQuestionMarked())
                            {
                                blocks[currentRow][currentColumn].setBlockAsDisabled(true);
                                blocks[currentRow][currentColumn].setQuestionMarkIcon(true);
                                blocks[currentRow][currentColumn].setFlagged(false);
                                blocks[currentRow][currentColumn].setQuestionMarked(true);
                            }
                            // case 3. change to blank square
                            else
                            {
                                blocks[currentRow][currentColumn].setBlockAsDisabled(true);
                                blocks[currentRow][currentColumn].clearAllIcons();
                                blocks[currentRow][currentColumn].setQuestionMarked(false);
                                // if it is flagged then increment mine count
                                if (blocks[currentRow][currentColumn].isFlagged())
                                    blocks[currentRow][currentColumn].setFlagged(false);
                            }

                        }

                        return true;
                    }
                });
            }
        }
    }

    private boolean checkGameWin()
    {
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                if (!blocks[row][column].hasMine() && blocks[row][column].isCovered())
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void winGame()
    {
        stopTimer();
        isTimerStarted = false;
        isGameOver = true;
        //set icon to cool dude
        btnSmile.setBackgroundResource(R.drawable.cool);

        // disable all buttons
        // set flagged all un-flagged blocks
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                blocks[row][column].setClickable(false);
                if (blocks[row][column].hasMine())
                {
                    blocks[row][column].setBlockAsDisabled(false);
                    blocks[row][column].setFlagIcon(true);
                }
            }
        }

        // show message
        showDialog("You won in " + Integer.toString(secondsPassed) + " seconds!", 1000, false, true);
    }

    private void finishGame(int currentRow, int currentColumn)
    {
        isGameOver = true; // mark game as over
        stopTimer(); // stop timer
        isTimerStarted = false;
        btnSmile.setBackgroundResource(R.drawable.sad);

        // show all mines
        // disable all blocks
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                // disable block
                blocks[row][column].setBlockAsDisabled(false);

                // block has mine and is not flagged
                if (blocks[row][column].hasMine() && !blocks[row][column].isFlagged())
                {
                    // set mine icon
                    blocks[row][column].setMineIcon(false);
                }

                // block is flagged and doesn't not have mine
                if (!blocks[row][column].hasMine() && blocks[row][column].isFlagged())
                {
                    // set flag icon
                    blocks[row][column].setFlagIcon(false);
                }

                // block is flagged
                if (blocks[row][column].isFlagged())
                {
                    // disable the block
                    blocks[row][column].setClickable(false);
                }
            }
        }

        // trigger mine
        blocks[currentRow][currentColumn].triggerMine();

        // show message
        //showDialog("You tried for " + Integer.toString(secondsPassed) + " seconds!", 1000, false, false);
        recordScore(score, "Minesweeper" ,this);
    }

    private void setMines(int currentRow, int currentColumn)
    {
        // set mines excluding the location where user clicked
        Random rand = new Random();
        int mineRow, mineColumn;

        for (int row = 0; row < totalNumberOfMines; row++)
        {
            mineRow = rand.nextInt(numberOfColumnsInMineField);
            mineColumn = rand.nextInt(numberOfRowsInMineField);
            if ((mineRow + 1 != currentColumn) || (mineColumn + 1 != currentRow))
            {
                if (blocks[mineColumn + 1][mineRow + 1].hasMine())
                {
                    row--; // mine is already there, don't repeat for same block
                }
                // plant mine at this location
                blocks[mineColumn + 1][mineRow + 1].plantMine();
            }
            // exclude the user clicked location
            else
            {
                row--;
            }
        }

        int nearByMineCount;

        // count number of mines in surrounding blocks
        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                // for each block find nearby mine count
                nearByMineCount = 0;
                if ((row != 0) && (row != (numberOfRowsInMineField + 1)) && (column != 0) && (column != (numberOfColumnsInMineField + 1)))
                {
                    // check in all nearby blocks
                    for (int previousRow = -1; previousRow < 2; previousRow++)
                    {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                        {
                            if (blocks[row + previousRow][column + previousColumn].hasMine())
                            {
                                // a mine was found so increment the counter
                                nearByMineCount++;
                            }
                        }
                    }

                    blocks[row][column].setNumberOfMinesInSurrounding(nearByMineCount);
                }
                // for side rows (0th and last row/column)
                // set count as 9 and mark it as opened
                else
                {
                    blocks[row][column].setNumberOfMinesInSurrounding(9);
                    blocks[row][column].OpenBlock();
                }
            }
        }
    }

    private void rippleUncover(int rowClicked, int columnClicked)
    {
        if(blocks[rowClicked][columnClicked].isClickable())
            updateScore();

        blocks[rowClicked][columnClicked].setClickable(false);

        // don't open flagged or mined rows
        if (blocks[rowClicked][columnClicked].hasMine() || blocks[rowClicked][columnClicked].isFlagged())
        {
            return;
        }

        // open clicked block
        blocks[rowClicked][columnClicked].OpenBlock();

        // if clicked block have nearby mines then don't open further
        if (blocks[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0 )
        {
            return;
        }

        // open next 3 rows and 3 columns recursively
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 3; column++)
            {
                // check all the above checked conditions
                // if met then open subsequent blocks
                if (blocks[rowClicked + row - 1][columnClicked + column - 1].isCovered()
                        && (rowClicked + row - 1 > 0) && (columnClicked + column - 1 > 0)
                        && (rowClicked + row - 1 < numberOfRowsInMineField + 1) && (columnClicked + column - 1 < numberOfColumnsInMineField + 1))
                {
                    rippleUncover(rowClicked + row - 1, columnClicked + column - 1 );
                }
            }
        }
        return;
    }

    public void startTimer()
    {
        if (secondsPassed == 0)
        {
            timer.removeCallbacks(updateTimeElasped);
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer()
    {
        // disable call backs
        timer.removeCallbacks(updateTimeElasped);
    }

    public void updateScore()
    {
        score++;
        if (score < 10)
            txtScore.setText("00" + score);
        else if (score < 100)
            txtScore.setText("0" + score);
        else
            txtScore.setText("" + score);
    }

    // timer call back when timer is ticked
    private Runnable updateTimeElasped = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;

            if (secondsPassed < 10)
            {
                txtTimer.setText("00" + Integer.toString(secondsPassed));
            }
            else if (secondsPassed < 100)
            {
                txtTimer.setText("0" + Integer.toString(secondsPassed));
            }
            else
            {
                txtTimer.setText(Integer.toString(secondsPassed));
            }

            // add notification
            timer.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            timer.postDelayed(updateTimeElasped, 1000);
        }
    };

    private void showDialog(String message, int milliseconds, boolean useSmileImage, boolean useCoolImage)
    {
        // show message
        Toast dialog = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG);

        dialog.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout dialogView = (LinearLayout) dialog.getView();
        ImageView coolImage = new ImageView(getApplicationContext());
        if (useSmileImage)
        {
            coolImage.setImageResource(R.drawable.smile);
        }
        else if (useCoolImage)
        {
            coolImage.setImageResource(R.drawable.cool);
        }
        else
        {
            coolImage.setImageResource(R.drawable.sad);
        }
        dialogView.addView(coolImage, 0);
        dialog.setDuration(milliseconds);
        dialog.show();
    }

    public void recordScore(final int score, final String game, final Context context){
        final CharSequence[] choice = {getResources().getString(R.string.yes),getResources().getString(R.string.play_again), getResources().getString(R.string.quit)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.reach)+score+".\n"+getResources().getString(R.string.save));
        builder.setItems(choice, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // Save the result
                    case 0:
                        builder.setMessage(getResources().getString(R.string.username));
                        userPreferences  = getSharedPreferences("settings", 0);
                        final String username = userPreferences.getString("username", null);
                        final TextInputEditText input = new TextInputEditText(context);
                        input.setText(username);
                        builder.setView(input);
                        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String username = input.getText().toString();
                                DBHelper db = new DBHelper(context);
                                db.open();
                                db.insertRow(username, game, score);
                                db.insertOnline(username, game, score);
                                db.close();
                                final CharSequence[] items = {getResources().getString(R.string.restart),getResources().getString(R.string.exit)};
                                AlertDialog.Builder build = new AlertDialog.Builder(builder.getContext());
                                build.setTitle(getResources().getString(R.string.saveDone));
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
                        builder.setNegativeButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
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