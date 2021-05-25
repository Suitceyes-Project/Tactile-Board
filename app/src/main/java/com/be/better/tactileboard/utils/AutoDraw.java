package com.be.better.tactileboard.utils;

import com.andrognito.patternlockview.PatternLockView;
import com.be.better.tactileboard.utils.MPatternLockUtils;

public class AutoDraw extends Thread {

    private PatternLockView patternView;
    private String haptogram;
    private long millis;

    public AutoDraw(PatternLockView patternView, String haptogram, long millis) {
        this.patternView = patternView;
        this.haptogram = haptogram;
        this.millis = millis;
    }

    @Override
    public void run() {

        patternView.setPattern(PatternLockView.PatternViewMode.AUTO_DRAW, MPatternLockUtils.stringToPattern(patternView, haptogram));

        long current = System.currentTimeMillis();
        millis += current;

        while(current <= millis) {
            current = System.currentTimeMillis();
        }

        patternView.clearPattern();
    }

}
