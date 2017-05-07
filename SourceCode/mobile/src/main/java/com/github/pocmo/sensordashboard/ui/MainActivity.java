package com.github.pocmo.sensordashboard.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.pocmo.sensordashboard.EatingActivity;
import com.github.pocmo.sensordashboard.R;
import com.github.pocmo.sensordashboard.ui.eating.EatingTabFragment;
import com.github.pocmo.sensordashboard.ui.moving.DayTask;
import com.github.pocmo.sensordashboard.ui.moving.MovingDayFragment;
import com.github.pocmo.sensordashboard.ui.moving.MovingTabFragment;
import com.github.pocmo.sensordashboard.ui.gbang.GbangTabFragment;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerAlarm();
            /*floating button - eating
            * */
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EatingActivity.class);
                startActivity(intent);
            }
        });

        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the DayFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new DayFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                if (menuItem.getItemId() == R.id.nav_id_home) {
                    FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                    fragmentTransaction1.replace(R.id.containerView, new DayFragment()).commit();

                }


                if (menuItem.getItemId() == R.id.nav_id_eating) {
                    FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
                    fragmentTransaction1.replace(R.id.containerView, new EatingTabFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_id_moving) {
                    FragmentTransaction fragmentTransaction2 = mFragmentManager.beginTransaction();
                    fragmentTransaction2.replace(R.id.containerView, new MovingTabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_id_gbang) {
                    FragmentTransaction fragmentTransaction3 = mFragmentManager.beginTransaction();
                    fragmentTransaction3.replace(R.id.containerView, new GbangTabFragment()).commit();
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();


    }

    public void registerAlarm()
    {
        Log.e("###", "registerAlarm");

        Intent intent = new Intent(this, DayTask.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        try
        {
            // 밤 11시 50분에 처음 시작해서, 24시간 마다 실행되게
            Date start = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-11-28 23:50:00");
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.RTC, start.getTime(), 24 * 60 * 60 * 1000, sender);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }


    }

}