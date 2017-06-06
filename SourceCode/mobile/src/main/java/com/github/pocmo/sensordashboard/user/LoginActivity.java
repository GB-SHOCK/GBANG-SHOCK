package com.github.pocmo.sensordashboard.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.pocmo.sensordashboard.R;
import com.github.pocmo.sensordashboard.ui.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    ImageButton loginBt, signupBt;
    EditText id,pw;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    ProgressDialog dialog = null;
    SharedPreferences mPref;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        edit = mPref.edit();


        id = (EditText)findViewById(R.id.userID);
        pw = (EditText)findViewById(R.id.userPW);

        loginBt = (ImageButton)findViewById(R.id.login_bt);
        signupBt = (ImageButton)findViewById(R.id.signup_bt);

        signupBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(getApplicationContext(),SignupActivity.class );
                startActivity(intent);
                finish();
            }



        });


        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();
            }


        });
    }

    void login(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://52.78.246.112/userCheck.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username",id.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password",pw.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request
            response=httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            });

            if(response.startsWith("User Found")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        //User Found-$row[0]-$row[1]-$row[2]-$row[3]
                        //age : gender : height : weight
                        // 1  : 2      :   3    :  4

                        String[] inform = response.split(":");
                        Log.d("response",inform[0]);
                        edit.putString("userId",id.getText().toString().trim());
                        edit.putString("userPw",pw.getText().toString().trim());
                        edit.putString("userAge",inform[1]);
                        edit.putString("userGender",inform[2]);
                        edit.putString("userHeight",inform[3]);
                        edit.putString("userWeight",inform[4]);
                        edit.commit();
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }else{
                showAlert();
            }

        }catch(Exception e){
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert(){
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Login Error.");
                builder.setMessage("User not Found.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}
