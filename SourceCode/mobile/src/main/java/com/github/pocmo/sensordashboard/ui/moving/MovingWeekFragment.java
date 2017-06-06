package com.github.pocmo.sensordashboard.ui.moving;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pocmo.sensordashboard.R;
import com.github.pocmo.sensordashboard.Server.MovingWeekData;
import com.github.pocmo.sensordashboard.Server.ServerDownload;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.CurveGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraph;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraphVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MovingWeekFragment  extends Fragment {
    private ViewGroup movingWeekGraphView;
    private View rootView;
    private MovingWeekData weekData;

    private ArrayList<HashMap<String, String>> week;
    private static final String TAG_DATE = "timestamp";
    private static final String TAG_COUNT = "step_count";
    private Context c;
    SharedPreferences user;
    private String userH;
    private String userW;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setHasOptionsMenu(true);
        rootView = (ViewGroup) inflater.inflate(R.layout.moving_week_fragment, null);
        return rootView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movingWeekGraphView = (ViewGroup) rootView.findViewById(R.id.movingWeekGraphView);
        c = this.getActivity();
        //user info read
        user = c.getSharedPreferences("user", c.MODE_PRIVATE);
        userH = user.getString("userHeight", "1");
        userW = user.getString("userWeight","1");
        //   setCurveGraph();
    }

    private void setCurveGraph() {
        //all setting
        CurveGraphVO vo = makeCurveGraphAllSetting();

        //default setting
//      CurveGraphVO vo = makeCurveGraphDefaultSetting();

        movingWeekGraphView.addView(new CurveGraphView(this.getActivity(), vo));
    }

    /**
     * make simple Curve graph
     *
     * @return
     */
    private CurveGraphVO makeCurveGraphDefaultSetting() {

        String[] legendArr = {"1", "2", "3", "4", "5"};

        float[] graph1 = {000, 100, 200, 100, 200};
        //  float[] graph3       = {200,500,300,400,000};

        List<CurveGraph> arrGraph = new ArrayList<CurveGraph>();
        arrGraph.add(new CurveGraph("moving", 0xaa66ff33, graph1));
        //arrGraph.add(new CurveGraph("ios", 0xaa00ffff, graph2));
        // arrGraph.add(new CurveGraph("tizen", 0xaaff0066, graph3));

        CurveGraphVO vo = new CurveGraphVO(legendArr, arrGraph);
        return vo;
    }

    /**
     * make Curve graph using options
     *
     * @return
     */
    private CurveGraphVO makeCurveGraphAllSetting() {
        //BASIC LAYOUT SETTING
        //padding
        int paddingBottom = CurveGraphVO.DEFAULT_PADDING;
        int paddingTop = CurveGraphVO.DEFAULT_PADDING;
        int paddingLeft = CurveGraphVO.DEFAULT_PADDING;
        int paddingRight = CurveGraphVO.DEFAULT_PADDING;

        //graph margin
        int marginTop = CurveGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight = CurveGraphVO.DEFAULT_MARGIN_RIGHT;

        //max value
        int maxValue = CurveGraphVO.DEFAULT_MAX_VALUE;
        //increment
        int increment = 500;


        week = weekData.getDataList();
        int size = week.size();
        float kSum = 0, kAvg = 0,sSum=0,sAvg=0;

        String[] legendArr = new String[size];
//        String[] legendArr = {"1","2","3","4","5"};

        float[] graph1 = new float[size];
        float[] step = new float[size];
        float mile, mPerC;

        for (int i = 0; i < size; i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //String[] t = dateFormat.format(System.currentTimeMillis()).split("-");

            String arr[] = week.get(i).get(TAG_DATE).split("-");
            legendArr[i] = Integer.parseInt(arr[2]) + "";
            Log.d("time", legendArr[i]);

            step[i] = Float.parseFloat(week.get(i).get(TAG_COUNT));
            mile = ((Float.parseFloat(userH) - 100) * step[i]) / 100;
            mPerC = (float) (3.7103 + 0.2678 * Float.parseFloat(userW) + (0.0359 * step[i] * 60 * 0.0006213) * 2) * Float.parseFloat(userW);
            graph1[i] = (float) (mile * mPerC * 0.00006213);

            Log.d("count",graph1[i]+"");
            sSum+=step[i];
            kSum+=graph1[i];

            if (maxValue <= graph1[i])
                maxValue = (int) graph1[i];
        }
        increment = maxValue/8;

        kAvg = kSum/size;
        TextView weekKcal = (TextView)getActivity().findViewById(R.id.mWeekKcal);
        int kcals = (int)kAvg;
        weekKcal.setText(kcals+"");

        sAvg = sSum/size;
        TextView weekStep = (TextView)getActivity().findViewById(R.id.weekStep);
        int steps = (int)sAvg;
        weekStep.setText(steps+"");

        List<CurveGraph> arrGraph = new ArrayList<CurveGraph>();

        arrGraph.add(new CurveGraph("moving", 0xaa66ff33, graph1, R.drawable.run));

        //  arrGraph.add(new CurveGraph("ios", 0xaa00ffff, graph2));
        //  arrGraph.add(new CurveGraph("tizen", 0xaaff0066, graph3));

        CurveGraphVO vo = new CurveGraphVO(
                paddingBottom, paddingTop, paddingLeft, paddingRight,
                marginTop, marginRight, maxValue, increment, legendArr, arrGraph);

        //set animation
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION));
        //set graph name box
        vo.setGraphNameBox(new GraphNameBox());
        //set draw graph region
//      vo.setDrawRegion(true);

        //use icon
//      arrGraph.add(new Graph(0xaa66ff33, graph1, R.drawable.icon1));
//      arrGraph.add(new Graph(0xaa00ffff, graph2, R.drawable.icon2));
//      arrGraph.add(new Graph(0xaaff0066, graph3, R.drawable.icon3));

//      CurveGraphVO vo = new CurveGraphVO(
//            paddingBottom, paddingTop, paddingLeft, paddingRight,
//            marginTop, marginRight, maxValue, increment, legendArr, arrGraph, R.drawable.bg);
        return vo;
    }


    @Override
    public void onResume() {
        super.onResume();

        weekData = new MovingWeekData();
        setCurveGraph();
    }
}