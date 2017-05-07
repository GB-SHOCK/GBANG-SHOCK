package com.github.pocmo.sensordashboard.data;

import java.util.ArrayList;

/**
 * Sensor Class represents data type of received sensor data from smart watch
 */
public class Sensor {
    private static final String TAG = "Sensor";
    private static final int MAX_DATA_POINTS = 30000;

    private long id;

    private ArrayList<SensorDataPoint> dataPoints = new ArrayList<SensorDataPoint>();

    public Sensor(int id) {
        this.id = id;
    }

    /**
     * Copy dataPoints ArrayList with different memory address
     * @return (ArrayList<SensorDataPoint>) clone of dataPoints
     */
    public synchronized ArrayList<SensorDataPoint> getDataPoints() {
        return (ArrayList<SensorDataPoint>) dataPoints.clone();
    }

    /**
     * Add SensorDataPoint to current ArrayList
     * @param dataPoint (SensorDataPoint) data value for attaching to ArrayList
     */
    public synchronized void addDataPoint(SensorDataPoint dataPoint) {
        dataPoints.add(dataPoint);

        if (dataPoints.size() > MAX_DATA_POINTS) {
            dataPoints.remove(0);
        }
    }

    public int getSize() {return dataPoints.size();}
    public long getId() {
        return id;
    }
}
