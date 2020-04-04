package com.example.gamesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;

public class MathGame extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private long mStartTime;
    private int score;
    final static String youwon1 = ":)";
    final static String youwon2 = "you";
    final static String youwon3 = "won";
    final static String youwon4 = "!!";
    final static String PLUS = "x+y";
    final static String MINUS = "x-y";
    final static String MULTI = "x*y";
    final static String DIVIDE = "x/y";
    final static String SQUARE = "x*x";
    final static String CUBE = "x*x*x";
    final static String SQUAREROOT = "squareroot";
    private String CURRENTMODE = PLUS;
    final static int CEILINGRESET = 10000;
    final static int CEILING = 1000;
    final static int CEILINGHALF = CEILING/2;
    private Random myRandom = new Random();
    private int myCurrentSum;
    private int targetInt;
    private String localTxt = "A";
    private int bttnValueInt;
    private boolean acceptInput = true;
    SharedPreferences userPreferences;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_mathgame);
        splash(null);
        ImageButton info = (ImageButton)findViewById(R.id.button_play);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(MathGame.this);
                myAlert.setTitle(R.string.tutorial);
                myAlert.setMessage(R.string.math_desc);
                myAlert.show();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        splash(null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            final TextView mTimeLabel = (TextView)findViewById(R.id.elapsedtime);
            long millis = SystemClock.uptimeMillis() - mStartTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;
            int secondsb = seconds - 2;
            if (secondsb < 0 ) {secondsb = 0;}
            if (seconds < 10) {
                if (secondsb > seconds) { mTimeLabel.setText("" + minutes + ":0" + secondsb); }
                else { mTimeLabel.setText("" + minutes + ":0" + seconds);}
            } else { mTimeLabel.setText("" + minutes + ":" + seconds);}

            mHandler.postAtTime(this,
                    mStartTime + (((minutes * 60) + seconds + 1) * 1000));
        }
    };


    public void splash(View view) {

        final TextView additionArrow = (TextView)findViewById(R.id.additionarrow);
        ViewGroup.LayoutParams additionArrowParams = additionArrow.getLayoutParams();
        additionArrowParams.height = 0;
        additionArrow.setLayoutParams(additionArrowParams);
        final TextView subtractionArrow = (TextView)findViewById(R.id.subtractionarrow);
        ViewGroup.LayoutParams subtractionArrowParams = subtractionArrow.getLayoutParams();
        subtractionArrowParams.height = 0;
        subtractionArrow.setLayoutParams(subtractionArrowParams);
        final TextView multiplicationArrow = (TextView)findViewById(R.id.multiplicationarrow);
        ViewGroup.LayoutParams multiplicationArrowParams = multiplicationArrow.getLayoutParams();
        multiplicationArrowParams.height = 0;
        multiplicationArrow.setLayoutParams(multiplicationArrowParams);
        final TextView divisionArrow = (TextView)findViewById(R.id.divisionarrow);
        ViewGroup.LayoutParams divisionArrowParams = divisionArrow.getLayoutParams();
        divisionArrowParams.height = 0;
        divisionArrow.setLayoutParams(divisionArrowParams);

        Animation scrollLeft = AnimationUtils.loadAnimation(this, R.anim.splashscrollleft);
        Animation scrollRight = AnimationUtils.loadAnimation(this, R.anim.splashscrollright);
        final TextView tv1 = (TextView)findViewById(R.id.targetnumberlabel);
        final TextView tv2 = (TextView)findViewById(R.id.targetnumber);
        final TextView tv3 = (TextView)findViewById(R.id.currentsumlabel);
        final TextView tv4 = (TextView)findViewById(R.id.currentnumber);
        final TextView tv5 = (TextView)findViewById(R.id.elapsedtimelabel);
        final TextView tv6 = (TextView)findViewById(R.id.elapsedtime);
        final TextView tv9 = (TextView)findViewById(R.id.newgame);
        final TextView tv11 = (TextView)findViewById(R.id.mathmath);
        final TextView tv12 = (TextView)findViewById(R.id.author);
        final TextView tv13 = (TextView)findViewById(R.id.addition);
        final TextView tv14 = (TextView)findViewById(R.id.subtraction);
        final TextView tv15 = (TextView)findViewById(R.id.multiplication);
        final TextView tv16 = (TextView)findViewById(R.id.division);
        final TextView tv17 = (TextView)findViewById(R.id.square);
        final TextView tv18 = (TextView)findViewById(R.id.cube);
        final TextView tv19 = (TextView)findViewById(R.id.squareroot);
        final TextView tv20 = (TextView)findViewById(R.id.copyright);
        final TextView tv21 = (TextView)findViewById(R.id.quote);
        tv1.startAnimation(scrollLeft);
        tv2.startAnimation(scrollRight);
        tv3.startAnimation(scrollLeft);
        tv4.startAnimation(scrollRight);
        tv5.startAnimation(scrollLeft);
        tv6.startAnimation(scrollRight);
        tv9.startAnimation(scrollRight);
        tv11.startAnimation(scrollRight);
        tv12.startAnimation(scrollLeft);
        tv12.startAnimation(scrollRight);
        tv13.startAnimation(scrollRight);
        tv14.startAnimation(scrollRight);
        tv15.startAnimation(scrollRight);
        tv16.startAnimation(scrollRight);
        tv17.startAnimation(scrollRight);
        tv18.startAnimation(scrollRight);
        tv19.startAnimation(scrollRight);
        tv20.startAnimation(scrollRight);
        tv21.startAnimation(scrollRight);
        setall(null);
    }
    public void setall(View view) {
        myCurrentSum = 0;
        targetInt = myRandom.nextInt(CEILING);
        final TextView targetNumber = (TextView)findViewById(R.id.targetnumber);
        final TextView currentNumber = (TextView)findViewById(R.id.currentnumber);
        currentNumber.setText(String.valueOf(0));
        targetNumber.setText(String.valueOf(targetInt));
        CURRENTMODE = PLUS;
        youWonToggle(false);
        setFlock();
        mStartTime = SystemClock.uptimeMillis();
        mUpdateTimeTask.run();
    }

    public void setFlock(){
        bump1(2000, false);
        bump2(1500, false);
        bump3(1000, false);
        bump4(targetInt, false);
        if (targetInt % 128 == 0) {bump5(128, true);}
        else { bump5(512, false);}
        bump6(targetInt, false);
        bump7(targetInt, false);
        bump8(targetInt, false);
        bump9(1000, false);
        bump10(targetInt, false);
        bump11(250, false);
        bump12(2, true);
        bump13(targetInt, false);
        bump14(targetInt, false);
        bump15(targetInt, false);
        bump16(1, true);
        bump17(targetInt, false);
        bump18(100, false);
        bump19(20, false);
        bump20(0, true);
    }


    public void chooseModeOp(View view) {
        int h = 40;
        final TextView currentNumber = (TextView)findViewById(R.id.currentnumber);
        if (Integer.parseInt((String) currentNumber.getText()) > CEILINGRESET)
        {  CURRENTMODE = MINUS; }
        final TextView additionArrow = (TextView)findViewById(R.id.additionarrow);
        ViewGroup.LayoutParams additionArrowParams = additionArrow.getLayoutParams();
        additionArrowParams.height = 0;
        additionArrow.setLayoutParams(additionArrowParams);
        final TextView subtractionArrow = (TextView)findViewById(R.id.subtractionarrow);
        ViewGroup.LayoutParams subtractionArrowParams = subtractionArrow.getLayoutParams();
        subtractionArrowParams.height = 0;
        subtractionArrow.setLayoutParams(subtractionArrowParams);
        final TextView multiplicationArrow = (TextView)findViewById(R.id.multiplicationarrow);
        ViewGroup.LayoutParams multiplicationArrowParams = multiplicationArrow.getLayoutParams();
        multiplicationArrowParams.height = 0;
        multiplicationArrow.setLayoutParams(multiplicationArrowParams);
        final TextView divisionArrow = (TextView)findViewById(R.id.divisionarrow);
        ViewGroup.LayoutParams divisionArrowParams = divisionArrow.getLayoutParams();
        divisionArrowParams.height = 0;
        divisionArrow.setLayoutParams(divisionArrowParams);
        switch (view.getId()) {
            case R.id.addition:
                CURRENTMODE=PLUS;
                additionArrowParams.height = h;
                additionArrow.setLayoutParams(additionArrowParams);
                break;
            case R.id.subtraction:
                CURRENTMODE=MINUS;
                subtractionArrowParams.height = h;
                subtractionArrow.setLayoutParams(subtractionArrowParams);
                break;
            case R.id.multiplication:
                CURRENTMODE=MULTI;
                multiplicationArrowParams.height = h;
                multiplicationArrow.setLayoutParams(multiplicationArrowParams);
                break;
            case R.id.division:
                CURRENTMODE=DIVIDE;
                divisionArrowParams.height = h;
                divisionArrow.setLayoutParams(divisionArrowParams);
                break;
            case R.id.square:
                CURRENTMODE=SQUARE;
                exponentMe();
                CURRENTMODE=PLUS;
                additionArrowParams.height = h;
                additionArrow.setLayoutParams(additionArrowParams);
                break;
            case R.id.cube:
                CURRENTMODE=CUBE;
                exponentMe();
                CURRENTMODE=PLUS;
                additionArrowParams.height = h;
                additionArrow.setLayoutParams(additionArrowParams);
                break;
            case R.id.squareroot:
                CURRENTMODE=SQUAREROOT;
                exponentMe();
                CURRENTMODE=PLUS;
                additionArrowParams.height = h;
                additionArrow.setLayoutParams(additionArrowParams);
                break;
            default: CURRENTMODE=PLUS;
        }
    }

    public void exponentMe() {
        if (CURRENTMODE.toLowerCase() == SQUARE) { myCurrentSum = myCurrentSum * myCurrentSum; }
        if (CURRENTMODE.toLowerCase() == CUBE) { myCurrentSum = myCurrentSum * myCurrentSum * myCurrentSum; }
        if (CURRENTMODE.toLowerCase() == SQUAREROOT) { myCurrentSum = Math.round((float)Math.sqrt(myCurrentSum)); }
        final TextView currentNumber = (TextView)findViewById(R.id.currentnumber);
        currentNumber.setText(String.valueOf( myCurrentSum));
        eyewon(null);
    }


    public void processIntegerInput(View view) {
        if (acceptInput == true) {
            hitceiling();
            Animation fasterscrollRight = AnimationUtils.loadAnimation(this, R.anim.fastersplashscrollright);
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animhyperspacejump);
            final TextView currentNumber = (TextView)findViewById(R.id.currentnumber);
            switch (view.getId()) {
                case R.id.movingBttn1:
                    final TextView tv1 = (TextView)findViewById(R.id.movingBttn1);
                    localTxt = String.valueOf(tv1.getText());
                    tv1.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(1);
                    break;
                case R.id.movingBttn2:
                    final TextView tv2 = (TextView)findViewById(R.id.movingBttn2);
                    localTxt = String.valueOf(tv2.getText());
                    tv2.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(2);
                    break;
                case R.id.movingBttn3:
                    final TextView tv3 = (TextView)findViewById(R.id.movingBttn3);
                    localTxt = String.valueOf(tv3.getText());
                    tv3.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(3);
                    break;
                case R.id.movingBttn4:
                    final TextView tv4 = (TextView)findViewById(R.id.movingBttn4);
                    localTxt = String.valueOf(tv4.getText());
                    tv4.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(4);
                    break;
                case R.id.movingBttn5:
                    final TextView tv5 = (TextView)findViewById(R.id.movingBttn5);
                    localTxt = String.valueOf(tv5.getText());
                    tv5.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(5);
                    break;
                case R.id.movingBttn6:
                    final TextView tv6 = (TextView)findViewById(R.id.movingBttn6);
                    localTxt = String.valueOf(tv6.getText());
                    tv6.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(6);
                    break;
                case R.id.movingBttn7:
                    final TextView tv7 = (TextView)findViewById(R.id.movingBttn7);
                    localTxt = String.valueOf(tv7.getText());
                    tv7.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(7);
                    break;
                case R.id.movingBttn8:
                    final TextView tv8 = (TextView)findViewById(R.id.movingBttn8);
                    localTxt = String.valueOf(tv8.getText());
                    tv8.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(8);
                    break;
                case R.id.movingBttn9:
                    final TextView tv9 = (TextView)findViewById(R.id.movingBttn9);
                    localTxt = String.valueOf(tv9.getText());
                    tv9.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(9);
                    break;
                case R.id.movingBttn10:
                    final TextView tv10 = (TextView)findViewById(R.id.movingBttn10);
                    localTxt = String.valueOf(tv10.getText());
                    tv10.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(10);
                    break;
                case R.id.movingBttn11:
                    final TextView tv11 = (TextView)findViewById(R.id.movingBttn11);
                    localTxt = String.valueOf(tv11.getText());
                    tv11.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(11);
                    break;
                case R.id.movingBttn12:
                    final TextView tv12 = (TextView)findViewById(R.id.movingBttn12);
                    localTxt = String.valueOf(tv12.getText());
                    tv12.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(12);
                    break;
                case R.id.movingBttn13:
                    final TextView tv13 = (TextView)findViewById(R.id.movingBttn13);
                    localTxt = String.valueOf(tv13.getText());
                    tv13.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(13);
                    break;
                case R.id.movingBttn14:
                    final TextView tv14 = (TextView)findViewById(R.id.movingBttn14);
                    localTxt = String.valueOf(tv14.getText());
                    tv14.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(14);
                    break;
                case R.id.movingBttn15:
                    final TextView tv15 = (TextView)findViewById(R.id.movingBttn15);
                    localTxt = String.valueOf(tv15.getText());
                    tv15.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(15);
                    break;
                case R.id.movingBttn16:
                    final TextView tv16 = (TextView)findViewById(R.id.movingBttn16);
                    localTxt = String.valueOf(tv16.getText());
                    tv16.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(16);
                    break;
                case R.id.movingBttn17:
                    final TextView tv17 = (TextView)findViewById(R.id.movingBttn17);
                    localTxt = String.valueOf(tv17.getText());
                    tv17.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(17);
                    break;
                case R.id.movingBttn18:
                    final TextView tv18 = (TextView)findViewById(R.id.movingBttn18);
                    localTxt = String.valueOf(tv18.getText());
                    tv18.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(18);
                    break;
                case R.id.movingBttn19:
                    final TextView tv19 = (TextView)findViewById(R.id.movingBttn19);
                    localTxt = String.valueOf(tv19.getText());
                    tv19.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(19);
                    break;
                case R.id.movingBttn20:
                    final TextView tv20 = (TextView)findViewById(R.id.movingBttn20);
                    localTxt = String.valueOf(tv20.getText());
                    tv20.startAnimation(hyperspaceJumpAnimation);
                    cellularAutomata(20);
                    break;

            }

            bttnValueInt=Integer.parseInt(localTxt);
            if (CURRENTMODE.toLowerCase() == PLUS) { myCurrentSum = myCurrentSum + bttnValueInt; }
            else if (CURRENTMODE.toLowerCase() == MINUS) { myCurrentSum = myCurrentSum - bttnValueInt; }
            else if (CURRENTMODE.toLowerCase() == MULTI) { myCurrentSum = myCurrentSum * bttnValueInt; }
            else if (CURRENTMODE.toLowerCase() == DIVIDE) { myCurrentSum = myCurrentSum / bttnValueInt; }
            currentNumber.setText(String.valueOf( myCurrentSum));
            currentNumber.startAnimation(fasterscrollRight);
            eyewon(null);
        }
    }

    public void cellularAutomata(int input) {
        switch (input) {
            case 1:
                bump2(1500, false);
                break;
            case 2:
                bump3(1000, false);
                break;
            case 3:
                bump4(targetInt, false);
                break;
            case 4:
                if (targetInt % 128 == 0) {bump5(128, true);}
                else { bump5(512, false);}
                break;
            case 5:
                bump6(targetInt, false);
                break;
            case 6:
                bump7(targetInt, false);
                break;
            case 7:
                bump8(targetInt, false);
                break;
            case 8:
                bump9(1000, false);
                break;
            case 9:
                bump10(targetInt, false);
                break;
            case 10:
                bump11(100, false);
                break;
            case 11:
                bump12(2, true);
                break;
            case 12:
                bump13(targetInt, false);
                break;
            case 13:
                bump14(targetInt, false);
                break;
            case 14:
                bump15(targetInt, false);
                break;
            case 15:
                bump16(1, true);
                break;
            case 16:
                bump17(targetInt, false);
                break;
            case 17:
                bump18(200, false);
                break;
            case 18:
                bump19(100, false);
                break;
            case 19:
                bump20(0, true);
                break;
            case 20:
                bump1(2000, false);
                break;
        }
        condition1(input);
    }


    public void condition1(int input) {
        if (input < 11) {changeUpperHalf();}
        else {changeLowerHalf();}
    }

    public void changeLowerHalf() {
        bump1(2000, false);
        bump2(1500, false);
        bump3(1000, false);
        bump4(targetInt, false);
        if (targetInt % 128 == 0) {bump5(128, true);}
        else { bump5(512, false);}
        bump6(targetInt, false);
        bump7(targetInt, false);
        bump8(targetInt, false);
        bump9(1000, false);
        bump10(targetInt, false);
    }

    public void changeUpperHalf() {
        bump11(250, false);
        bump12(250, false);
        bump13(targetInt, false);
        bump14(targetInt, false);
        bump15(targetInt, false);
        bump16(100, false);
        bump17(100, false);
        bump18(100, false);
        bump19(100, false);
        bump20(100, false);
    }

    public void bump1(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn1);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump2(int input, boolean force) {
        Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
        final TextView tv = (TextView)findViewById(R.id.movingBttn2);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon2));
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            tv.startAnimation(flockscrollRight);}
    }
    public void bump3(int input, boolean force) {
        Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
        final TextView tv = (TextView)findViewById(R.id.movingBttn3);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon3));
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            tv.startAnimation(flockscrollRight); }
    }
    public void bump4(int input, boolean force) {
        Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
        final TextView tv = (TextView)findViewById(R.id.movingBttn4);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon4));
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            tv.startAnimation(flockscrollRight);}
    }
    public void bump5(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn5);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump6(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn6);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump7(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn7);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump8(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn8);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump9(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn9);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump10(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn10);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump11(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn11);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump12(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn12);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump13(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn13);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump14(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn14);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump15(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn15);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump16(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn16);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump17(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn17);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump18(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn18);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump19(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn19);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }
    public void bump20(int input, boolean force) {
        final TextView tv = (TextView)findViewById(R.id.movingBttn20);
        if (acceptInput == false) {
            tv.setText(String.valueOf(youwon1));
            Animation rotateNinety = AnimationUtils.loadAnimation(this, R.anim.rotatezerotoninety);
            tv.startAnimation(rotateNinety);
        } else {
            if (force == true) {tv.setText(String.valueOf(input));}
            else {tv.setText(String.valueOf(myRandom.nextInt(input)));}
            Animation flockscrollRight = AnimationUtils.loadAnimation(this, R.anim.flockscrollright);
            tv.startAnimation(flockscrollRight);}
    }

    public void eyewon(View view) {
        final TextView targetNumber = (TextView)findViewById(R.id.targetnumber);
        final TextView currentNumber = (TextView)findViewById(R.id.currentnumber);
        final TextView mTime = (TextView)findViewById(R.id.elapsedtime);
        if (Integer.parseInt((String) currentNumber.getText()) == Integer.parseInt((String) targetNumber.getText()))
        {   youWonToggle(true);
            mHandler.removeCallbacks(mUpdateTimeTask);
            String time = mTime.getText().toString();
            String[] gameTime= time.split(":");
            score = 1003 - ((parseInt(gameTime[0])*22 + (parseInt(gameTime[1]) )) * 3);
            if (score>0)
                recordScore(score, "Math Game", this);
        }
    }

    public void hitceiling() {
        final TextView currentNumber = (TextView)findViewById(R.id.currentnumber);
        if (Integer.parseInt((String) currentNumber.getText()) > CEILINGRESET)
        {  CURRENTMODE = MINUS; }
    }

    public void youWonToggle(boolean input) {
        if (input == true) {
            acceptInput = false;
            condition1(1);
            condition1(15);
        } else { acceptInput = true;}
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
