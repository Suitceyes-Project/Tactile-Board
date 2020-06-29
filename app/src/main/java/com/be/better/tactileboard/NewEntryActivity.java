package com.be.better.tactileboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.be.better.tactileboard.databinding.AddWordBinding;
import com.be.better.tactileboard.viewmodels.NewEntryViewModel;

import java.util.HashMap;
import java.util.List;


public class NewEntryActivity extends AppCompatActivity {

    protected static EditText newWord;
    private Button showDict;
    private PatternLockView patternView;
    private Button clearButton;
    public static HashMap<String, String> dict;
    private StringBuilder patternBuilder = new StringBuilder();
    private AddWordBinding binding;
    private NewEntryViewModel viewModel;

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
            viewModel.onStrokeCompleted(pattern);
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

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(NewEntryViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.add_word);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        //setContentView(R.layout.add_word);

        Intent i = getIntent();
        HashMap<String, String> map = new HashMap<String, String>((HashMap<String, String>) i.getSerializableExtra("dict"));
        if(map == null) {
            dict = new HashMap<String, String >();
        } else {
            dict = map;
        }

        // Setup bindings
        viewModel.getIsValidHaptogram().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isValidHaptogram) {
                patternView.setViewMode(isValidHaptogram? PatternLockView.PatternViewMode.CORRECT : PatternLockView.PatternViewMode.WRONG);
            }
        });

        viewModel.getUserFeedbackMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(TextUtils.isEmpty(s))
                    return;

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getEncodedHaptogram().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                patternView.clearPattern();
            }
        });

        newWord = (EditText) findViewById(R.id.newWord);
        clearButton = (Button) findViewById(R.id.clearButton);
        initPatternView();

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

    private void initPatternView() {
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
