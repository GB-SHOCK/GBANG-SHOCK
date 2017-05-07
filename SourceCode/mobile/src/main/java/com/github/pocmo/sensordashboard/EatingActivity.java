package com.github.pocmo.sensordashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.pocmo.sensordashboard.Server.ServerDownload;
import com.github.pocmo.sensordashboard.Server.ServerUpload;
import com.github.pocmo.sensordashboard.algorithm.EatingCount;
import com.github.pocmo.sensordashboard.ui.eating.EatingResultPopup;

public class EatingActivity extends Activity {

    private TextView stateView, countView, termView, timeView;
    private Button btn;

    private long startT = 0;
    private long stopT = 0;
    private long eating_time = 0;
    private String timeFormat = "";
    private String count, term, time;

    Intent intentPopup;
    private ServerUpload update;
    private SensorReceive sensorThread;
    private EatingCount eatingCount = new EatingCount();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eating);
        stateView = (TextView) findViewById(R.id.state);
        countView = (TextView) findViewById(R.id.count);
        termView = (TextView) findViewById(R.id.avgTerm);
        timeView = (TextView) findViewById(R.id.time);
        btn = (Button) findViewById(R.id.btn);


        btn.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       if (btn.getText().toString().equals("EATING  START")) {

                                           new SensorReceive(countView, termView).execute();
                                           btn.setText("STOP");
                                           stateView.setText("Spoon");
                                           if(countView.equals("1")) {
                                               startT = SystemClock.elapsedRealtime();
                                           }

                                       } else if (btn.getText().toString().equals("STOP")) {

                                           if(sensorThread!=null) {
                                               sensorThread.cancel(true);
                                           }

                                           //clear
                                           btn.setText("EATING  START");
                                           stopT = SystemClock.elapsedRealtime();
                                           eating_time = stopT - startT;
                                           timeFormat = String.format("%01dh:%02dmin:%02dsec", (eating_time / (1000 * 60 * 60)) % 24, (eating_time / (1000 * 60)) % 60, (eating_time / 1000) % 60);
                                           //timeView.setText(timeFormat);

                                           //get info and popup
                                           count = countView.getText().toString();
                                           term = termView.getText().toString();
                                           time = timeFormat;

                                           intentPopup = new Intent(EatingActivity.this, EatingResultPopup.class);
                                           intentPopup.putExtra("count", count);
                                           intentPopup.putExtra("term", term);
                                           intentPopup.putExtra("time", time);
                                           startActivity(intentPopup);

                                           //upload to server database
                                           update = new ServerUpload();
                                           update.insertEatingTable(count, eatingCount.getAverage_term()+"", eating_time+"");


                                       }
                                   }
                               }
        );

        // Intent intent = new Intent(this, SensorReceive.class);
        //   intent.putExtra("eatT",eating_time);
        //  startActivity(intent);
        //  finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        if (remoteSensorManager.getSensors().size() > 0 && btn.getText().toString().equals("STOP")) {
            BusProvider.getInstance().unregister(this);
            remoteSensorManager.stopMeasurement();
        }
        */
    }
}