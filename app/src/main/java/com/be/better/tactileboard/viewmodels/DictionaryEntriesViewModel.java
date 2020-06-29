package com.be.better.tactileboard.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.be.better.tactileboard.ShowDict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DictionaryEntriesViewModel extends ViewModel {
    private MutableLiveData<List<String>> words = new MutableLiveData<>();

    public DictionaryEntriesViewModel() {
        initWordList();
    }

    public LiveData<List<String>> getWords() {
        return words;
    }

    private void initWordList() {
        Set<String> words = ShowDict.dict.keySet();
        List<String> tmp = new ArrayList<>(words);
        Collections.sort(tmp);
        this.words.setValue(tmp);
    }
}
