package com.be.better.tactileboard;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class Dict extends HashMap implements Serializable {

    protected static HashMap<String,String> dict;

    private String DICT;
    private Gson gson;
    private SharedPreferences prefs;

    public Dict(SharedPreferences prefs) {
        this.prefs = prefs;
        dict = new HashMap<String,String>();
        loadDict(dict);
    }

    public String getKey(String value) {
        if(dict != null) {
            Set<String> keys = dict.keySet();

            for (String s : keys) {
                if(dict.get(s).equals(value)) {
                    return s;
                }
            }
        }

        return "";
    }

    public void saveDict(SharedPreferences.Editor editor) {
        String serializedDict = gson.toJson(this.dict);

        editor.putString(DICT, serializedDict);
        editor.apply();
    }

    public void loadDict(HashMap<String,String> dict) {
        gson = new Gson();

        if (!this.prefs.getString(DICT, "").equals("null")) {
            String wrapperStr = this.prefs.getString(DICT, "");
            this.dict = gson.fromJson(wrapperStr, dict.getClass());
        }
    }

    public void setHashMap(HashMap<String, String> hashDict) {
        this.dict = hashDict;
    }

    public HashMap<String, String> getInstance() {
        if(this.dict == null) {
            return new HashMap<String, String>();
        } else {
            return this.dict;
        }
    }

    public SharedPreferences getPrefs() { return this.prefs; }
}
