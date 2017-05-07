package com.github.pocmo.sensordashboard.ui.moving;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pocmo.sensordashboard.R;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.CurveGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraph;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraphVO;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionResult;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import com.samsung.android.sdk.healthdata.HealthResultHolder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
    이동거리(m) = ((키(cm) - 100)  * 걸음수)/100
    마일당 칼로리(cal/mile) =  3.7103 + 0.2678*체중(kg) + (0.0359*(체중(kg)*60*0.0006213)*2)*체중(kg)
    소비칼로리(cal) = 이동거리(m) * 마일당 칼로리(cal/mile) * 0.0006213
*/

public class MovingDayFragment extends Fragment {
    private ViewGroup movingDayGraphView;
    private View rootView;

    public static final String APP_TAG = "SimpleHealth";
    private final int MENU_ITEM_PERMISSION_SETTING = 1;
    private static MovingDayFragment mInstance = null;
    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnError;
    private Set<PermissionKey> mKeySet;
    private StepCountReporter mReporter;
    private Context c;
    private HashMap<Integer, Integer> step = new HashMap<Integer, Integer>();

    //read step for Graph
    SharedPreferences stepHash;
    //server upload
    SharedPreferences todayStep;
    SharedPreferences.Editor todayEditor;
    //user info read
    SharedPreferences user;
    private String userH;
    private String userW;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setHasOptionsMenu(true);
        rootView = (ViewGroup) inflater.inflate(R.layout.moving_day_fragment, null);
        return rootView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movingDayGraphView = (ViewGroup) rootView.findViewById(R.id.movingDayGraphView);
        c = this.getActivity();

        //read stepreporter
        stepHash = c.getSharedPreferences("step", c.MODE_PRIVATE);
        //today step save
        todayStep = c.getSharedPreferences("today",c.MODE_PRIVATE);
        todayEditor = todayStep.edit();

        //user info read
        user = c.getSharedPreferences("user", c.MODE_PRIVATE);
        userH = user.getString("userHeight", "1");
        userW = user.getString("userWeight","1");

        int cnt = 0;
        //sharedpreferences values assigned hashMap.
        for (int i = 0; i < 24; i++) {
            if (stepHash.contains(i + "")) {
                cnt = stepHash.getInt(i + "", 0);
               //  Log.d("Count", cnt + "-");
                step.put(i, cnt);
             //   System.out.println("key:" + i +"value"+ step.get(i));
            }
        }

        setCurveGraph();

        //getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mInstance = this;
        mKeySet = new HashSet<PermissionKey>();
        mKeySet.add(new PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, PermissionType.READ));
        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(this.getActivity(), mConnectionListener);
        // Request the connection to the health data store
        mStore.connectService();


    }

    private void setCurveGraph() {
        //all setting
        CurveGraphVO vo = makeCurveGraphAllSetting();

        //default setting
//      CurveGraphVO vo = makeCurveGraphDefaultSetting();

        movingDayGraphView.addView(new CurveGraphView(this.getActivity(), vo));
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

        String[] legendArr = new String[step.size() ];
        //String[] legendArr = {"1","2","3","4","5"};

        int legendIdx = 0;
        int last= 0;
        int current = 0;
        float tempStep;
        float[] graph1 = new float[step.size()];
        float mile, mPerC;

        //  float[] graph1       = {000,100,200,100,200};
        // float[] graph3       = {200,500,300,400,000};

        for (int i = 0; i < 24; i++) {
            if (step.containsKey(i)) {
                current = step.get(i);
                tempStep = current - last;
                mile = ((Float.parseFloat(userH)- 100)  * tempStep)/100;
                mPerC = (float) (3.7103 + 0.2678*Float.parseFloat(userW) + (0.0359*tempStep*60*0.0006213)*2)*Float.parseFloat(userW);
                graph1[legendIdx] = (float) (mile * mPerC *  0.00006213);
//                  graph1[legendIdx] = current - last ;
                    legendArr[legendIdx] = i + "";
                    legendIdx++;
               // }
                last = current;

                if (maxValue < step.get(i)) {
                    maxValue = step.get(i);
                }

            }


        }


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
    public void onDestroy() {
        mStore.disconnectService();
        super.onDestroy();
    }

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            Log.d(APP_TAG, "Health data service is connected.");
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
            mReporter = new StepCountReporter(c, mStore);

            try {
                // Check whether the permissions that this application needs are acquired
                Map<PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);

                if (resultMap.containsValue(Boolean.FALSE)) {
                    // Request the permission for reading step counts if it is not acquired
                    pmsManager.requestPermissions(mKeySet, MovingDayFragment.this.getActivity()).setResultListener(mPermissionListener);
                } else {
                    // Get the current step count and display it
                    mReporter.start();
                }
            } catch (Exception e) {
                Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(APP_TAG, "Permission setting fails.");
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(APP_TAG, "Health data service is not available.");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.");
        }
    };

    private final HealthResultHolder.ResultListener<PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<PermissionResult>() {

                @Override
                public void onResult(PermissionResult result) {
                    Log.d(APP_TAG, "Permission callback is received.");
                    Map<PermissionKey, Boolean> resultMap = result.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        drawStepCount("");
                        showPermissionAlarmDialog();
                    } else {
                        // Get the current step count and display it
                        mReporter.start();
                    }
                }
            };

    public void drawStepCount(String count) {

        TextView stepCountTv = (TextView) getActivity().findViewById(R.id.editHealthDateValue1);
        TextView mDayKcal = (TextView) getActivity().findViewById(R.id.mDayKcal);
        // Display the today step count so far
        stepCountTv.setText(count);

        /*
     걷는 중 칼로리 소비량 = 마일당 칼로리 × 거리(m) × 0.00006213
    마일당 칼로리 = 3.7103 + 0.2678 × 몸무게 + [0.0359× ( 걸음수 × 60 × 0.0006213)2] × 몸무게
    거리 (m) = [(신장 - 100 ) × 걸음수 ]/100
        */
        float mile, mPerC,kcal;
        mile = ((Float.parseFloat(userH)- 100)  * Integer.parseInt(count))/100;
        mPerC = (float) (3.7103 + 0.2678*Float.parseFloat(userW) + (0.0359*Float.parseFloat(count)*60*0.0006213)*2)*Float.parseFloat(userW);
        kcal = (float) (mile * mPerC *  0.00006213);
        mDayKcal.setText(kcal+"");
        todayEditor.putString("1", count);
        todayEditor.commit();
        //stepCount = Integer.parseInt(count);
    }


    public static MovingDayFragment getInstance() {
        return mInstance;
    }

    private void showPermissionAlarmDialog() {
      /*  if (isFinishing()) {
            return;
        }
*/
        AlertDialog.Builder alert = new AlertDialog.Builder(MovingDayFragment.this.getActivity());
        alert.setTitle("Notice");
        alert.setMessage("All permissions should be acquired");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        mConnError = error;
        String message = "Connection with S Health is not available";

        if (mConnError.hasResolution()) {
            switch (error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install S Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade S Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable S Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with S Health policy";
                    break;
                default:
                    message = "Please make S Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnError.hasResolution()) {
                    mConnError.resolve(mInstance.getActivity());
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(1, MENU_ITEM_PERMISSION_SETTING, 0, "Connect to S Health");

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        if (item.getItemId() == (MENU_ITEM_PERMISSION_SETTING)) {
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
            try {
                // Show user permission UI for allowing user to change options
                pmsManager.requestPermissions(mKeySet, MovingDayFragment.this.getActivity()).setResultListener(mPermissionListener);
            } catch (Exception e) {
                Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(APP_TAG, "Permission setting fails.");
            }
        }

        return true;
    }


}
