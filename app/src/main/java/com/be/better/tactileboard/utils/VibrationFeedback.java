package com.be.better.tactileboard.utils;

import android.os.VibrationEffect;

public class VibrationFeedback {
    public static VibrationEffect Success(){
        int[] amplitudes = {128,0,128, 0};
        long[] timings = {50, 75, 100,125};
        return VibrationEffect.createWaveform(timings, amplitudes, -1);
    }

    public static VibrationEffect Error(){
        int[] amplitudes = {128};
        long[] timings = {500};
        return VibrationEffect.createWaveform(timings, amplitudes, -1);
    }

    public static VibrationEffect DotActivation(){
        int[] amplitudes = {128};
        long[] timings = {50};
        return VibrationEffect.createWaveform(timings, amplitudes, -1);
    }
}
