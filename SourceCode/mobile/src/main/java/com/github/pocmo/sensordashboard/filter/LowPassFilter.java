package com.github.pocmo.sensordashboard.filter;

import com.github.pocmo.sensordashboard.data.SensorDataPoint;

import java.util.ArrayList;

/**
 * Created by OneStar on 2016-09-09.
 * LowPassFilter is Filtering Class for received accelerometer raw sensor data
 */
public class LowPassFilter {
    private ArrayList<SensorDataPoint> acc;
    private int firstRun = 0;
    private float prevX = 0;
    float alpha = (float) 0.99;

    /**
     *Constructor which initializes ArrayList<SensorDataPoint>
     * @param accValue (ArrayList<SensorDataPoint>) set of received accelerometer data
     */
    public LowPassFilter(ArrayList<SensorDataPoint> accValue) {
        this.acc = accValue;
    }

    /**
     * Applies Low Pass Filtering
     * It uses previous combined accelerometer data value with alpha ratio and current value with 1-alpha ratio
     * @return (ArrayList<SensorDataPoint>) a set of filtered accelerometer data
     */
    public ArrayList<SensorDataPoint> filtering() {
        float temp = 0;
        float x = acc.get(acc.size() - 1).getCombinedValue();
        if (firstRun == 0) {
            prevX = x;
            firstRun = 1;
        }
        temp = alpha * prevX + (1 - alpha) * x;
        prevX = temp;
        acc.get(acc.size() - 1).setFilteredValue(temp);
        return acc;
    }
}
