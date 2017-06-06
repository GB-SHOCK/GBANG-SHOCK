package com.github.pocmo.sensordashboard.ui;

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

public class DayFragment extends Fragment {
    private ViewGroup dayEatingShockGraphView;
    private View rootView;
    private TextView coachView;
    private ArrayList<HashMap<String, String>> data;
    private ServerDownload download;
    private Context context;
    private static final String TAG_COUNT = "eating_count";
    private static final String TAG_DATE = "eating_date";

    private HashMap<Integer, Integer> stepCalorie = new HashMap<Integer, Integer>();

    SharedPreferences stepCal;

    //user info read
    SharedPreferences user;
    private String userA;
    private String userG;
    private String userH;
    private String userW;
    private float userBMR;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.day_fragment, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dayEatingShockGraphView = (ViewGroup) rootView.findViewById(R.id.dayEatingShockGraphView);
        coachView = (TextView) rootView.findViewById(R.id.coachingView);
        context = this.getActivity();

        stepCal = context.getSharedPreferences("step", context.MODE_PRIVATE);

        //user inform download
        user = context.getSharedPreferences("user", context.MODE_PRIVATE);
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


        int cnt = 0;
        //sharedpreferences values assigned hashMap.
        for (int i = 0; i < 24; i++) {
            if (stepCal.contains(i + "")) {
                cnt = stepCal.getInt(i + "", 0);
                //  Log.d("Count", cnt + "-");
                stepCalorie.put(i, cnt);
                //   System.out.println("key:" + i +"value"+ step.get(i));
            }
        }

        //download = new ServerDownload();
        //setLineCompareGraph();
    }

    private void setLineCompareGraph() {
        //all setting
        LineGraphVO vo = makeLineCompareGraphAllSetting();

        //default setting
//      LineGraphVO vo = makeLineCompareGraphDefaultSetting();

        dayEatingShockGraphView.addView(new LineCompareGraphView(this.getActivity(), vo));
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

        data = download.getDataList();

        if(data.size()!=0)
        {
            //GRAPH SETTING
            String[] legendTemp = new String[24];
            float[] graph1Temp = new float[data.size()];
            int index = 0;
            int last = 0;
            int current = 0;
            float totalGraph1 = 0, totalGraph2 = 0;
            float tempStep, mile, mPerC;

            for (int i = 0; i < data.size(); i++) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String[] t = dateFormat.format(System.currentTimeMillis()).split("-");

                String arr[] = data.get(i).get(TAG_DATE).split("-");
                if (!t[0].equals(arr[0]) || !t[1].equals(arr[1]) || !t[2].equals(arr[2]))
                    continue;
                else {
                    legendTemp[index] = Integer.parseInt(arr[3]) + "";
                    Log.d("time", legendTemp[index]);

                    graph1Temp[index] = Float.parseFloat(data.get(i).get(TAG_COUNT)) * 12;

                    totalGraph1 += graph1Temp[index];

                    if (maxValue <= Integer.parseInt(data.get(i).get(TAG_COUNT)))
                        maxValue = Integer.parseInt(data.get(i).get(TAG_COUNT));
                    index++;


                }
            }

            String[] legendArr = new String[index];
            float[] graph1 = new float[index];
            //   float[] graph2 = new float[data.size()];
            float[] graph2 = new float[24];

            for (int i = 0; i < index; i++) {
                legendArr[i] = legendTemp[i];
                graph1[i] = graph1Temp[i];
            }

            index = 0;

            for (int i = 0; i < 24; i++) {
                if (stepCalorie.containsKey(i)) {
                    current = stepCalorie.get(i);
                    tempStep = current - last;
                    mile = ((Float.parseFloat(userH) - 100) * tempStep) / 100;
                    mPerC = (float) (3.7103 + 0.2678 * Float.parseFloat(userW) + (0.0359 * tempStep * 60 * 0.0006213) * 2) * Float.parseFloat(userW);
                    graph2[index] = (float) (mile * mPerC * 0.00006213);
                    totalGraph2+=graph2[index];
                    index++;

                    last = current;
                }
            }

            totalGraph2+=userBMR;

            increment = maxValue/8;

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
        //    coachView = (TextView) getActivity().findViewById(R.id.coachingView);
            if (totalGraph1>totalGraph2) {
                coachView.setText("하루 권장 칼로리보다 " + (totalGraph1 - totalGraph2) + "kcal 더 드셨습니다.\n 잘하셨지만, 무조건 굶는 것은 요요의 지름길!");

            }
            else if(totalGraph1==0){

                coachView.setText("오늘은 식사를 하지 않으셨네요! 규칙적인 식사가 중요합니다!");

            }
            else if(totalGraph1==totalGraph2){

                coachView.setText("오늘은 권장 칼로리 모두 섭취! 잘하셨습니다!! 지금 운동하면 지방이 SHOCK!");
            }
            else {
                coachView.setText("하루 권장 칼로리보다 " + (totalGraph2 - totalGraph1) + "kcal 덜 드셨습니다.\n 잘 하셨지만, 규칙적인 식사만이 건강을 유지할 수 있는 법!");
            }
            return vo;
        }
        return makeLineCompareGraphDefaultSetting();
    }

    @Override
    public void onResume() {
        super.onResume();

        download = new ServerDownload();
        setLineCompareGraph();
    }

}