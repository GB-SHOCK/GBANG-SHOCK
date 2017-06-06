package com.github.pocmo.sensordashboard.ui.gbang;

import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pocmo.sensordashboard.R;
import com.github.pocmo.sensordashboard.Server.EatingMonthData;
import com.github.pocmo.sensordashboard.Server.MovingMonthData;
import com.github.pocmo.sensordashboard.Server.ServerDownload;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.LineCompareGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.linegraph.LineGraph;
import com.handstudio.android.hzgrapherlib.vo.linegraph.LineGraphVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GbangMonthFragment extends Fragment {
    private ViewGroup monthGbangShockGraphView;
    private View rootView;
    private TextView coachView;
    private ArrayList<HashMap<String, String>> eMonth;
    private EatingMonthData eMonthdata;
    private static final String e_TAG_COUNT = "eating_count";
    private static final String e_TAG_DATE = "eating_date";

    private ArrayList<HashMap<String, String>> mMonth;
    private MovingMonthData mMonthdata;
    private static final String m_TAG_COUNT = "step_count";
    private static final String m_TAG_DATE = "timestamp";

    private Context c;
    SharedPreferences user;
    private String userA;
    private String userG;
    private String userH;
    private String userW;
    private float userBMR;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.gbang_month_fragment, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        monthGbangShockGraphView = (ViewGroup) rootView.findViewById(R.id.gbangMonthGraphView);
        c = this.getActivity();
        user = c.getSharedPreferences("user", c.MODE_PRIVATE);
        userA = user.getString("userAge", "1");
        userG = user.getString("userGender", "m");
        userH = user.getString("userHeight", "1");
        userW = user.getString("userWeight", "1");

        //l   BMR(남)  = (10 × W) + (6.25 × H) - (5 × A) + 5
        // l   BMR(여) = (10 × W) + (6.25 × H) - (5 × A) – 161

        if(userG.equals("m")){
            userBMR = (float) ((10 * Float.parseFloat(userW)) + (6.25 * Float.parseFloat(userH)) - (5 * Float.parseFloat(userA)) + 5);
        }
        else{
            userBMR = (float) ((10 * Float.parseFloat(userW)) + (6.25 * Float.parseFloat(userH)) - (5 * Float.parseFloat(userA)) - 161);
        }

        coachView = (TextView) rootView.findViewById(R.id.monthGbang);
        //download = new ServerDownload();
        //setLineCompareGraph();
    }

    private void setLineCompareGraph() {
        //all setting
        LineGraphVO vo = makeLineCompareGraphAllSetting();

        //default setting
//		LineGraphVO vo = makeLineCompareGraphDefaultSetting();

        monthGbangShockGraphView.addView(new LineCompareGraphView(this.getActivity(), vo));
    }

    /**
     * make simple line graph
     *
     * @return
     */
    private LineGraphVO makeLineCompareGraphDefaultSetting() {

        String[] legendArr = {"1", "2", "3", "4", "5"};
        float[] graph1 = {5, 1, 3, 2, 1};
        float[] graph2 = {0, 1, 2, 1, 2};

        List<LineGraph> arrGraph = new ArrayList<LineGraph>();
        arrGraph.add(new LineGraph("eating", 0xaa66ff33, graph1));
        arrGraph.add(new LineGraph("moving", 0xaa00ffff, graph2));

        LineGraphVO vo = new LineGraphVO(legendArr, arrGraph);
        return vo;
    }

    /**
     * make line graph using options
     *
     * @return
     */
    private LineGraphVO makeLineCompareGraphAllSetting() {
        //BASIC LAYOUT SETTING
        //padding
        int paddingBottom = LineGraphVO.DEFAULT_PADDING;
        int paddingTop = LineGraphVO.DEFAULT_PADDING;
        int paddingLeft = LineGraphVO.DEFAULT_PADDING;
        int paddingRight = LineGraphVO.DEFAULT_PADDING;

        //graph margin
        int marginTop = LineGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight = LineGraphVO.DEFAULT_MARGIN_RIGHT;

        //max value
        int maxValue = LineGraphVO.DEFAULT_MAX_VALUE;

        //increment
        int increment = LineGraphVO.DEFAULT_INCREMENT;


        //moving
        mMonth = mMonthdata.getDataList();
        int mSize = mMonth.size();
        //eating
        eMonth = eMonthdata.getDataList();
        int eSize = eMonth.size();

        int arrSize = eSize;

        //legend size
        if(arrSize<mSize)
            arrSize = mSize;


        String[] legendArr = new String[arrSize];

        //eating graph
        float[] graph1 = new float[eSize];
        float totalGraph1 = 0, totalGraph2 = 0;
        for (int i = 0; i < eSize; i++) {

            String arr[] = eMonth.get(i).get(e_TAG_DATE).split("-");
            legendArr[i] = Integer.parseInt(arr[1]) + "";
            Log.d("Month time", legendArr[i]);

            graph1[i] = Float.parseFloat(eMonth.get(i).get(e_TAG_COUNT)) * (float)12;
            Log.d("Month count",graph1[i]+"");

            if (maxValue < graph1[i])
                maxValue = (int) graph1[i];

            totalGraph1+=graph1[i];
        }


        float[] graph2 = new float[mSize];
        float mile, mPerC,temp;

        for (int i = 0; i < mSize; i++) {

            String arr[] = mMonth.get(i).get(m_TAG_DATE).split("-");
            legendArr[i] = Integer.parseInt(arr[1]) + "";
            Log.d("Month time", legendArr[i]);

            temp = Float.parseFloat(mMonth.get(i).get(m_TAG_COUNT));
            mile = ((Float.parseFloat(userH) - 100) * temp) / 100;
            mPerC = (float) (3.7103 + 0.2678 * Float.parseFloat(userW) + (0.0359 * temp * 60 * 0.0006213) * 2) * Float.parseFloat(userW);
            graph2[i] = (float) (mile * mPerC * 0.00006213);
            Log.d("Month count",graph2[i]+"");

            if (maxValue < graph2[i])
                maxValue = (int) graph2[i];

            totalGraph2+=graph2[i];
        }
        increment = maxValue/8;

        float bmrSum = 0;
        for(int i = 0 ; i<mSize;i++)
            bmrSum+=userBMR;



        List<LineGraph> arrGraph = new ArrayList<LineGraph>();

        arrGraph.add(new LineGraph("eating", 0xaabaaca1, graph1, R.drawable.rice));
        arrGraph.add(new LineGraph("moving", 0xaa8c705d, graph2, R.drawable.run));

        LineGraphVO vo = new LineGraphVO(paddingBottom, paddingTop, paddingLeft, paddingRight,
                marginTop, marginRight, maxValue, increment, legendArr, arrGraph);

        //set animation
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION));
        //set graph name box
        vo.setGraphNameBox(new GraphNameBox());

        //use icon
//		arrGraph.add(new Graph(0xaa66ff33, graph1, R.drawable.icon1));
//		arrGraph.add(new Graph(0xaa00ffff, graph2, R.drawable.icon2));
//		arrGraph.add(new Graph(0xaaff0066, graph3, R.drawable.icon3));

//		LineGraphVO vo = new LineGraphVO(
//				paddingBottom, paddingTop, paddingLeft, paddingRight,
//				marginTop, marginRight, maxValue, increment, legendArr, arrGraph, R.drawable.bg);
        if (totalGraph1>totalGraph2) {
            coachView.setText("이번 달은 권장 칼로리보다 " + (totalGraph1 - totalGraph2) + "kcal 더 드셨습니다.\n 잘하셨지만, 무조건 굶는 것은 요요의 지름길!");

        }
        else if(totalGraph1==0){

            coachView.setText("이번 달은 식사를 하지 않으셨네요! 규칙적인 식사가 중요합니다!");

        }
        else if(totalGraph1==totalGraph2){

            coachView.setText("이번 달은 권장 칼로리 모두 섭취! 잘하셨습니다!! 지금 운동하면 지방이 SHOCK!");
        }
        else {
            coachView.setText("이번 달은 권장 칼로리보다 " + (totalGraph2 - totalGraph1) + "kcal 덜 드셨습니다.\n 잘 하셨지만, 규칙적인 식사만이 건강을 유지할 수 있는 법!");
        }


        return vo;

    }

    @Override
    public void onResume() {
        super.onResume();


        eMonthdata = new EatingMonthData();
        mMonthdata = new MovingMonthData();
        setLineCompareGraph();
    }

}