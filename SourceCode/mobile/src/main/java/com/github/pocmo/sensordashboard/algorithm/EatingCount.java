package com.github.pocmo.sensordashboard.algorithm;

import android.util.Log;

import com.github.pocmo.sensordashboard.data.Sensor;
import com.github.pocmo.sensordashboard.data.SensorDataPoint;

import java.util.ArrayList;

/**
 * Created by OneStar on 2016-09-08.
 * This is an activity for eating count algorithm
 * @param: accValue (ArrayList<SensorDataPoint>) ArrayList which contains accelerometer data value
 * @param: gyroValue (ArrayList<SensorDataPoint>) ArrayList which contains gyroscope data value
 * @param: thresholdA (float) threshold value for filtered accelerometer data
 * @param: thresholdG (float) threshold value for filtered gyroscope data
 * @param: alpha (float) correlation for error range
 * @param: countE (int) number of time for eating count
 * @param: standardA (float) 9.9 for error range from 9.8 of gravity
 * @param: firstRun (int) check if it is first time for implement
 *
 * @param: average_term ()
 *
 * @param: length (int) size of gyroscope ArrayList
 * @param: flag (int) notify whether conditions are required
 * @param: index (int)
 * @param: handUp (int) notify whether hand is up
 * @param: handDown (int) notify whether hand is down
 * @param: maxPG (int) variable for gyroscope peak value
 * @param: maxPA (int) variable for accelerometer peak value
 * @param: maxNA (int) variable for accelerometer minimum value
 */
public class EatingCount {
    private ArrayList<SensorDataPoint> accValue;
    private ArrayList<SensorDataPoint> gyroValue;

    private float thresholdA = (float) 0;
    private float thresholdG = (float) 1.5;
    private float alpha = (float) 0.6;
    private int countE = 67;
    private float standardA = (float) 9.9;
    private int firstRun = 0;

    private String TAG = "EatingCount";

    private long start = 0;
    private long end = 0;
    private long temp = 0;
    private ArrayList<Long> mIntervalList = new ArrayList<Long>();
    private int interval_idx = 0;
    private long average_term = 0;


    int window = 65;
    int length;
    int index;
    int flag;
    int handUp;
    int handDown;

    int maxPG = 0;
    int maxPA = 0;
    int maxNA = 0;

    public EatingCount() {
        this.index = 0;
        this.flag = 1;
        this.handUp = 0;
        this.handDown = 0;
    }

    /**
     * Initializes variables and implements initialThreshold method
     * @param: sensor1 Sensor class (usually accelerometer)
     * @param: sensor2 Sensor class (usually gyroscope)
     * @param: count integer about eating count
     */
    public void Count(Sensor a, Sensor g, int count) {
        accValue = a.getDataPoints();
        gyroValue = g.getDataPoints();
        this.length = gyroValue.size();
        this.countE = count;

        if (firstRun == 0 && length > window) {
            firstRun = 1;
        }
        initialThreshold();
    }

    /**
     * Initializes threshold measurement before main algorithm implementation
     */
    public void initialThreshold() {
        maxPG = length - window;
        maxPA = length - window;
        maxNA = length - window;

        if (flag == 1 && length > window) {
            for (int k = length - window + 1; k < length - 1; k++) {
                if (handUp == 0 && gyroValue.get(maxPG).getFilteredValue() < gyroValue.get(k).getFilteredValue())
                    this.maxPG = k;
                else if (handUp == 1 && gyroValue.get(maxPG).getFilteredValue() > gyroValue.get(k).getFilteredValue())
                    this.maxPG = k;

                if (handUp == 0 && accValue.get(k).getFilteredValue() >= standardA && accValue.get(maxPA).getFilteredValue() < accValue.get(k).getFilteredValue())
                    this.maxPA = k;
                else if (handUp == 1 && accValue.get(k).getFilteredValue() < standardA && accValue.get(maxPA).getFilteredValue() > accValue.get(k).getFilteredValue())
                    this.maxNA = k;
            }
        }
    }

    /**
     * Main algorithm for counting eating time
     * It checks set of sensor data values in the window size whether they represent hand is up or down in real-time
     * If data values satisfy the conditions, then the method updates eating count value
     */
    public void analyzeEatingPoint() {
        if (flag == 0 && length == index)
            flag = 1;

        else if (flag == 1 && length > window) {
            for (int i = length - window; i < length - 1; i++) {
                if (handUp == 0 && thresholdG <= gyroValue.get(maxPG).getFilteredValue() - gyroValue.get(i).getFilteredValue() && gyroValue.get(maxPG).getFilteredValue() - gyroValue.get(i).getFilteredValue() < 4) {

                    if (thresholdA == 0 && (accValue.get(maxPA).getFilteredValue() - standardA > 0.095 && accValue.get(maxPA).getFilteredValue() - standardA < 2.0)) {
                        thresholdA = accValue.get(maxPA).getFilteredValue() - standardA;
                        handUp = 1;
                    } else if (thresholdA != 0 && thresholdA <= accValue.get(maxPA).getFilteredValue() - standardA && accValue.get(maxPA).getFilteredValue() - standardA < 2.5) {
                        handUp = 1;
                        if (start <= accValue.get(maxPA).getTimestamp() && 0 < start) {
                            end = accValue.get(maxPA).getTimestamp();
                            if (end != start) {
                                temp = end - start;
                                mIntervalList.add(temp);
                                interval_idx++;
                            }
                        }
                        flag = 0;
                        index = maxPG + window;
                    }
                } else if (handUp == 1 && thresholdG <= gyroValue.get(i).getFilteredValue() - gyroValue.get(maxPG).getFilteredValue() && gyroValue.get(i).getFilteredValue() - gyroValue.get(maxPG).getFilteredValue() < 4) {
                    if (thresholdA <= standardA - accValue.get(maxNA).getFilteredValue() && standardA - accValue.get(maxNA).getFilteredValue() < 2.5) {
                        handDown = 1;
                        if (start < accValue.get(maxNA).getTimestamp()) {
                            start = accValue.get(maxNA).getTimestamp();
                        }

                        thresholdA = (float) (((1 - alpha) * (standardA - accValue.get(maxNA).getFilteredValue())) + (alpha * thresholdA) - 0.05);
                        thresholdG = (float) (((1 - alpha) * (gyroValue.get(i).getFilteredValue() - gyroValue.get(maxPG).getFilteredValue())) + (alpha * thresholdG) - 0.05);
                    }
                }

                if (handDown == 1 && handUp == 1) {
                    countE = countE + 1;
                    handDown = 0;
                    handUp = 0;
                    flag = 0;
                    index = maxPG + window;

                    Log.d("Count", ": " + countE + "(" + thresholdA + ", " + thresholdG + ")");
                    break;
                }
            }
        }
    }

    /**
     * Return analyzed eating counts
     * @return: (int) eating counts
     */
    public int getEatingCount() {
        analyzeEatingPoint();
        return this.countE;
    }

    /**
     * This Function returns average term of the time between spoon down and spoon up.
     * @return: (long)average term of each eating point
     */
    public long getAverage_term() {
        //analyzeEatingPoint();
        long sumInterval = 0;
        for (int i = 0; i < mIntervalList.size(); i++) {
            sumInterval += mIntervalList.get(i);
        }
        if (mIntervalList.size() != 0) {
            average_term = sumInterval / mIntervalList.size();
            return average_term;
        }
        return 0;
    }
}