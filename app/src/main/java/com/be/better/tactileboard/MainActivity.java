package com.be.better.tactileboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.be.better.tactileboard.databinding.ActivityMainBinding;
import com.be.better.tactileboard.viewmodels.MainActivityViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private ImageButton textToSpeechButton;
    private Button addWord;
    private PatternLockView patternView;
    private TextToSpeech textToSpeech;

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    public static Dict dict;

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
            viewModel.onCompleteStroke(pattern);
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

        // Setup view model
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(MainActivityViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        initPatternView();

        // Setup bindings that cannot be done in xml.
        viewModel.getIsPatternCorrect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isCorrect) {
                patternView.setViewMode(isCorrect? PatternLockView.PatternViewMode.CORRECT : PatternLockView.PatternViewMode.WRONG);
            }
        });

        viewModel.getWrittenTranslation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String writtenTranslation) {
                if(TextUtils.isEmpty(viewModel.getEncodedHaptogramString().getValue()))
                    return;

                speak();
            }
        });

        viewModel.getEncodedHaptogramString().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String encodedHaptogramString) {
                if(encodedHaptogramString == null || encodedHaptogramString.isEmpty())
                    patternView.clearPattern();
            }
        });

        if(dict == null) {
            SharedPreferences prefs = getSharedPreferences("DICT", Context.MODE_PRIVATE);
            dict = new Dict(prefs);
        } else {
            Intent i = getIntent();
            HashMap<String, String> hashDict = (HashMap<String, String>) i.getSerializableExtra("dict");
            dict.setHashMap(hashDict);
        }

        textToSpeechButton = (ImageButton) findViewById(R.id.textToSpeech);

        addWord = (Button) findViewById(R.id.addWord);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewEntryActivity.class);
                i.putExtra("dict", dict.getInstance());
                startActivity(i);
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
                speak();
            }
        });
    }

    private void speak() {
        if(textToSpeech.isSpeaking())
            textToSpeech.stop();

        textToSpeech.speak(viewModel.getWrittenTranslation().getValue(), TextToSpeech.QUEUE_FLUSH, null);
    }

    private void initPatternView(){
        patternView = (PatternLockView) findViewById(R.id.pattern_lock_view);

        patternView.setDotCount(viewModel.getRows().getValue());
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
    }

    private Tuple<Boolean, String> tryTranslatePattern(String pattern){
        Tuple<Boolean, String> returnValues = new Tuple();
        if(dict.getInstance() == null){
            returnValues.first = false;
        }
        else{
            returnValues.second = dict.getKey(pattern);
            returnValues.first = !returnValues.second.isEmpty();
        }
        return returnValues;
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
    }
}