package com.github.pocmo.sensordashboard.filter;

import com.github.pocmo.sensordashboard.data.SensorDataPoint;

import java.util.ArrayList;

/**
 * Created by OneStar on 2016-09-09.
 * MovingAverageFilter is Filtering Class for received gyroscope raw sensor data
 */

public class MovingAverageFilter {
    private ArrayList<SensorDataPoint> gyro;
    private int n = 65;

    /**
     *Constructor which initializes ArrayList<SensorDataPoint>
     * @param gyroValue (ArrayList<SensorDataPoint>) set of received gyroscope data
     */
    public MovingAverageFilter(ArrayList<SensorDataPoint> gyroValue) {
        //Resampling gyroscope sensor value if it is needed
        gyro = gyroValue;
    }

    /**
     * Applies Moving Averaging Filtering
     * It uses combined gyroscope data value of size of window
     * @return (ArrayList<SensorDataPoint>) a set of filtered gyroscope data
     */
    public ArrayList<SensorDataPoint> filtering() {
        float sum = 0;

        if (gyro.size() -1 < n) {
            for (int k = 0; k < gyro.size(); k++)
                sum += gyro.get(k).getCombinedValue();
            gyro.get(gyro.size()-1).setFilteredValue(sum / (gyro.size()-1));
        } else {
            for (int k = gyro.size() - n; k < gyro.size(); k++)
                sum += gyro.get(k).getCombinedValue();
            gyro.get(gyro.size()-1).setFilteredValue(sum / n);
        }

        return gyro;
    }
}
