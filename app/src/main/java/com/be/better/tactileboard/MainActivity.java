package com.be.better.tactileboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView word;
    private String writtenTranslation = "";
    private ImageButton textToSpeechButton;
    private Button addWord;
    private Button clearButton;
    private Button completeButton;
    private PatternLockView patternView;
    private MessageManager messageManager;
    private List<PatternLockView.Dot> pattern;
    private TextToSpeech textToSpeech;
    private StringBuilder patternBuilder = new StringBuilder();
    protected static Dict dict;

    protected static SharedPreferences prefs;
    protected static SharedPreferences.Editor editor;

    private PatternLockViewListener patternListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(patternView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            String haptogram = MPatternLockUtils.patternToString(patternView, pattern);

            if(haptogram.isEmpty())
                return;

            patternBuilder.append(haptogram);
            //translateHaptogram(pattern);
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
        setContentView(R.layout.activity_main);

        if(messageManager == null) {
            messageManager = new MessageManager();
        }

        if(dict == null) {
            SharedPreferences prefs = getSharedPreferences("DICT", Context.MODE_PRIVATE);
            dict = new Dict(prefs);
        } else {
            Intent i = getIntent();
            HashMap<String, String> hashDict = (HashMap<String, String>) i.getSerializableExtra("dict");
            dict.setHashMap(hashDict);
        }

        word = (TextView) findViewById(R.id.Text);
        textToSpeechButton = (ImageButton) findViewById(R.id.textToSpeech);
        clearButton = (Button) findViewById(R.id.clearButton);
        completeButton = (Button) findViewById(R.id.completeButton);
        addWord = (Button) findViewById(R.id.addWord);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewEntryActivity.class);
                i.putExtra("dict", dict.getHashMap());
                startActivity(i);
            }
        });

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
        patternView.addPatternLockListener(patternListener);
        patternView.setTactileFeedbackEnabled(false);

        Button messageTest = (Button) findViewById(R.id.SendMessage);

        messageTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pattern == null)
                    return;

                VibrationPattern vibrationPattern = VibrationPatternFactory.create(pattern);
                messageManager.sendMessage(MessageFactory.create("TactileBoard", vibrationPattern));
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.ERROR)
                    return;

                textToSpeech.setLanguage(Locale.US);
            }
        });

        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textToSpeech.isSpeaking())
                    textToSpeech.stop();

                textToSpeech.speak(writtenTranslation, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tuple<Boolean, String> returnValue = tryTranslatePattern(patternBuilder.toString());
                if(returnValue.first)
                {
                    //TODO: update views
                }
                //translateHaptogram(pattern);
            }
        });
    }

    private void translateHaptogram(List<PatternLockView.Dot> pattern) {

        boolean validHaptogram = checkHaptogram(pattern);

        if(validHaptogram) {
            this.pattern = pattern;
            word.setText(writtenTranslation);
            textToSpeechButton.setVisibility(View.VISIBLE);
            textToSpeech.speak(writtenTranslation, TextToSpeech.QUEUE_FLUSH, null);
            patternView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        } else {
            patternView.setViewMode(PatternLockView.PatternViewMode.WRONG);
            word.setText(R.string.not_known);
            textToSpeechButton.setVisibility(View.INVISIBLE);
        }
    }

    private Tuple<Boolean, String> tryTranslatePattern(String pattern){
        Tuple<Boolean, String> returnValues = new Tuple();
        if(dict.getHashMap() == null){
            returnValues.first = false;
        }
        else{
            Boolean hasKey = dict.containsKey(pattern);
            if(hasKey)
                returnValues.second = dict.getKey(pattern);
            returnValues.first = hasKey;
        }
        return returnValues;
    }

    private boolean checkHaptogram(List<PatternLockView.Dot> pattern) {

        if(dict.getHashMap() != null) {
            writtenTranslation = dict.getKey(MPatternLockUtils.patternToString(patternView, pattern));
        } else {
            writtenTranslation = "";
        }

        if(writtenTranslation != "") {
            return true;
        } else {
            return false;
        }
    }
    /*
    @Override
    protected void onPause() {
        super.onPause();

        editor = prefs.edit();
        dict.saveDict(editor);
    }*/

    @Override
    public void onBackPressed() {

        editor = getSharedPreferences("DICT", Context.MODE_PRIVATE).edit();
        dict.saveDict(editor);
    }

    @Override
    protected void onStop() {
        super.onStop();

        editor = getSharedPreferences("DICT", Context.MODE_PRIVATE).edit();
        dict.saveDict(editor);

        patternView.clearPattern();
        word.setText("Translation");
    }
}