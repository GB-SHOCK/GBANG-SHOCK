package com.github.pocmo.sensordashboard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.github.pocmo.sensordashboard.R;
import com.github.pocmo.sensordashboard.user.LoginActivity;

public class SplashActivity extends Activity {
    private static String TAG = SplashActivity.class.getName();
    private static long SLEEP_TIME = 5; // Sleep for some time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Removes title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
// Removes notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
// Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }
    private class IntentLauncher extends Thread {
        @Override
/**
 * Sleep for some time and than start new activity.
 */
        public void run() {
            try {
// Sleeping
                Thread.sleep(SLEEP_TIME*500);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
// Start main activity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
        }
    }
}
