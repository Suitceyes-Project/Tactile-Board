package com.be.better.tactileboard;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.be.better.tactileboard.services.IWordRepository;
import com.be.better.tactileboard.services.WordRepository;

public class Main extends Application {
    private static final String TAG = "Main";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "On Create called!");
        ServiceLocator.init(this);
        ServiceLocator.bindInstance(IWordRepository.class, new WordRepository(getSharedPreferences("DICT", Context.MODE_PRIVATE)));
    }
}
