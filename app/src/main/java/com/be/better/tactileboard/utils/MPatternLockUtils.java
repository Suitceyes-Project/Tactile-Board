package com.be.better.tactileboard.utils;

import android.content.Context;
import android.text.TextUtils;

import com.andrognito.patternlockview.PatternLockView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MPatternLockUtils {

    public static String patternToString(int rows, int columns, List<PatternLockView.Dot> pattern) {

        if (pattern == null)
            return "";

        int patternSize = pattern.size();
        List<String> indices = new LinkedList<>();
        for (int i = 0; i < patternSize; i++) {
            PatternLockView.Dot dot = pattern.get(i);
            int index = dot.getRow() * rows + dot.getColumn();
            indices.add(String.valueOf(index));
        }
        return TextUtils.join(",", indices);
    }

    public static String patternToString(PatternLockView patternLockView, List<PatternLockView.Dot> pattern) {

        if (pattern == null)
            return "";

        int patternSize = pattern.size();
        List<String> indices = new LinkedList<>();
        for (int i = 0; i < patternSize; i++) {
            PatternLockView.Dot dot = pattern.get(i);
            int index = dot.getRow() * patternLockView.getDotCount() + dot.getColumn();
            indices.add(String.valueOf(index));
        }
        return TextUtils.join(",", indices);
    }

    public static List<PatternLockView.Dot> stringToPattern(int rows, int columns, String string) {

        List<PatternLockView.Dot> result = new ArrayList<>();
        String[] items = string.split(",");

        for (int i = 0; i < items.length; i++) {
            int index = Integer.parseInt(items[i]);
            result.add(PatternLockView.Dot.of(index / rows,
                    index % columns));
        }

        return result;
    }

    public static List<PatternLockView.Dot> stringToPattern(PatternLockView patternLockView, String string) {

        List<PatternLockView.Dot> result = new ArrayList<>();
        String[] items = string.split(",");

        for (int i = 0; i < items.length; i++) {
            int index = Integer.parseInt(items[i]);
            result.add(PatternLockView.Dot.of(index / patternLockView.getDotCount(),
                    index % patternLockView.getDotCount()));
        }

        return result;
    }
}
