package com.example.gamesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FlagQuiz extends AppCompatActivity {
    private static final String TAG = "FlagQuizGame Activity";
    private List<String> fileNameList; // flag file names
    private List<String> quizCountriesList;
    private Map<String, Boolean> regionsMap;
    private String correctAnswer;
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses
    private int guessRows;
    private Random random;
    private Handler handler;
    private Animation shakeAnimation;

    private TextView answerTextView;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private TableLayout buttonTableLayout;
    private ImageButton info;

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
        setContentView(R.layout.activity_flag_quiz);

        fileNameList = new ArrayList<String>();
        quizCountriesList = new ArrayList<String>();
        regionsMap = new HashMap<String, Boolean>();
        guessRows = 2;
        random = new Random();
        handler = new Handler();

        info = (ImageButton)findViewById(R.id.button_play);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(FlagQuiz.this);
                myAlert.setTitle(R.string.tutorial);
                myAlert.setMessage(R.string.flag_desc);
                myAlert.show();
            }
        });

        shakeAnimation =
                AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); String[] regionNames =
            getResources().getStringArray(R.array.regionsList);
        for (String region : regionNames )
            regionsMap.put(region, true);
        questionNumberTextView =
                (TextView) findViewById(R.id.questionNumberTextView);
        flagImageView = (ImageView) findViewById(R.id.flagImageView);
        buttonTableLayout =
                (TableLayout) findViewById(R.id.buttonTableLayout);
        answerTextView = (TextView) findViewById(R.id.answerTextView);
        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " 1 " +
                        getResources().getString(R.string.of) + " 10");

        resetQuiz();
    }
    private void resetQuiz()
    {
        AssetManager assets = getAssets();
        fileNameList.clear();

        try
        {
            Set<String> regions = regionsMap.keySet();

            for (String region : regions)
            {
                if (regionsMap.get(region))
                {               String[] paths = assets.list(region);

                    for (String path : paths)
                        fileNameList.add(path.replace(".png", ""));
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error loading image file names", e);
        }

        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();
        while (flagCounter <= 10)
        {
            int randomIndex = random.nextInt(numberOfFlags);
            String fileName = fileNameList.get(randomIndex);
            if (!quizCountriesList.contains(fileName))
            {
                quizCountriesList.add(fileName);
                ++flagCounter;
            }}
        loadNextFlag();
    }
    private void loadNextFlag()
    {
        String nextImageName = quizCountriesList.remove(0);
        correctAnswer = nextImageName;

        answerTextView.setText("");
        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " " +
                        (correctAnswers + 1) + " " +
                        getResources().getString(R.string.of) + " 10");
        String region =
                nextImageName.substring(0, nextImageName.indexOf('-'));
        AssetManager assets = getAssets(); // get app's AssetManager
        InputStream stream;
        try
        {
            stream = assets.open(region + "/" + nextImageName + ".png");

            Drawable flag = Drawable.createFromStream(stream, nextImageName);
            flagImageView.setImageDrawable(flag);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error loading " + nextImageName, e);
        }
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();

        Collections.shuffle(fileNameList);

        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);


        for (int row = 0; row < guessRows; row++)
        {
            TableRow currentTableRow = getTableRow(row);

            for (int column = 0; column < 3; column++)
            {
                Button newGuessButton =
                        (Button) inflater.inflate(R.layout.activity_guess_button, null);
                String fileName = fileNameList.get((row * 3) + column);
                newGuessButton.setText(getCountryName(fileName));
                newGuessButton.setOnClickListener(guessButtonListener);
                currentTableRow.addView(newGuessButton);
            }
        }
        int row = random.nextInt(guessRows);
        int column = random.nextInt(3);
        TableRow randomTableRow = getTableRow(row);
        String countryName = getCountryName(correctAnswer);
        ((Button)randomTableRow.getChildAt(column)).setText(countryName);
    }
    private TableRow getTableRow(int row)
    {
        return (TableRow) buttonTableLayout.getChildAt(row);
    }
    private String getCountryName(String name)
    {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }
    private void submitGuess(Button guessButton)
    {
        String guess = guessButton.getText().toString();
        String answer = getCountryName(correctAnswer);
        ++totalGuesses;
        if (guess.equals(answer))
        {
            ++correctAnswers;
            answerTextView.setText(answer + "!");
            answerTextView.setTextColor(
                    getResources().getColor(R.color.correct_answer));

            disableButtons();
            if (correctAnswers == 10)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.your_score);

                int score=(int) (1000 / totalGuesses);

                recordScore(score, "Flag Quiz" ,this);

                builder.setMessage(String.format("%s %d %s, %d up to 100 ",
                        getResources().getString(R.string.correct),
                        totalGuesses,
                        getResources().getString(R.string.guesses),
                        score));

                builder.setCancelable(false);
                builder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                resetQuiz();
                            }
                        }
                );
                AlertDialog resetDialog = builder.create();
                resetDialog.show();
            }
            else
            {  handler.postDelayed(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            loadNextFlag();
                        }
                    }, 1000);
            }
        }
        else
        {  flagImageView.startAnimation(shakeAnimation);
            answerTextView.setText(R.string.incorrect_answer);
            answerTextView.setTextColor(
                    getResources().getColor(R.color.incorrect_answer));
            guessButton.setEnabled(false);
        }
    }

    private void disableButtons()
    {
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
        {
            TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); ++i)
                tableRow.getChildAt(i).setEnabled(false);
        }
    }
    private final int CHOICES_MENU_ID = Menu.FIRST;
    private final int REGIONS_MENU_ID = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, CHOICES_MENU_ID, Menu.NONE, R.string.choices);
        menu.add(Menu.NONE, REGIONS_MENU_ID, Menu.NONE, R.string.regions);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case CHOICES_MENU_ID:
                final String[] possibleChoices =
                        getResources().getStringArray(R.array.guessesList);

                AlertDialog.Builder choicesBuilder =
                        new AlertDialog.Builder(this);
                choicesBuilder.setTitle(R.string.choices);

                choicesBuilder.setItems(R.array.guessesList,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int item)
                            {
                                guessRows = Integer.parseInt(
                                        possibleChoices[item].toString()) / 3;
                                resetQuiz();
                            }
                        }
                );
                AlertDialog choicesDialog = choicesBuilder.create();
                choicesDialog.show();
                return true;

            case REGIONS_MENU_ID:
                final String[] regionNames =
                        regionsMap.keySet().toArray(new String[regionsMap.size()]);

                boolean[] regionsEnabled = new boolean[regionsMap.size()];
                for (int i = 0; i < regionsEnabled.length; ++i)
                    regionsEnabled[i] = regionsMap.get(regionNames[i]);
                AlertDialog.Builder regionsBuilder =
                        new AlertDialog.Builder(this);
                regionsBuilder.setTitle(R.string.regions);

                String[] displayNames = new String[regionNames.length];
                for (int i = 0; i < regionNames.length; ++i)
                    displayNames[i] = regionNames[i].replace('_', ' ');

                regionsBuilder.setMultiChoiceItems(
                        displayNames, regionsEnabled,
                        new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked)
                            {
                                regionsMap.put(
                                        regionNames[which].toString(), isChecked);
                            }
                        }
                );

                regionsBuilder.setPositiveButton(R.string.your_score,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int button)
                            {
                                resetQuiz();
                            }
                        }
                );
                AlertDialog regionsDialog = regionsBuilder.create();
                regionsDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private View.OnClickListener guessButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            submitGuess((Button) v);
        }
    };

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
