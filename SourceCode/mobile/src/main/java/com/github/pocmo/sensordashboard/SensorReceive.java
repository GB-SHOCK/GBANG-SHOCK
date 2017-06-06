package com.github.pocmo.sensordashboard;

/**
 * Created by OneStar on 2016-09-12.
 * SensorReceive Class handles received sensor data and also implements EatingCount Class
 */

import android.app.Activity;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import com.github.pocmo.sensordashboard.algorithm.EatingCount;
import com.github.pocmo.sensordashboard.algorithm.Filtering;
import com.github.pocmo.sensordashboard.data.Sensor;
import com.github.pocmo.sensordashboard.data.SensorDataPoint;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.WIFI_SERVICE;

public class SensorReceive extends AsyncTask<Void, Integer, Long> {

    /*WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    DhcpInfo dhcpInfo = wm.getDhcpInfo() ;
    int serverIp = dhcpInfo.gateway;


    String ipAddress = String.format(
            "%d.%d.%d.%d",
            (serverIP & 0xff),
            (serverIP >> 8 & 0xff),
            (serverIP >> 16 & 0xff),
            (serverIP >> 24 & 0xff));*/

    private String TAG = "SensorReceive";
    public String SERVERIP = "172.20.10.7"; // 'Within' the emulator!
    public static final int SERVERPORT = 12345;

    public int countE = 0;
    private long term = 0;
    private long termM = 0;
    private long termS = 0;

    private final static int SENS_ACCELEROMETER = android.hardware.Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_GYROSCOPE = android.hardware.Sensor.TYPE_GYROSCOPE;

    private Sensor sensorA = new Sensor(SENS_ACCELEROMETER);
    private Sensor sensorG = new Sensor(SENS_GYROSCOPE);
    private DatagramSocket socket;

    private Filtering filtering;
    private EatingCount eatingCount = new EatingCount();

    private TextView countView;
    private TextView termView;
    private String termTohms = "";

    private int cancelFlag = 0;

    public SensorReceive(TextView countView, TextView termView) {
        this.countView = countView;
        this.termView = termView;
    }

    public void cancel(int v) {
        this.cancelFlag = v;
        this.cancel(true);
    }

    /**
     *Receives UDP packets using AsyncTask
     */
    protected Long doInBackground(Void... something) {
        try {
            /* Retrieve the ServerName */

            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.d("UDP", "S: Connecting...");
            /* Create new UDP-Socket */
            socket = new DatagramSocket(SERVERPORT, serverAddr);

            /* By magic we know, how much data will be waiting for us */
            byte[] buf = new byte[50];

            /* Prepare a UDP-Packet that can contain the data we want to receive */
            long beginTime = System.currentTimeMillis();
            int count = 0;


            while (cancelFlag != 1) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                if (packet == null)
                    break;
                /* Receive the UDP-Packet */
                socket.receive(packet);
                long endTime = System.currentTimeMillis();
                long difference = endTime - beginTime;
                count++;
                //Log.d("UDP", " Received: '" + new String(packet.getData()) + "(" + difference + ", " + count + ")");

                receivedData(packet);
                publishProgress((Integer) countE);
            }
        } catch (Exception e) {
            Log.e("UDP", "S: Error", e);
        }
        return (long) 1;
    }

    protected void onProgressUpdate(Integer... result) {
        countView.setText("" + countE);
        termView.setText("" + termTohms);
    }

    protected void onPostExecute(Long result) {
        //intent = getIntent();
        Log.d("Thread Finished", "");
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d("Thread Cancelled", "");
    }

    /**
     * Receives packet and unpacks packet for saving data to each SensorDataPoint depends on sensor type
     * Also implements Filtering Class and EatingCount Class
     * @param packet (DatagramPacket) received packet
     */
    private void receivedData(DatagramPacket packet) {
        String[] parseDataResult = new String(packet.getData()).split(", ");

        String type = parseDataResult[0];
        float[] values = {Float.parseFloat(parseDataResult[1]), Float.parseFloat(parseDataResult[2]), Float.parseFloat(parseDataResult[3])};
        long time = System.currentTimeMillis();

        SensorDataPoint dataPoint = new SensorDataPoint(time, values);
        if (type.equals("a")) {
            //remoteSensorManager.addSensorData(SENS_ACCELEROMETER, time, values);
            sensorA.addDataPoint(dataPoint);
        } else if (type.equals("g")) {
            //remoteSensorManager.addSensorData(SENS_GYROSCOPE, time, values);
            sensorG.addDataPoint(dataPoint);
        }

        if (sensorG.getSize() > 65) {
            filtering = new Filtering(sensorA.getDataPoints(), sensorG.getDataPoints());
            eatingCount.Count(sensorA, sensorG, countE);
            countE = eatingCount.getEatingCount();
            //countView.setText("Your eating count: " + countE);
            Log.d("SensorReceive", ": " + countE);

            term = eatingCount.getAverage_term();
            termM = (term / 1000) / 60;
            termS = (term / 1000) % 60;
            // termTohms =  String.format("%02d:%02d:%02d",(term/(1000 * 60 * 60)) % 24,(term / (1000 * 60)) % 60,(term / 1000) % 60);
            termTohms = String.format("%02dmin:%02dsec", termM, termS);
            Log.d("Average Term", ":" + term);
        }
    }
}