package com.be.better.tactileboard;

import java.util.LinkedList;

public class Frame {

    private double time;
    private LinkedList<ActuatorValue> actuators;

    Frame(double time) {
        this.time = time;
        actuators = new LinkedList<ActuatorValue>();
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public LinkedList<ActuatorValue> getActuators() {
        return actuators;
    }

    public void addActuators(LinkedList<ActuatorValue> actuators) {
        this.actuators.addAll(actuators);
    }

    public void addActuators(ActuatorValue actuators) {
        this.actuators.add(actuators);
    }
}
