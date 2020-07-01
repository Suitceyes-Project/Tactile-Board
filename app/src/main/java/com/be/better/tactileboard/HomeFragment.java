package com.be.better.tactileboard;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.be.better.tactileboard.databinding.FragmentHomeBinding;
import com.be.better.tactileboard.viewmodels.MainActivityViewModel;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ImageButton textToSpeechButton;
    private Button addWord;
    private PatternLockView patternView;
    private TextToSpeech textToSpeech;

    private FragmentHomeBinding binding;
    private MainActivityViewModel viewModel;

    public HomeFragment() {
    }

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup view model
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(MainActivityViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        initPatternView();

        // Setup bindings that cannot be done in xml.
        viewModel.getIsPatternCorrect().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isCorrect) {
                patternView.setViewMode(isCorrect? PatternLockView.PatternViewMode.CORRECT : PatternLockView.PatternViewMode.WRONG);
            }
        });

        viewModel.getWrittenTranslation().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String writtenTranslation) {
                if(TextUtils.isEmpty(viewModel.getEncodedHaptogramString().getValue()))
                    return;

                speak();
            }
        });

        viewModel.getEncodedHaptogramString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String encodedHaptogramString) {
                if(encodedHaptogramString == null || encodedHaptogramString.isEmpty())
                    patternView.clearPattern();
            }
        });

        textToSpeechButton = (ImageButton) getView().findViewById(R.id.textToSpeech);

        addWord = (Button) getView().findViewById(R.id.addWord);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(HomeFragmentDirections.actionHomeFragmentToAddWordFragment());
            }
        });

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
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
        patternView = (PatternLockView) getView().findViewById(R.id.pattern_lock_view);

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}