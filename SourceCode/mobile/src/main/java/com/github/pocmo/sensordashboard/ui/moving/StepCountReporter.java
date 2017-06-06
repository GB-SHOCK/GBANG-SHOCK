package com.github.pocmo.sensordashboard.ui.moving;

/**
 * Created by Bae Somi on 2016-09-29.
 */
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataObserver;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataResolver.Filter;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadResult;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StepCountReporter {
    private final HealthDataStore mStore;

    //step timestamp
    long endTime = 0;
    //stem hour
    int hour = 0;

    //step count sharedpreference
    SharedPreferences stepValue;
    SharedPreferences.Editor stepEditor;


    public StepCountReporter(Context c , HealthDataStore store) {
        mStore = store;
        stepValue = c.getSharedPreferences("step", Context.MODE_PRIVATE);
        stepEditor = stepValue.edit();
        stepEditor.putInt(0 + "", 0);
        stepEditor.putInt(1 + "", 10);
        stepEditor.commit();
        readTodayStepCount();



    }



    public void start() {
        // Register an observer to listen changes of step count and get today step count
        HealthDataObserver.addObserver(mStore, HealthConstants.StepCount.HEALTH_DATA_TYPE, mObserver);
        // create a reference & editor for the shared preferences object



    }

    // Read the today's step count on demand
    private void readTodayStepCount() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        // Set time range from start time of today to the current time
        long startTime = getStartTimeOfToday();
        endTime = System.currentTimeMillis();
        Filter filter = Filter.and(Filter.greaterThanEquals(HealthConstants.StepCount.START_TIME, startTime),
                Filter.lessThanEquals(HealthConstants.StepCount.START_TIME, endTime));


        String h;

        h  = ((endTime/(1000 * 60 * 60)) %24)+"";
        hour = Integer.parseInt(h);
       // System.out.println(hour);

        HealthDataResolver.ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .setProperties(new String[]{HealthConstants.StepCount.COUNT})
                .setFilter(filter)
                .build();

        try {
            //today step
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MovingDayFragment.APP_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(MovingDayFragment.APP_TAG, "Getting step count fails.");
        }
    }

    private long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    private final HealthResultHolder.ResultListener<ReadResult> mListener = new HealthResultHolder.ResultListener<ReadResult>() {
        @Override
        public void onResult(ReadResult result) {

            Cursor c = null;
            int count=0;


            try {
                c = result.getResultCursor();
                if (c != null) {
                    while (c.moveToNext()) {
                        count += c.getInt(c.getColumnIndex(HealthConstants.StepCount.COUNT));
                    }
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
                //   시간 당 스텝 수 저장
            //count  = 0 되면 sharedPreference 초기화
            if(count==0){
                //stepEditor.clear().commit();
              //  for(int i = 0 ;i <24 ; i++){

                    stepEditor.putInt(0 + "", 0);
                    stepEditor.putInt(1 + "", 10);
                    stepEditor.commit();
             //   }

            }
            //아니라면(Today) 저장
            else {
                //convert to korean time GMT+9:00
                int kHour = 0;
                kHour = hour + 9;

                //23보다 크면
                if(kHour>23){
                    kHour = kHour-24;
                }
                //
                stepEditor.putInt(kHour + "", count);
                System.out.println(kHour + "");
                System.out.println(kHour);
                stepEditor.commit();

            }
            MovingDayFragment.getInstance().drawStepCount(String.valueOf(count));




        }
    };




    private final HealthDataObserver mObserver = new HealthDataObserver(null) {

        // Update the step count when a change event is receivesd
        @Override
        public void onChange(String dataTypeName) {
            Log.d(MovingDayFragment.APP_TAG, "Observer receives a data changed event");
            readTodayStepCount();
        }
    };



}
