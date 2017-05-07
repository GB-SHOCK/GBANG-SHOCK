package com.github.pocmo.sensordashboard;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;

import com.github.pocmo.sensordashboard.shared.DataMapKeys;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeviceClient {
    private static final String TAG = "DeviceClient";
    public static DeviceClient instance;
    public static DeviceClient getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceClient(context.getApplicationContext());
        }
        return instance;
    }

    private Context context;
    public GoogleApiClient googleApiClient;
    private ExecutorService executorService;
    PutDataRequest putDataRequest;
    public PutDataMapRequest dataMap;
    private DeviceClient(Context context) {
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        executorService = Executors.newCachedThreadPool();
    }

    public void sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, ": " + sensorType + ", ");
                sendSensorDataInBackground(sensorType, accuracy, timestamp, values);
            }
        });
    }

    private void sendSensorDataInBackground(int sensorType, int accuracy, long timestamp, float[] values) {

        dataMap = PutDataMapRequest.create("/sensors/" + sensorType);
        dataMap.getDataMap().putInt(DataMapKeys.ACCURACY, accuracy);
        dataMap.getDataMap().putLong(DataMapKeys.TIMESTAMP, timestamp);
        dataMap.getDataMap().putFloatArray(DataMapKeys.VALUES, values);
        putDataRequest = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);
    }
}
