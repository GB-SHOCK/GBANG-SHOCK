package com.github.pocmo.sensordashboard.filter;

/**
 * Created by OneStar on 2016-09-09.
 * SVM Class combines 3-axis sensor data value using Math.sqrt and Math.pow function
 */
public class SVM {
    private float[] values;

    /**
     * Constructor which initializes float array contains 3-axis values
     * @param v (float) 3-axis sensor data values
     */
    public SVM(float[] v){
        values = v;
    }

    /**
     * Getter method
     * @return (float) a result of combined 3-axis value
     */
    public float getCombinedValue(){
        return (float)(Math.sqrt(Math.pow(values[0],2) + Math.pow(values[1],2) + Math.pow(values[2],2)));
    }
}
