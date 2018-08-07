package com.hg4.oopalgorithm.oopalgorithm;


import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


    public class AlwaysOnService extends Service {
        public int counter=0;

        static final  String TAG = "xOOPAlgorithm";

        public AlwaysOnService(Context applicationContext) {
            super();
            Log.i(TAG, "AlwaysOnService - constructor");
        }

        public AlwaysOnService() {
            Log.i(TAG, "AlwaysOnService - constructor 2");
        }
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "AlwaysOnService - onStartCommand");
            super.onStartCommand(intent, flags, startId);
            if(MainActivity.getPerfBoolean(this, "UseForegroundService",false)) {
                startForeground(1, new Notification());
            }
            startTimer();
            return START_STICKY;
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(TAG, "Alwayson service - ondestroy! called, will try to restart");
            Intent broadcastIntent = new Intent("com.hg4.oopalgorithm.oopalgorithm.RestartService");
            sendBroadcast(broadcastIntent);
            stoptimertask();
        }

        private Timer timer;
        private TimerTask timerTask;
        long oldTime=0;
        public void startTimer() {
            //set a new Timer
            timer = new Timer();

            //initialize the TimerTask's job
            initializeTimerTask();
            int duration = MainActivity.getPerfInt(this, "TimerDurationSeconds",5*60);
            //schedule the timer, to wake up every 1 second
            Log.i(TAG, "Alwayson service - starting timer every " + duration + " seconds.");
            timer.schedule(timerTask, 1000,  duration * 1000);
        }

        /**
         * it sets the timer to print the counter every x seconds
         */
        public void initializeTimerTask() {
            timerTask = new TimerTask() {
                public void run() {
                    Log.i(TAG, "in timer ++++  "+ (counter++));
                }
            };
        }

        /**
         * not needed
         */
        public void stoptimertask() {
            //stop the timer, if it's not already null
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }



