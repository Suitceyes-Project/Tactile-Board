package com.be.better.tactileboard;

import android.content.Context;

import com.andrognito.patternlockview.PatternLockView;

import java.util.ArrayList;
import java.util.List;

public class MPatternLockUtils {

    public static String patternToString(PatternLockView patternLockView, List<PatternLockView.Dot> pattern) {

        if (pattern == null) {
            return "";
        }
        int patternSize = pattern.size();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < patternSize; i++) {
            PatternLockView.Dot dot = pattern.get(i);
            stringBuilder.append((dot.getRow() * patternLockView.getDotCount() + dot.getColumn() + ","));
        }
        return stringBuilder.toString();
    }

    public static List<PatternLockView.Dot> stringToPattern(PatternLockView patternLockView, String string) {

        String tmp = "";
        List<PatternLockView.Dot> result = new ArrayList<>();

        for (int i = 0; i < string.length(); i++) {

            if(string.charAt(i) != ',') {
                tmp += string.charAt(i);
            } else {
                int number = Integer.parseInt(tmp);
                result.add(PatternLockView.Dot.of(number / patternLockView.getDotCount(),
                        number % patternLockView.getDotCount()));
                tmp = "";
            }
        }
        return result;
    }
}
