package com.github.pocmo.sensordashboard.ui.moving;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pocmo.sensordashboard.R;
import com.github.pocmo.sensordashboard.Server.MovingMonthData;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.CurveGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraph;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraphVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MovingMonthFragment  extends Fragment {
    private ViewGroup movingMonthGraphView;
    private View rootView;
    private MovingMonthData monthData;
    private ArrayList<HashMap<String, String>> month;
    private static final String TAG_DATE = "timestamp";
    private static final String TAG_COUNT = "step_count";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setHasOptionsMenu(true);
        rootView = (ViewGroup) inflater.inflate(R.layout.moving_month_fragment, null);
        return rootView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movingMonthGraphView = (ViewGroup) rootView.findViewById(R.id.movingMonthGraphView);
        //  setCurveGraph();
    }

    private void setCurveGraph() {
        //all setting
        CurveGraphVO vo = makeCurveGraphAllSetting();

        //default setting
//      CurveGraphVO vo = makeCurveGraphDefaultSetting();

        movingMonthGraphView.addView(new CurveGraphView(this.getActivity(), vo));
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
        arrGraph.add(new CurveGraph("step", 0xaa66ff33, graph1));
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
        int increment = CurveGraphVO.DEFAULT_INCREMENT;

//        String[] legendArr = new String[step.size() ];
        month = monthData.getDataList();
        int size = month.size();
        float kSum = 0, kAvg = 0,sSum=0,sAvg=0;

        String[] legendArr = new String[size];
//        String[] legendArr = {"1","2","3","4","5"};

        float[] graph1 = new float[size];
        float[] step = new float[size];

        for (int i = 0; i < size; i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            //String[] t = dateFormat.format(System.currentTimeMillis()).split("-");

            String arr[] = month.get(i).get(TAG_DATE).split("-");
            legendArr[i] = Integer.parseInt(arr[1]) + "";
            Log.d("monthtime", legendArr[i]);


            step[i] = Float.parseFloat(month.get(i).get(TAG_COUNT));
            graph1[i] = step[i] * (float)0.4;
            Log.d("count",graph1[i]+"");
            sSum+=step[i];
            kSum+=graph1[i];

            if (maxValue < graph1[i])
                maxValue = (int) graph1[i];
        }

        kAvg = kSum/size;
        TextView monthKcal = (TextView)getActivity().findViewById(R.id.mMonthKcal);
        int kcals = (int)kAvg;
        monthKcal.setText(kcals+"");

        sAvg = sSum/size;
        TextView monthStep = (TextView)getActivity().findViewById(R.id.monthStep);
        int steps = (int)sAvg;
        monthStep.setText(steps+"");

        List<CurveGraph> arrGraph = new ArrayList<CurveGraph>();

        arrGraph.add(new CurveGraph("step", 0xaa66ff33, graph1, R.drawable.run));
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

        monthData = new MovingMonthData();
        setCurveGraph();
    }
}