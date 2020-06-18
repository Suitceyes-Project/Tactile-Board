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
    private PatternLockView patternView;
    private Button clearButton;
    private HashMap<String, String> dict;
    private StringBuilder patternBuilder = new StringBuilder();

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
            // Check if there is a word with the current pattern
            // if yes, update hint
            String haptogram = MPatternLockUtils.patternToString(patternView, pattern);

            if(haptogram.isEmpty())
                return;

            if(patternBuilder.length() > 0)
                patternBuilder.append(',');

            patternBuilder.append(haptogram);
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
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
        clearButton = (Button) findViewById(R.id.clearButton);
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
                String haptogram = patternBuilder.toString();
                String entry = newWord.getText().toString();
                entry = entry.toLowerCase();

                // Check if word was entered for haptogram
                if(entry.isEmpty() || entry.equals(R.string.new_word)) {
                    Toast.makeText(NewEntryActivity.this, R.string.no_word,
                            Toast.LENGTH_LONG).show();
                    entry = "";
                    return;
                }
                 // Check if haptogram itself was empty
                if(haptogram.isEmpty()) {
                    Toast.makeText(NewEntryActivity.this, R.string.no_pattern,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if entry already exists in dict
                if (!isValidWord(entry)) {
                    patternView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    Toast.makeText(NewEntryActivity.this, R.string.word_exists,
                            Toast.LENGTH_LONG).show();
                    String existingVal = dict.get(entry);
                    showHaptogram(existingVal);
                    return;
                }

                // Check if the haptogram already exists.
                if(!isValidHaptogram(haptogram))
                {
                    Toast.makeText(NewEntryActivity.this, R.string.pattern_exists,
                            Toast.LENGTH_LONG).show();
                    newWord.setText("");
                    patternView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    showWord(haptogram);
                    patternBuilder.setLength(0);
                    return;
                }

                // If we get here the haptogram and word is valid and we can add the entry.
                Toast.makeText(NewEntryActivity.this, R.string.word_inserted,
                        Toast.LENGTH_LONG).show();

                dict.put(entry, haptogram);
                patternView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                patternBuilder.setLength(0);
                entry = "";
                newWord.setHint(R.string.new_word);
                patternView.clearPattern();

                // Go back to main activity
                Intent i = new Intent(NewEntryActivity.this, MainActivity.class);
                i.putExtra("dict", dict);
                startActivity(i);
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

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patternBuilder.setLength(0);
            }
        });
    }

    private Boolean isValidHaptogram(String haptogram) {
        if(haptogram.isEmpty())
            return false;

        if(dict.isEmpty() || dict.containsValue(haptogram))
            return false;

        return true;
    }

    private boolean isValidWord(String entry) {
        if(entry.isEmpty())
            return false;

        if(dict.isEmpty() || dict.containsKey(entry))
            return false;

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

        patternBuilder.setLength(0);
    }

    @Override
    public void onBackPressed() {
        patternBuilder.setLength(0);
        newWord.setHint(R.string.new_word);
        patternView.clearPattern();

        Intent i = new Intent(NewEntryActivity.this, MainActivity.class);
        i.putExtra("dict", dict);
        startActivity(i);
    }
}
