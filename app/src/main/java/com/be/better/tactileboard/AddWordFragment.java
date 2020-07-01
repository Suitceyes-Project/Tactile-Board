package com.be.better.tactileboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.be.better.tactileboard.databinding.FragmentAddWordBinding;
import com.be.better.tactileboard.viewmodels.NewEntryViewModel;

import java.util.List;

public class AddWordFragment extends Fragment {
    protected EditText newWord;
    private Button showDict;
    private PatternLockView patternView;
    private FragmentAddWordBinding binding;
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

    public AddWordFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(NewEntryViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Setup bindings
        viewModel.getIsValidHaptogram().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isValidHaptogram) {
                patternView.setViewMode(isValidHaptogram? PatternLockView.PatternViewMode.CORRECT : PatternLockView.PatternViewMode.WRONG);
            }
        });

        viewModel.getUserFeedbackMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(TextUtils.isEmpty(s))
                    return;

                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getEncodedHaptogram().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                patternView.clearPattern();
            }
        });

        newWord = (EditText) getView().findViewById(R.id.newWord);
        initPatternView();

        showDict = (Button) getView().findViewById(R.id.showDict);
        showDict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(NewEntryActivity.this, ShowDict.class);
                startActivity(i);*/
            }
        });
    }

    private void initPatternView() {
        patternView = (PatternLockView) getView().findViewById(R.id.pattern_lock_view);

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddWordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}