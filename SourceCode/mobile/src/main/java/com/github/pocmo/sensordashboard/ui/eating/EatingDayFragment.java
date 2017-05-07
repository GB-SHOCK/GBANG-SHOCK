package com.github.pocmo.sensordashboard.ui.eating;

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
import com.handstudio.android.hzgrapherlib.vo.Graph;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraphVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.handstudio.android.hzgrapherlib.graphview.CurveGraphView;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraph;

public class EatingDayFragment  extends Fragment {
    private ViewGroup eatingDayGraphView;
    private View rootView;

    private ArrayList<HashMap<String, String>> data;
    private ServerDownload down =  new ServerDownload();
    private static final String TAG_COUNT = "eating_count";
    private static final String TAG_DATE = "eating_date";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.eating_day_fragment, null);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eatingDayGraphView = (ViewGroup) rootView.findViewById(R.id.eatingDayGraphView);

        // setCurveGraph();
    }

    private void setCurveGraph() {
        //all setting
        CurveGraphVO vo = makeCurveGraphAllSetting();

        //default setting
//		CurveGraphVO vo = makeCurveGraphDefaultSetting();

        eatingDayGraphView.addView(new CurveGraphView(this.getActivity(), vo));
    }

    /**
     * make simple Curve graph
     * @return
     */
    private CurveGraphVO makeCurveGraphDefaultSetting() {

        String[] legendArr = {"1", "2", "3", "4", "5"};
        float[] graph1 = {0, 0, 0, 0, 0};

        List<CurveGraph> arrGraph = new ArrayList<CurveGraph>();
        arrGraph.add(new CurveGraph("EATING", 0xaa66ff33, graph1));

        CurveGraphVO vo = new CurveGraphVO(legendArr, arrGraph);
        return vo;
    }

    /**
     * make Curve graph using options
     * @return
     */
    private CurveGraphVO makeCurveGraphAllSetting() {
        //BASIC LAYOUT SETTING
        //padding
        int paddingBottom 	= CurveGraphVO.DEFAULT_PADDING;
        int paddingTop 		= CurveGraphVO.DEFAULT_PADDING;
        int paddingLeft 	= CurveGraphVO.DEFAULT_PADDING;
        int paddingRight 	= CurveGraphVO.DEFAULT_PADDING;

        //graph margin
        int marginTop 		= CurveGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight 	= CurveGraphVO.DEFAULT_MARGIN_RIGHT;

        //max value
        int maxValue 		= CurveGraphVO.DEFAULT_MAX_VALUE;

        //increment
        int increment 		= CurveGraphVO.DEFAULT_INCREMENT;


        //


        data = down.getDataList();
        TextView dayEating = (TextView) getActivity().findViewById(R.id.dayEating);
        int size = data.size();

        //GRAPH SETTING

        String[] legendArr = new String[size];
        float[] graph1 = new float[size];

        for (int i = 0; i < size; i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String[] t = dateFormat.format(System.currentTimeMillis()).split("-");

            String arr[] = data.get(i).get(TAG_DATE).split("-");
            if (!t[0].equals(arr[0]) || !t[1].equals(arr[1]) || !t[2].equals(arr[2]))
                continue;
            else {
                legendArr[i] = Integer.parseInt(arr[3]) + "";
                Log.d("day time", legendArr[i]);

                graph1[i] = Float.parseFloat(data.get(i).get(TAG_COUNT)) * 12;

                if (maxValue < Integer.parseInt(data.get(i).get(TAG_COUNT)))
                    maxValue = Integer.parseInt(data.get(i).get(TAG_COUNT));

            }
        }


        List<CurveGraph> arrGraph = new ArrayList<CurveGraph>();

        arrGraph.add(new CurveGraph("eating", 0xaabaaca1, graph1, R.drawable.rice));



        CurveGraphVO vo = new CurveGraphVO(
                paddingBottom, paddingTop, paddingLeft, paddingRight,
                marginTop, marginRight, maxValue, increment, legendArr, arrGraph);
        //set animation
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION));
        vo.setGraphNameBox(new GraphNameBox());


        //set draw graph region
//		vo.setDrawRegion(true);

        //use icon
//		arrGraph.add(new CurveGraph("EATING",0xaa66ff33, graph1, R.drawable.rice));
//		arrGraph.add(new Graph(0xaa00ffff, graph2, R.drawable.icon2));
//		arrGraph.add(new Graph(0xaaff0066, graph3, R.drawable.icon3));

//		CurveGraphVO vo = new CurveGraphVO(
//				paddingBottom, paddingTop, paddingLeft, paddingRight,
//				marginTop, marginRight, maxValue, increment, legendArr, arrGraph, R.drawable.bg);


        return vo;




    }


    @Override
    public void onResume() {
        super.onResume();
        down = new ServerDownload();
        setCurveGraph();
    }
}
