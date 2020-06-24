package com.be.better.tactileboard;

import com.andrognito.patternlockview.PatternLockView;

import java.util.List;

public class VibrationPatternFactory {
    public static VibrationPattern create(List<PatternLockView.Dot> pattern)
    {
        VibrationPattern vibrationPattern = new VibrationPattern(false);
        double tmp = 0;
        for (PatternLockView.Dot dot : pattern)
        {
            ActuatorValue actuatorStart = new ActuatorValue(dot.getId(), 1);
            ActuatorValue actuatorEnd = new ActuatorValue(dot.getId(), 0);

            Frame frameOn = new Frame(-1);
            Frame frameOff = new Frame(-1);
            if(tmp == 0) {
                frameOn.setTime(tmp);
                frameOff.setTime(tmp += 0.5);
            } else {
                frameOn.setTime(tmp -= 0.05);
                frameOff.setTime(tmp += 0.3);
            }
            frameOn.addActuators(actuatorStart);
            frameOff.addActuators(actuatorEnd);
            vibrationPattern.addFrame(frameOn);
            vibrationPattern.addFrame(frameOff);
        }

        return vibrationPattern;
    }
}
