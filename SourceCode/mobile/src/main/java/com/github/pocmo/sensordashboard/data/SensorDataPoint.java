package com.github.pocmo.sensordashboard.data;

import com.github.pocmo.sensordashboard.filter.SVM;

/**
 * SensorDataPoint represents information of each sensor data, pretend structure
 */
public class SensorDataPoint {
    private long timestamp;
    private float combinedValue;
    private float filteredValue;
    private SVM svm;

    /**
     * Constructor with parameters initializes timestamp value and implements SVM class to combine 3-axis sensor value
     * @param timestamp (long) contains time of received sensor data
     * @param values (float[]) contains 3-axis sensor data value
     */
    public SensorDataPoint(long timestamp, float[] values) {
        this.timestamp = timestamp;

        svm = new SVM(values);
        setCombinedValue(svm.getCombinedValue());
    }

    public float getCombinedValue() {
        return this.combinedValue;
    }

    public float getFilteredValue() {
        return this.filteredValue;
    }
    
    public void setCombinedValue(float value) {
        this.combinedValue = value;
    }

    public void setFilteredValue(float value) {
        this.filteredValue = value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
