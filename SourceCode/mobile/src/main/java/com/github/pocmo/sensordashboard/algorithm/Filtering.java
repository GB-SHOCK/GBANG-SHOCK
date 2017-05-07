package com.github.pocmo.sensordashboard.algorithm;

import com.github.pocmo.sensordashboard.data.Sensor;
import com.github.pocmo.sensordashboard.data.SensorDataPoint;
import com.github.pocmo.sensordashboard.filter.LowPassFilter;
import com.github.pocmo.sensordashboard.filter.MovingAverageFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OneStar on 2016-09-09.
 * Filtering Class calls proper filtering Class depends on current sensor type
 */
public class Filtering {
    private List<Sensor> sensor;
    ArrayList<SensorDataPoint> accValue;
    ArrayList<SensorDataPoint>  gyroValue;
    private LowPassFilter lpf;
    private MovingAverageFilter maf;

    private final static int SENS_ACCELEROMETER = android.hardware.Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_GYROSCOPE = android.hardware.Sensor.TYPE_GYROSCOPE;
    private static final String TAG = "Filtering";

    /**
     * Constructor with parameters implements proper filtering Class
     * @param a (ArrayList<SensorDataPoint>) combined accelerometer data set
     * @param g (ArrayList<SensorDataPoint>) combined gyroscope data set
     */
    public Filtering(ArrayList<SensorDataPoint> a, ArrayList<SensorDataPoint> g) {
        //filtering
        accValue = a;
        gyroValue = g;

        if(a.size()!=0) {
            lpf = new LowPassFilter(a);
            accValue = lpf.filtering();
        }

        if(g.size()!=0) {
            maf = new MovingAverageFilter(g);
            gyroValue = maf.filtering();
        }
    }

    /**
     * Getter method
     * @param id (int) represents current sensor type
     * @return (ArrayList<SensorDataPoint>) filtered ArrayList<SensorDataPoint> depends on sensor type
     */
    public ArrayList<SensorDataPoint> getValue(int id){
        for(int i = 0; i < sensor.size(); i++){
            if(id == SENS_ACCELEROMETER) {
                return accValue;
            }
            else if(id == SENS_GYROSCOPE){
                return gyroValue;
            }
        }
        return null;
    }
}
