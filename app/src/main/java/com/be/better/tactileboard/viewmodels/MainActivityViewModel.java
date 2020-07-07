package com.be.better.tactileboard.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.andrognito.patternlockview.PatternLockView;
import com.be.better.tactileboard.MPatternLockUtils;
import com.be.better.tactileboard.MessageFactory;
import com.be.better.tactileboard.MessageManager;
import com.be.better.tactileboard.R;
import com.be.better.tactileboard.ServiceLocator;
import com.be.better.tactileboard.Tuple;
import com.be.better.tactileboard.VibrationPattern;
import com.be.better.tactileboard.VibrationPatternFactory;
import com.be.better.tactileboard.services.IWordRepository;

import java.util.List;


public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<Integer> rows = new MutableLiveData<>(4);        // Should be moved to config file.
    private MutableLiveData<Integer> columns = new MutableLiveData<>(4);

    private MutableLiveData<String> writtenTranslation = new MutableLiveData<>();
    private MutableLiveData<Boolean> showTextToSpeech = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPatternCorrect = new MutableLiveData<>();
    private MessageManager messageManager;
    private StringBuilder patternBuilder = new StringBuilder();
    private MutableLiveData<String> encodedHaptogramString = new MutableLiveData<>();
    private IWordRepository wordRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        writtenTranslation.setValue("Translation");
        showTextToSpeech.setValue(false);
        messageManager = new MessageManager();
        wordRepository = ServiceLocator.get(IWordRepository.class);
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
        Tuple<Boolean, String> returnValue = tryTranslatePattern(patternBuilder.toString());
        if(returnValue.first) {
            writtenTranslation.setValue(returnValue.second);
            showTextToSpeech.setValue(true);
            isPatternCorrect.setValue(true);
        }
        else {
            isPatternCorrect.setValue(false);
            showTextToSpeech.setValue(false);
            writtenTranslation.setValue(getApplication().getString(R.string.not_known));
        }

        patternBuilder.setLength(0);
        encodedHaptogramString.setValue(null);
    }

    public void onClearHaptogram(){
        patternBuilder.setLength(0);
        encodedHaptogramString.setValue(null);
        showTextToSpeech.setValue(false);
        writtenTranslation.setValue("Translation");
        encodedHaptogramString.setValue(null);
    }

    public void onSendMessage(){
        if(encodedHaptogramString.getValue() == null || encodedHaptogramString.getValue().isEmpty())
            return;

        VibrationPattern vibrationPattern = VibrationPatternFactory.create(MPatternLockUtils.stringToPattern(getRows().getValue(), getColumns().getValue(), encodedHaptogramString.getValue()));
        messageManager.sendMessage(MessageFactory.create("TactileBoard", vibrationPattern));
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