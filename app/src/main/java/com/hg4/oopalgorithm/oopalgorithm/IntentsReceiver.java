package com.hg4.oopalgorithm.oopalgorithm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.AlgorithmRunner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

class Constants {
    static public final String XDRIP_PLUS_LIBRE_DATA = "com.eveningoutpost.dexdrip.LIBRE_DATA";
    static public final String LIBRE_DATA_BUFFER = "com.eveningoutpost.dexdrip.Extras.DATA_BUFFER";
    static public final String LIBRE_DATA_TIMESTAMP = "com.eveningoutpost.dexdrip.Extras.TIMESTAMP";
    static public final String XDRIP_PLUS_NS_EMULATOR = "com.eveningoutpost.dexdrip.NS_EMULATOR";
}

public class IntentsReceiver extends BroadcastReceiver {
    static final  String TAG = "OOPAlgorithm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"we are inside the broadcast reciever");

        String packet_file = intent.getStringExtra("packet");
        String old_state_file = intent.getStringExtra("old_state");//"/data/local/tmp/new_state_20171002_165943.dat";
        long timestamp;

        Log.e(TAG,"packet_file = " + packet_file);
        Log.e(TAG,"old_state_file = " + old_state_file);
        byte[] packet;
        byte[] oldState = {(byte)0xff, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0xff, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};;
        if(packet_file != null) {
            packet = Utils.readBinaryFile(packet_file);
        } else {
            packet = intent.getByteArrayExtra(Constants.LIBRE_DATA_BUFFER);
        }
        timestamp = intent.getLongExtra(Constants.LIBRE_DATA_TIMESTAMP, 0);

        Log.e(TAG,"byte packet = " + Utils.byteArrayToHex(packet));

        if(old_state_file != null) {
            oldState = Utils.readBinaryFile(old_state_file);
        }
        Log.i(TAG,"byte oldState = " + Utils.byteArrayToHex(oldState));

        if(packet == null || oldState == null) {
            Log.i(TAG,"packet or oldState are null - returning without sending a data file " + packet + oldState);
            return;
        }


        int sgv = AlgorithmRunner.RunAlgorithm(context, packet, oldState);
        Log.i(TAG,"RunAlgorithm returned " + sgv);
        if(sgv > 0) {
            BroadcastBack(context, sgv, timestamp);
        }

    }

    void BroadcastBack(Context context, int sgv, long timestamp) {
        // Broadcast the data back to xDrip.
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", "sgv");
            jo.put("sgv", sgv);
            jo.put("date", timestamp);
        }catch (JSONException e) {
            Log.i(TAG,"JSONException: Exception cought in jo.put " + e);
        }

        JSONArray ja = new JSONArray();
        ja.put(jo);

        Intent intent = new Intent(Constants.XDRIP_PLUS_NS_EMULATOR);
        Bundle bundle = new Bundle();
        bundle.putString("collection", "entries");
        bundle.putString("data", ja.toString());
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(intent);
    }

}
