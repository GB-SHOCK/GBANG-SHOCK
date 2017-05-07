package com.github.pocmo.sensordashboard.Server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

/**
 * Created by OneStar on 2016-09-24.
 */

public class ServerUpload {


    public ServerUpload() {
       /* try {
            url = new URL("http://192.168.0.18:8080/index2.jsp");
            conn = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void insertEatingTable(String count, String term, String time) {
        try {
            uploadToDB("EatingCount", count, term, time);

        } catch (Exception e) {
            Log.e("File", ": " + e);
        }
    }

    private void uploadToDB(String user, String count, String term, String time) {

        class UploadData extends AsyncTask<String, Void, String> {

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
                    String link = "http://52.78.78.185/eatingtable_upload.php";//http://52.78.6.137/sensordata_insert.html
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");

                    String count = (String) params[0];
                    String term = (String) params[1];
                    String time = (String) params[2];
                    String date = (String) dateFormat.format(System.currentTimeMillis()) + "-" + (String) timeFormat.format(System.currentTimeMillis());

                    String data = URLEncoder.encode("count", "UTF-8") + "=" + URLEncoder.encode(count, "UTF-8");
                    data += "&" + URLEncoder.encode("term", "UTF-8") + "=" + URLEncoder.encode(term, "UTF-8");
                    data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
                    data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");

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
        UploadData task = new UploadData();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, count, term, time);
    }
}
