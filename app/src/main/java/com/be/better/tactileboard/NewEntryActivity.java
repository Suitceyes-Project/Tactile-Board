package com.be.better.tactileboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.HashMap;
import java.util.List;


public class NewEntryActivity extends AppCompatActivity {

    protected static EditText newWord;
    private Button wordAdded;
    private Button showDict;
    private String haptogram = "";
    private PatternLockView patternView;
    private HashMap<String, String> dict;

    private boolean validHaptogram = true;

    private PatternLockViewListener patternListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(patternView, progressPattern));

            newWord.setText("");
            newWord.setHint(R.string.new_word);
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            haptogram = MPatternLockUtils.patternToString(patternView, pattern);

            checkHaptogram(haptogram);
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
            haptogram = "";
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_word);

        Intent i = getIntent();
        HashMap<String, String> map = new HashMap<String, String>((HashMap<String, String>) i.getSerializableExtra("dict"));
        if(map == null) {
            dict = new HashMap<String, String >();
        } else {
            dict = map;
        }

        newWord = (EditText) findViewById(R.id.newWord);

        patternView = (PatternLockView) findViewById(R.id.pattern_lock_view);

        patternView.setDotCount(4);
        //Tablet - Margin 40dp each side
        //patternView.setDotNormalSize(70);
        //patternView.setDotSelectedSize(70);

        //Smartphone - Margin none
        patternView.setDotNormalSize(40);
        patternView.setDotSelectedSize(40);

        patternView.setInputEnabled(true);
        patternView.setDotAnimationDuration(50);
        patternView.setPathEndAnimationDuration(50);
        patternView.setInStealthMode(false);
        patternView.setTactileFeedbackEnabled(false);
        patternView.addPatternLockListener(patternListener);

        wordAdded = (Button) findViewById(R.id.button);
        wordAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean entryEntered, haptogramEntered;
                String entry = newWord.getText().toString();
                entry = entry.toLowerCase();

                boolean validWord = checkWord(entry);

                if(entryEntered = entry.equals("") || entry.equals(R.string.new_word)) {
                    Toast.makeText(NewEntryActivity.this, R.string.no_word,
                            Toast.LENGTH_LONG).show();
                    entry = "";
                }

                if(haptogramEntered = haptogram.equals("")) {
                    Toast.makeText(NewEntryActivity.this, R.string.no_pattern,
                            Toast.LENGTH_LONG).show();
                }

                if (!validWord) {
                    patternView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                    if(!validHaptogram) {
                        Toast.makeText(NewEntryActivity.this, R.string.pattern_exists,
                                Toast.LENGTH_LONG).show();
                        newWord.setText("");
                        checkHaptogram(haptogram);
                    } else {
                        Toast.makeText(NewEntryActivity.this, R.string.word_exists,
                                Toast.LENGTH_LONG).show();
                        String existingVal = dict.get(entry);
                        showHaptogram(existingVal);
                    }
                }

                if(validHaptogram && validWord && !entryEntered && !haptogramEntered) {

                    Toast.makeText(NewEntryActivity.this, R.string.word_inserted,
                            Toast.LENGTH_LONG).show();

                    dict.put(entry, haptogram);

                    haptogram = "";
                    entry = "";
                    newWord.setHint(R.string.new_word);
                    patternView.clearPattern();

                    Intent i = new Intent(NewEntryActivity.this, MainActivity.class);
                    i.putExtra("dict", dict);
                    startActivity(i);
                }
            }
        });

        showDict = (Button) findViewById(R.id.showDict);
        showDict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NewEntryActivity.this, ShowDict.class);
                i.putExtra("dict", dict);
                startActivity(i);
            }
        });

    }

    private void checkHaptogram(String haptogram) {
        if(!dict.isEmpty() && !haptogram.equals("")) {
            validHaptogram = !dict.containsValue(haptogram);
        } else {
            validHaptogram = true;
        }

        if(validHaptogram) {
            patternView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        } else {
            patternView.setViewMode(PatternLockView.PatternViewMode.WRONG);
            showWord(haptogram);
            this.haptogram = "";
        }
    }

    private boolean checkWord(String entry) {
        if(!dict.isEmpty()) {
            return !dict.containsKey(entry);
        }
        return true;
    }

    private void showWord(String haptogram) {
        String key = MainActivity.dict.getKey(haptogram);
        newWord.setText("");
        newWord.setHint("Meaning: " + key);
    }

    private void showHaptogram(String haptogram) {

        long dots = MPatternLockUtils.stringToPattern(patternView, haptogram).size();
        long sec = dots * 1000;

        AutoDraw thread = new AutoDraw(patternView, haptogram, sec);
        thread.start();

        this.haptogram = "";
    }

    @Override
    public void onBackPressed() {
        haptogram = "";
        newWord.setHint(R.string.new_word);
        patternView.clearPattern();

        Intent i = new Intent(NewEntryActivity.this, MainActivity.class);
        i.putExtra("dict", dict);
        startActivity(i);
    }
}
