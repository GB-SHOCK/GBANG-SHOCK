package com.github.pocmo.sensordashboard.ui.moving;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class DayTask  extends BroadcastReceiver {

    //step count sharedpreference

    SharedPreferences stepValue;

    @Override
    public void onReceive(Context c, Intent intent) {
        stepValue = c.getSharedPreferences("today", Context.MODE_PRIVATE);
        String step,date;

        step = stepValue.getString("1","0");

        insertUserTable(step);

    }


    public void insertUserTable(String step) {

        try {
            uploadDB(step);

        } catch (Exception e) {
            Log.e("File", ": " + e);
        }


    }
    private void uploadDB(String step) {

        class UploadUserData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("ServerTest", " :Sensor// " + s);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String link = "http://52.78.78.185/stepUpload.php";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");

                    String step = (String) params[0];
                    String date = (String) dateFormat.format(System.currentTimeMillis()) + "-" + (String) timeFormat.format(System.currentTimeMillis());




                    String data = URLEncoder.encode("step", "UTF-8") + "=" + URLEncoder.encode(step, "UTF-8");
                    data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");

                    System.out.println("data:"+data);

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Read SensorReceive Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }

        }
        UploadUserData task = new UploadUserData();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,step);

    }


}