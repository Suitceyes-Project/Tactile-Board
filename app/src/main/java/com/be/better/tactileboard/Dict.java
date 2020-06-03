package com.be.better.tactileboard;

import android.content.Context;
import android.content.SharedPreferences;

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

    protected String getKey(String value) {
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

    protected void saveDict(SharedPreferences.Editor editor) {
        String serializedDict = gson.toJson(this.dict);

        editor.putString(DICT, serializedDict);
        editor.apply();
    }

    protected void loadDict(HashMap<String,String> dict) {
        gson = new Gson();

        if (!this.prefs.getString(DICT, "").equals("null")) {
            String wrapperStr = this.prefs.getString(DICT, "");
            this.dict = gson.fromJson(wrapperStr, dict.getClass());
        }
    }

    protected void setHashMap(HashMap<String, String> hashDict) {
        this.dict = hashDict;
    }

    protected HashMap<String, String> getHashMap () {
        if(this.dict == null) {
            return new HashMap<String, String>();
        } else {
            return this.dict;
        }
    }

    protected SharedPreferences getPrefs() { return this.prefs; }
}
