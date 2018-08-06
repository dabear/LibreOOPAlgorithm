package com.hg4.oopalgorithm.oopalgorithm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.hg4.oopalgorithm.oopalgorithm.MainActivity.StartServiceIfNeeded;

public class AlwaysOnServiceBroadcastReciever  extends BroadcastReceiver {

    static final  String TAG = "xOOPAlgorithm";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "AlwaysOnServiceBroadcastReciever Service Stops, restarting it...");
        StartServiceIfNeeded(context);
    }
}