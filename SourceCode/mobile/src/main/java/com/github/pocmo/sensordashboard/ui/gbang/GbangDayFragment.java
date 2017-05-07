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

public class GbangDayFragment extends Fragment {
    private ViewGroup dayGbangShockGraphView;
    private View rootView;
    private TextView coachView;
    private ArrayList<HashMap<String, String>> data;
    private ServerDownload download;
    private Context context;
    private static final String TAG_COUNT = "eating_count";
    private static final String TAG_TERM = "average_term";
    private static final String TAG_TIME = "eating_time";
    private static final String TAG_DATE = "eating_date";

    private HashMap<Integer, Integer> stepCalorie = new HashMap<Integer, Integer>();

    SharedPreferences stepCal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.gbang_day_fragment, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dayGbangShockGraphView = (ViewGroup) rootView.findViewById(R.id.gbangDayGraphView);
        context = this.getActivity();

        stepCal = context.getSharedPreferences("step", context.MODE_PRIVATE);

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
//		LineGraphVO vo = makeLineCompareGraphDefaultSetting();

        dayGbangShockGraphView.addView(new LineCompareGraphView(this.getActivity(), vo));
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

        if(data.size()!=0){
        //GRAPH SETTING
        String[] legendTemp = new String[stepCalorie.size()];
        float[] graph1Temp = new float[data.size()];
        int index = 0;
        int last = 0;
        int current = 0;
        float totalGraph1 = 0, totalGraph2 = 0;

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

                if (maxValue < Integer.parseInt(data.get(i).get(TAG_COUNT)))
                    maxValue = Integer.parseInt(data.get(i).get(TAG_COUNT));
                index++;
            }
        }

        String[] legendArr = new String[index];
        float[] graph1 = new float[index];
        //   float[] graph2 = new float[data.size()];
        float[] graph2 = new float[stepCalorie.size()];

        for (int i = 0; i < index; i++) {
            legendArr[i] = legendTemp[i];
            graph1[i] = graph1Temp[i];
        }

        index = 0;

        for (int i = 0; i < 24; i++) {
            if (stepCalorie.containsKey(i)) {
                current = stepCalorie.get(i);
                graph2[index] = (current - last) * (float)0.5;
                //legendArr[index] = i + "";
                index++;
                // }
                last = current;
            }
        }


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
            coachView = (TextView) getActivity().findViewById(R.id.dayGbang);

            if (totalGraph1 - totalGraph2 > 0) {
            coachView.setText("하루 칼로리보다 " + (totalGraph1 - totalGraph2) + "kcal 더 드셨습니다.\n그만드세요");

        } else {
            coachView.setText("하루 칼로리보다 " + (totalGraph2 - totalGraph1) + "kcal 덜 드셨습니다.\n더 드세요");
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