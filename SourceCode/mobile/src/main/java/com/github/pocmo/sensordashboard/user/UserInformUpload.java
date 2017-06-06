package com.github.pocmo.sensordashboard.user;

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
 * Created by Bae Somi on 2016-11-21.
 */
public class UserInformUpload {


    public UserInformUpload(){


//ID,PW,age,gender,height,weight
    }


    public void insertUserTable(String id, String pw, String age, String mfm, String height, String weight) {

        try {
            uploadDB(id, pw, age, mfm, height, weight);

        } catch (Exception e) {
            Log.e("File", ": " + e);
        }


    }
    private void uploadDB(String id, String pw, String age, String mfm, String height, String weight) {

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
                    String link = "http://52.78.246.112/userInfoUpload.php";

                    String id = (String) params[0];
                    String pw = (String) params[1];
                    String age = (String) params[2];
                    String mfm = (String) params[3];
                    String height = (String) params[4];
                    String weight = (String) params[5];



                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("pw", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");
                    data += "&" + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8");
                    data += "&" + URLEncoder.encode("mfm", "UTF-8") + "=" + URLEncoder.encode(mfm, "UTF-8");
                    data += "&" + URLEncoder.encode("height", "UTF-8") + "=" + URLEncoder.encode(height, "UTF-8");
                    data += "&" + URLEncoder.encode("weight", "UTF-8") + "=" + URLEncoder.encode(weight, "UTF-8");

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
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id,pw,age,mfm,height,weight);

    }


}


