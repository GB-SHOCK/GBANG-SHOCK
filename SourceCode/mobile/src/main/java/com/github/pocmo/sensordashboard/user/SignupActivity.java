package com.github.pocmo.sensordashboard.user;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.pocmo.sensordashboard.R;
import com.github.pocmo.sensordashboard.Server.ServerUpload;

public class SignupActivity extends Activity {

    private UserInformUpload infoUpload;
    EditText userId, userPw, userAge, userHeight, userWeight;
    RadioGroup sex;
    RadioButton mfm;
    Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        userId = (EditText) findViewById(R.id.userId);
        userPw = (EditText) findViewById(R.id.userPw);
        userAge = (EditText) findViewById(R.id.userAge);
        userHeight = (EditText) findViewById(R.id.userHeight);
        userWeight = (EditText) findViewById(R.id.userWeight);

        sex = (RadioGroup) findViewById(R.id.sex);
        submit = (Button) findViewById(R.id.submitBt);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id, pw, gender,age, height, weight;

                id = userId.getText().toString();
                pw = userPw.getText().toString();
                age = userAge.getText().toString();
                height = userHeight.getText().toString();
                weight = userWeight.getText().toString();
                Toast.makeText(getApplicationContext(),"height:"+height,Toast.LENGTH_LONG).show();
                int selected = sex.getCheckedRadioButtonId();
                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                mfm = (RadioButton) findViewById(selected);
                gender = mfm.getText().toString();

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();

//ID,PW,age,gender,height,weight
                //upload to server database
                infoUpload = new UserInformUpload();
                infoUpload.insertUserTable(id, pw,age,gender,height,weight);



            }

        });


    }


}
