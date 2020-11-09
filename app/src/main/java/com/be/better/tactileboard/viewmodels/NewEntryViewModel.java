package com.be.better.tactileboard.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.andrognito.patternlockview.PatternLockView;
import com.be.better.tactileboard.MPatternLockUtils;
import com.be.better.tactileboard.R;
import com.be.better.tactileboard.ServiceLocator;
import com.be.better.tactileboard.services.IWordRepository;
import java.util.List;

public class NewEntryViewModel extends AndroidViewModel {
    private MutableLiveData<Integer> rows = new MutableLiveData<>(4);
    private MutableLiveData<Integer> columns = new MutableLiveData<>(4);
    private MutableLiveData<String> newWord = new MutableLiveData<>();
    private MutableLiveData<Boolean> isValidHaptogram = new MutableLiveData<>();
    private MutableLiveData<String> userFeedbackMessage = new MutableLiveData<>();
    private MutableLiveData<String> encodedHaptogram = new MutableLiveData<>();
    private StringBuilder patternBuilder = new StringBuilder();
    private IWordRepository wordRepository;

    public NewEntryViewModel(@NonNull Application application) {
        super(application);
        wordRepository = ServiceLocator.get(IWordRepository.class);

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

    public MutableLiveData<String> getNewWord() {
        return newWord;
    }

    public LiveData<Boolean> getIsValidHaptogram() {
        return isValidHaptogram;
    }

    public LiveData<String> getUserFeedbackMessage() {
        return userFeedbackMessage;
    }

    public LiveData<String> getEncodedHaptogram() {
        return encodedHaptogram;
    }

    public void onStrokeCompleted(List<PatternLockView.Dot> pattern){
        String haptogram = MPatternLockUtils.patternToString(rows.getValue(),columns.getValue(), pattern);

        if(TextUtils.isEmpty(haptogram))
            return;

        if(patternBuilder.length() > 0)
            patternBuilder.append(',');

        patternBuilder.append(haptogram);
    }

    public void onWordAdded(){
        String haptogram = patternBuilder.toString();
        String entry = newWord.getValue();
        entry = entry.toLowerCase();

        // Check if word was entered for haptogram
        if(TextUtils.isEmpty(entry) || entry.equals(R.string.new_word)) {

            userFeedbackMessage.setValue(getApplication().getString(R.string.no_word));
            newWord.setValue(null);
            return;
        }

        // Check if haptogram itself was empty
        if(haptogram.isEmpty()) {
            userFeedbackMessage.setValue(getApplication().getString(R.string.no_pattern));
            return;
        }

        // Check if entry already exists in dict
        if (!isValidWord(entry)) {
            isValidHaptogram.setValue(false);
            userFeedbackMessage.setValue(getApplication().getString(R.string.word_exists));

            //TODO: show existing haptogram. Throw event.
            //String existingVal = dict.get(entry);
            //showHaptogram(existingVal);
            return;
        }

        // Check if the haptogram already exists.
        if(!isValidHaptogram(haptogram))
        {
            userFeedbackMessage.setValue(getApplication().getString(R.string.pattern_exists));
            newWord.setValue(null);
            isValidHaptogram.setValue(false);
            patternBuilder.setLength(0);

            // TODO: show word
            // showWord(haptogram);
            return;
        }

        // If we get here the haptogram and word is valid and we can add the entry.
        userFeedbackMessage.setValue(getApplication().getString(R.string.word_inserted));
        isValidHaptogram.setValue(true);
        wordRepository.addWord(entry, haptogram);
        patternBuilder.setLength(0);
        newWord.setValue(null);
        encodedHaptogram.setValue(null);

        // Go back to main activity
        // TODO: Use navigation components
        //Intent i = new Intent(getApplication(), MainActivity.class);
        //i.putExtra("dict", dict);
        //getApplication().startActivity(i);
    }

    public void onClearPattern(){
        patternBuilder.setLength(0);
        encodedHaptogram.setValue(null);
    }

    private Boolean isValidHaptogram(String haptogram) {
        if(haptogram.isEmpty())
            return false;

        return !wordRepository.patternExists(haptogram);
    }

    private boolean isValidWord(String entry) {
        if(entry.isEmpty())
            return false;

        return !wordRepository.wordExists(entry);
    }
}
