package com.be.better.tactileboard.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.andrognito.patternlockview.PatternLockView;
import com.be.better.tactileboard.MPatternLockUtils;
import com.be.better.tactileboard.MessageFactory;
import com.be.better.tactileboard.VibrationFeedback;
import com.be.better.tactileboard.services.IMessenger;
import com.be.better.tactileboard.R;
import com.be.better.tactileboard.ServiceLocator;
import com.be.better.tactileboard.Tuple;
import com.be.better.tactileboard.VibrationPattern;
import com.be.better.tactileboard.VibrationPatternFactory;
import com.be.better.tactileboard.services.IWordRepository;

import java.util.ArrayList;
import java.util.List;


public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = "MainActivityViewModel";

    private MutableLiveData<Integer> rows = new MutableLiveData<>(4);
    private MutableLiveData<Integer> columns = new MutableLiveData<>(4);

    private MutableLiveData<String> completeButtonText = new MutableLiveData<>("Complete");
    private MutableLiveData<String> writtenTranslation = new MutableLiveData<>();
    private MutableLiveData<Boolean> showTextToSpeech = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPatternCorrect = new MutableLiveData<>();
    private MutableLiveData<String> enteredText = new MutableLiveData<>();
    private MutableLiveData<String> feedbackText = new MutableLiveData<>();
    private MutableLiveData<List<PatternLockView.Dot>> dots = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSpeechAvailable = new MutableLiveData<>();
    private IMessenger messenger;
    private StringBuilder patternBuilder = new StringBuilder();
    private MutableLiveData<String> encodedHaptogramString = new MutableLiveData<>();
    private IWordRepository wordRepository;
    private SpeechRecognizer speechRecognizer;
    private boolean canSendMessage = false;

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            feedbackText.setValue("Speech recoginition stopped");
            isSpeechAvailable.setValue(true);
        }

        @Override
        public void onError(int i) {

        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] confidence = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

            if(results.isEmpty())
                return;

            // Find result with highest confidence
            int index = 0;
            float max = confidence[0];
            for (int i = 1; i < confidence.length; i++){
                float value = confidence[i];
                if(value > max){
                    max = value;
                    index = i;
                }
            }

            String word = results.get(index);
            enteredText.setValue(word);
            onEnterTextComplete();
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        writtenTranslation.setValue("Draw Haptogram or enter text");
        showTextToSpeech.setValue(false);
        messenger = ServiceLocator.get(IMessenger.class);
        wordRepository = ServiceLocator.get(IWordRepository.class);
        completeButtonText.setValue("Complete");
        isSpeechAvailable.setValue(SpeechRecognizer.isRecognitionAvailable(getApplication().getApplicationContext()));

        if(isSpeechAvailable.getValue()) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication().getApplicationContext());
            speechRecognizer.setRecognitionListener(recognitionListener);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String gridKey = getApplication().getResources().getString(R.string.pref_grid_size_key);
        int size = prefs.getInt(gridKey, 4);
        rows.setValue(size);
        columns.setValue(size);
    }

    /**
     * Gets the number of rows of the haptogram grid.
     * @return
     */
    public LiveData<Integer> getRows() {
        return rows;
    }

    /**
     * Gets the number of columns of the haptogram grid.
     * @return
     */
    public LiveData<Integer> getColumns() {
        return columns;
    }

    /**
     * Gets the translation string for the drawn haptogram.
     * @return
     */
    public LiveData<String> getWrittenTranslation() {
        return writtenTranslation;
    }

    /**
     * Gets a value indicating whether the text-to-speech button should be displayed.
     * @return
     */
    public LiveData<Boolean> getShowTextToSpeech() {
        return showTextToSpeech;
    }

    /**
     * Gets a value indicating whether the haptogram pattern is correct.
     * @return
     */
    public LiveData<Boolean> getIsPatternCorrect() {
        return isPatternCorrect;
    }

    /**
     * Gets a value holding a comma separated encoding of the haptogram.
     * E.g. 0,1,2,3
     * @return
     */
    public LiveData<String> getEncodedHaptogramString() {
        return encodedHaptogramString;
    }

    /**
     * Gets the entered text.
     * @return
     */
    public MutableLiveData<String> getEnteredText() {
        return enteredText;
    }

    public MutableLiveData<String> getFeedbackText() {
        return feedbackText;
    }

    public MutableLiveData<List<PatternLockView.Dot>> getDots() {
        return dots;
    }

    public MutableLiveData<Boolean> getIsSpeechAvailable() {
        return isSpeechAvailable;
    }

    public MutableLiveData<String> getCompleteButtonText() { return completeButtonText; }

    public void onCompleteStroke(List<PatternLockView.Dot> pattern){
        String haptogram = MPatternLockUtils.patternToString(rows.getValue(), columns.getValue(), pattern);

        if(haptogram.isEmpty())
            return;

        if(patternBuilder.length() > 0)
            patternBuilder.append(','); // Maybe change to ':' to distinguish strokes.

        patternBuilder.append(haptogram);
        encodedHaptogramString.setValue(patternBuilder.toString());
    }

    public void onFinalizeHaptogram(){
        if(!canSendMessage)
            ValidatePattern();
        else
            onSendMessage();
    }

    private void ValidatePattern(){
        Tuple<Boolean, String> returnValue = tryTranslatePattern(patternBuilder.toString());
        if(returnValue.first) {
            writtenTranslation.setValue(returnValue.second);
            showTextToSpeech.setValue(true);
            isPatternCorrect.setValue(true);
            completeButtonText.setValue("Send");
            canSendMessage = true;
        }
        else {
            isPatternCorrect.setValue(false);
            showTextToSpeech.setValue(false);
            writtenTranslation.setValue(getApplication().getString(R.string.not_known));
            completeButtonText.setValue("Complete");
            canSendMessage = false;
            provideHapticFeedback(VibrationFeedback.Error());
        }

        patternBuilder.setLength(0);
    }

    public void provideHapticFeedback(VibrationEffect effect){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return;

        Vibrator vibrator = (Vibrator)getApplication().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(effect);
    }

    public void onClearHaptogram(){
        patternBuilder.setLength(0);
        encodedHaptogramString.setValue(null);
        showTextToSpeech.setValue(false);
        writtenTranslation.setValue("Draw Haptogram or enter text");
        encodedHaptogramString.setValue(null);
        completeButtonText.setValue("Complete");
        canSendMessage = false;
    }

    public void onSendMessage(){
        if(encodedHaptogramString.getValue() == null || encodedHaptogramString.getValue().isEmpty())
            return;


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        float frameDuration = prefs.getInt(getApplication().getResources().getString(R.string.pref_actuator_frame_time_key), 300) / 1000f;
        float frameOverlap = prefs.getInt(getApplication().getResources().getString(R.string.pref_actuator_frame_overlap_key), 100) / 1000f;
        boolean useReferenceFrame = prefs.getBoolean(getApplication().getResources().getString(R.string.pref_actuator_first_frame_long_key), false);

        VibrationPattern vibrationPattern = VibrationPatternFactory.create(MPatternLockUtils.stringToPattern(getRows().getValue(), getColumns().getValue(), encodedHaptogramString.getValue()),
                useReferenceFrame, frameDuration, frameOverlap);

        if(!prefs.getBoolean(getApplication().getResources().getString(R.string.pref_send_ontology_key), false))
            messenger.send("suitceyes/tactile-board/play", MessageFactory.create(vibrationPattern));
        else {
            Log.d(TAG, "onSendMessage: message: " + writtenTranslation.getValue());
            messenger.send("suitceyes/ontology/query", MessageFactory.createOntologyMessage(writtenTranslation.getValue()));
        }
        feedbackText.setValue("Sending...");
        provideHapticFeedback(VibrationFeedback.Success());
        onClearHaptogram();
    }



    public void onEnterTextComplete(){
        Log.d(TAG, "On Enter Text");
        String enteredText = getEnteredText().getValue();
        getEnteredText().setValue(null);
        if(TextUtils.isEmpty(enteredText))
            return;

        enteredText = enteredText.toLowerCase();
        if(!wordRepository.wordExists(enteredText)){
            feedbackText.setValue("Word does not exist.");
            return;
        }

        encodedHaptogramString.setValue(wordRepository.getPattern(enteredText));
        writtenTranslation.setValue(enteredText);
        showTextToSpeech.setValue(true);
        dots.setValue(MPatternLockUtils.stringToPattern(rows.getValue(), columns.getValue(), wordRepository.getPattern(enteredText)));
        completeButtonText.setValue("Send");
        canSendMessage = true;
    }

    public void onSpeechToTextClicked(){
        Log.d(TAG, "On Speech to Text clicked");
        // Check for permission
        isSpeechAvailable.setValue(false);
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizer.startListening(recognizerIntent);
        feedbackText.setValue("Speech recoginition activated");
    }

    public void cleanUp(){
        if(speechRecognizer != null){
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
        }
    }



    private Tuple<Boolean, String> tryTranslatePattern(String pattern){ // TODO: move to service
        Tuple<Boolean, String> returnValues = new Tuple();
        String word = wordRepository.getWord(pattern);
        if(word == null) {
            returnValues.first = false;
        }
        else{
            returnValues.first = true;
            returnValues.second = word;
        }

        return returnValues;
    }
}