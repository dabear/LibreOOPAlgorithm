package com.no.bjorninge.librestate;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;

public class LibreState {
    private static final String TAG = "xOOPAlgorithm state";
    private static String SAVED_STATE =  "savedstate";
    private static String SAVED_SENDOR_ID = "savedstatesensorid";

    private static byte[] defaultState = {(byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static byte[] getDefaultState(){
        return Arrays.copyOf(defaultState, defaultState.length);
    }

    public static byte[] getAndSaveDefaultStateForSensor(String sensorid, Context context){
        byte[] newstate1 = getDefaultState();
        saveSensorState(sensorid, newstate1, context);
        return newstate1;
    }

    public static void saveSensorState(String sensorid, byte[] state, Context context) {

        if (sensorid == null) {
            Log.e(TAG, "dabear: tried to save sensorstate, but sensorid was null! ");
            return;
        }
        if (state == null) {
            Log.e(TAG, "dabear: tried to save sensorstate, but state was null! ");
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(
                TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        edit.clear();
        edit.putString(SAVED_STATE, Base64.encodeToString(state, Base64.NO_WRAP));
        edit.putString(SAVED_SENDOR_ID, sensorid);

        // we really want this to be sync
        // as we depend on these preferences for the next calls to algorunner
        edit.commit();

        Log.e(TAG, "dabear: saved newState for sensorid " + sensorid + ": " + Arrays.toString(state));

    }


    public static byte[] getStateForSensor(String sensorid, Context context) {

        if(sensorid == null) {
            Log.e(TAG,"dabear: shortcutting gettingstate, as sensorid was null" );
            return getDefaultState();
        }

        SharedPreferences prefs = context.getSharedPreferences(
                TAG, Context.MODE_PRIVATE);
        String savedstate = prefs.getString(SAVED_STATE, "-NA-");
        String savedstatesensorid = prefs.getString(SAVED_SENDOR_ID, "-NA-");



        if(savedstate.equals("-NA-") || savedstatesensorid.equals("-NA-")) {
            Log.e(TAG,"dabear: returning defaultstate to caller, we did not have sensordata stored on disk" );

            return getAndSaveDefaultStateForSensor(sensorid, context);
        }

        if(!savedstatesensorid.equals(sensorid)) {
            Log.e(TAG,"dabear: returning defaultstate to caller, new sensorid detected: " + sensorid );

            return getAndSaveDefaultStateForSensor(sensorid, context);
        }

        byte[] decoded = getDefaultState();

        try{
            decoded = Base64.decode(savedstate, Base64.DEFAULT);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG,"dabear: could not decode sensorstate, returning defaultstate to caller" );
            return getDefaultState();
        }
        return decoded;

    }

}
