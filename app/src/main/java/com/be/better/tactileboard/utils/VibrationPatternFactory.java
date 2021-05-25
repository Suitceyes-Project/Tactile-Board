package com.be.better.tactileboard.utils;

import com.andrognito.patternlockview.PatternLockView;
import com.be.better.tactileboard.models.ActuatorValue;
import com.be.better.tactileboard.models.Frame;
import com.be.better.tactileboard.models.VibrationPattern;

import java.util.List;

public class VibrationPatternFactory {
    public static VibrationPattern create(List<PatternLockView.Dot> pattern)
    {
        return create(pattern, false, 0.3f, 0.05f);
    }

    public static VibrationPattern create(List<PatternLockView.Dot> pattern, boolean useReferenceFrame, float frameDuration, float frameOverlap)
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
                if(useReferenceFrame)
                    frameOff.setTime(tmp += 0.5);
                else
                    frameOff.setTime(tmp += frameDuration);
            } else {
                frameOn.setTime(tmp -= frameOverlap);
                frameOff.setTime(tmp += frameDuration);
            }
            frameOn.addActuators(actuatorStart);
            frameOff.addActuators(actuatorEnd);
            vibrationPattern.addFrame(frameOn);
            vibrationPattern.addFrame(frameOff);
        }

        return vibrationPattern;
    }
}
