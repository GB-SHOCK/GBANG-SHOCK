package com.github.pocmo.sensordashboard.ui.eating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.pocmo.sensordashboard.R;


public class EatingResultPopup extends Activity {



    private String count,term,time;
    private TextView countResult, termResult, timeResult;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //// 뒷배경 흐리게 처리 ////
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.9f;// 뒷배경의 어두운 정도
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.eating_result_popup);

        countResult =(TextView)findViewById(R.id.countResult);
        termResult = (TextView)findViewById(R.id.termResult);
        timeResult =(TextView)findViewById(R.id.timeResult);
        intent = getIntent();

        count = intent.getStringExtra("count");
        term = intent.getStringExtra("term");
        time = intent.getStringExtra("time");

        Log.d("intent", count );

        countResult.setText(count);
        termResult.setText(term);
        timeResult.setText(time);

    }
}
