package com.be.better.tactileboard.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.be.better.tactileboard.ServiceLocator;
import com.be.better.tactileboard.services.IWordRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DictionaryEntriesViewModel extends ViewModel {
    private MutableLiveData<List<String>> words = new MutableLiveData<>();
    private IWordRepository wordRepository;

    public DictionaryEntriesViewModel() {
        wordRepository = ServiceLocator.get(IWordRepository.class);
        initWordList();
    }

    public MutableLiveData<List<String>> getWords() {
        return words;
    }

    private void initWordList() {
        List<String> tmp = new ArrayList<>(wordRepository.getAllWords());
        Collections.sort(tmp);
        this.words.setValue(tmp);
    }

    public void onClear(){
        wordRepository.clear();
        words.getValue().clear();
    }
}
