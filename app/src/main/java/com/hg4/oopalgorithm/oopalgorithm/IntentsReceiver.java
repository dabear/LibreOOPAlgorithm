package com.hg4.oopalgorithm.oopalgorithm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



class Constants {
    static public final String XDRIP_PLUS_LIBRE_DATA = "com.eveningoutpost.dexdrip.LIBRE_DATA";
    static public final String LIBRE_DATA_BUFFER = "com.eveningoutpost.dexdrip.Extras.DATA_BUFFER";
    static public final String LIBRE_DATA_TIMESTAMP = "com.eveningoutpost.dexdrip.Extras.TIMESTAMP";
    static public final String XDRIP_PLUS_NS_EMULATOR = "com.eveningoutpost.dexdrip.NS_EMULATOR";
    static public final String LIBRE_SN = "com.eveningoutpost.dexdrip.Extras.LIBRE_SN";
}

public class IntentsReceiver extends BroadcastReceiver {
    static final  String TAG = "xOOPAlgorithm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"we are inside the broadcast reciever");

        MainActivity.StartServiceIfNeeded(context);

        String sensorid = intent.getStringExtra(Constants.LIBRE_SN);
        String packet_file = intent.getStringExtra("packet");

        long timestamp;

        Log.e(TAG,"packet_file = " + packet_file);
        Log.e(TAG,"dabear: sensorid = " + sensorid);

        byte[] packet;

        if(packet_file != null) {
            packet = Utils.readBinaryFile(packet_file);
        } else {
            packet = intent.getByteArrayExtra(Constants.LIBRE_DATA_BUFFER);
        }
        timestamp = intent.getLongExtra(Constants.LIBRE_DATA_TIMESTAMP, 0);

        Log.e(TAG,"byte packet = " + Utils.byteArrayToHex(packet));
        if(packet == null) {
            Log.i(TAG,"packet is null - returning without sending a data file " + packet );
            return;
        }


        OOPResults oOPResults = AlgorithmRunner.RunAlgorithm(timestamp, context, packet, false, sensorid);
        double sgv = oOPResults.currentBg;
        Log.i(TAG,"RunAlgorithm returned " + sgv);
        if(sgv > 0) {
            BroadcastBack(context, oOPResults, timestamp);
        }

    }

    void BroadcastBack(Context context, OOPResults oOPResults, long timestamp) {
        // Broadcast the data back to xDrip.
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", "sgv");
            jo.put("sgv", oOPResults.currentBg);
            jo.put("date", timestamp);
        }catch (JSONException e) {
            Log.e(TAG,"JSONException: Exception cought in jo.put " + e);
            return;
        }

        JSONArray ja = new JSONArray();
        ja.put(jo);
        try {
            OOPResultsContainer OOPResultsContainer = new OOPResultsContainer();
            OOPResultsContainer.oOPResultsArray = new OOPResults[1];
            OOPResultsContainer.oOPResultsArray[0] = oOPResults;
            jo = new JSONObject(OOPResultsContainer.toGson());
        } catch(org.json.JSONException e) {
            Log.e(TAG,"JSONException: Exception cought in jo.put " + e);
            // Since we have a fallback above we continue
        }
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
