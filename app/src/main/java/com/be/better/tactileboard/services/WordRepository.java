package com.be.better.tactileboard.services;

import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class WordRepository implements IWordRepository, ServiceLocator.IService {
    private static final String Key = "DICT";
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private HashMap<String,String> dictionary;
    private Gson gson = new Gson();

    public WordRepository(@NonNull SharedPreferences prefs) {
        this.prefs = prefs;
        this.prefsEditor = prefs.edit();
        initDictionary();
    }

    private void initDictionary(){
        String value = prefs.getString(Key, null);
        if(TextUtils.isEmpty(value)) {
            dictionary = new HashMap<>();
            applyChanges();
        }
        else {
            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            dictionary = gson.fromJson(value, type);
        }
    }

    private void applyChanges(){
        String json = gson.toJson(dictionary);
        prefsEditor.putString(Key, json);
        prefsEditor.apply();
    }

    @Override
    public boolean addWord(@NonNull String word, @NonNull String pattern) {
        if(dictionary.containsKey(word))
            return false;

        dictionary.put(word, pattern);
        applyChanges();
        return true;
    }

    @Override
    public String getWord(String pattern) {

        Set<String> words = dictionary.keySet();

        for (String word : words) {
            if(dictionary.get(word).equals(pattern)) {
                return word;
            }
        }

        return null;
    }

    @Override
    public String getPattern(String word) {
        if(!dictionary.containsKey(word))
            return null;

        return dictionary.get(word);
    }

    @Override
    public boolean wordExists(String word) {
        return dictionary.containsKey(word);
    }

    @Override
    public boolean patternExists(String pattern) {
        return dictionary.containsValue(pattern);
    }

    @Override
    public Collection<String> getAllWords() {
        return dictionary.keySet();
    }

    @Override
    public void clear() {
        dictionary.clear();
        applyChanges();
    }
}
