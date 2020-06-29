package com.be.better.tactileboard;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

public class VibrationPattern {

    private static double EPSILON = 0.001;

    private boolean isLooped;
    private double duration;
    private TreeSet<Frame> frames;

    VibrationPattern(boolean isLooped) {
        this.isLooped = isLooped;
        this.duration = 0.0;
        // frames must be sorted by time
        frames = new TreeSet<Frame>(new Comparator<Frame>() {
            @Override
            public int compare(Frame f1, Frame f2) {
                return Double.compare(f1.getTime(), f2.getTime());
            }

            private double compare(float a, float b) {
                return a - b;
            }
        });
    }

    public void addFrame(Frame frame) {

        if(frame.getTime() > duration)
            duration = frame.getTime();

        // adds actuators to existing frame, if they should be transmitted at the same time
        for(Frame f : frames) {
            double time = Math.abs((f.getTime() - frame.getTime()));

            if(time < EPSILON) {
                f.addActuators(frame.getActuators());
            }
        }
        frames.add(frame);
    }

    public String toJSON() {
        Gson gson = new Gson();

        String jsonString = gson.toJson(this);
        jsonString = "{\"FunctionName\":\"TactileBoard\",\"Payload\":" + jsonString + "}";

        return jsonString;
    }

}
