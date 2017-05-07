package com.github.pocmo.sensordashboard;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.PutDataMapRequest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;

    SensorManager mSensorManager;

    private DeviceClient client;
    @Override
    public void onCreate() {
        super.onCreate();

        client = DeviceClient.getInstance(this);
        client.googleApiClient.connect();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);

        startForeground(1, builder.build());

        startMeasurement();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopMeasurement();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE);

        // Register the listener
        if (mSensorManager != null) {
            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor, 20000);
            }
            if (gyroscopeSensor != null) {
                mSensorManager.registerListener(this, gyroscopeSensor, 20000);
            }
        }
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
        Log.d(TAG, ": " + event.sensor.getType());
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
