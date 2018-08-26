package com.hg4.oopalgorithm.oopalgorithm;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.util.Log;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.AlarmConfiguration;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.ApplicationRegion;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.NonActionableConfiguration;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingNative;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingOutputs;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingResult;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingException;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.GlucoseValue;
import com.no.bjorninge.librestate.LibreState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class AlgorithmRunner {

    static public OOPResults RunAlgorithm(long timestamp, Context context, byte[] packet, boolean usedefaultstatealways, String sensorid) {
        byte oldState[];

        DataProcessingNative data_processing_native= new DataProcessingNative(1095774808 /*DataProcessingType.APOLLO_PG2*/);

        MyContextWrapper my_context_wrapper = new MyContextWrapper(context);

        data_processing_native.initialize(my_context_wrapper);
        byte[] bDat = {(byte)0xdf, 0x00, 0x00, 0x01, 01, 02};
        boolean bret = data_processing_native.isPatchSupported(bDat , ApplicationRegion.LEVEL_1);

        Log.e(TAG,"data_processing_native.isPatchSupported11 returned " + bret);
        if(!bret) {
            Log.e(TAG,"gson:");
            return new OOPResults(timestamp,-1, 0, null);
        }

        AlarmConfiguration alarm_configuration = new AlarmConfiguration(70, 180);
        NonActionableConfiguration non_actionable_configuration = new NonActionableConfiguration (true, true, 0, 40, 500, -2, 2);

        int sensorStartTimestamp = 0x0e181349;
        int sensorScanTimestamp = 0x0e1c4794;
        int currentUtcOffset = 0x0036ee80;
        if(usedefaultstatealways) {
            Log.e(TAG, "dabear: using default oldstate");
            oldState = LibreState.getDefaultState();
        } else {
            Log.e(TAG, "dabear:  getting state from persistent storage:");
            oldState = LibreState.getStateForSensor(sensorid, context);
        }



        Log.e(TAG, "dabear: oldstate is now :" + Arrays.toString(oldState));


        DataProcessingOutputs data_processing_outputs = null;
        try {

            data_processing_outputs = data_processing_native.processScan(alarm_configuration, non_actionable_configuration, packet, sensorStartTimestamp, sensorScanTimestamp, currentUtcOffset, oldState);

        } catch (DataProcessingException e) {
            Log.e(TAG,"cought DataProcessingException on data_processing_native.processScan ", e);

            if(e.getResult() == DataProcessingResult.FATAL_ERROR_BAD_ARGUMENTS) {
                Log.e(TAG,"Exception is FATAL_ERROR_BAD_ARGUMENTS reseting state");
                LibreState.getAndSaveDefaultStateForSensor("-NA-", context);
            }
            return new OOPResults(timestamp, -2, 0, null);
        } catch (Exception e) {
            Log.e(TAG,"cought exception on data_processing_native.processScan ", e);
            return new OOPResults(timestamp, -3, 0, null);
        }
        Log.e(TAG,"data_processing_native.processScan returned successfully " + data_processing_outputs);
        if(data_processing_outputs == null) {
            Log.e(TAG,"data_processing_native.processScan returned null");
            Log.e(TAG,"gson:");
            return new OOPResults(timestamp,-3, 0, null);
        }
        Log.e(TAG,"data_processing_native.processScan returned successfully " + data_processing_outputs.getAlgorithmResults().getRealTimeGlucose().getValue());

        byte[] newState = data_processing_outputs.getNewState();

        if(sensorid != null) {
            LibreState.saveSensorState(sensorid, newState, context);
        }

        OOPResults OOPResults = new OOPResults(timestamp,  data_processing_outputs.getAlgorithmResults().getRealTimeGlucose().getValue(),
                data_processing_outputs.getAlgorithmResults().getRealTimeGlucose().getId(),
                                                data_processing_outputs.getAlgorithmResults().getTrendArrow());

        if (data_processing_outputs.getAlgorithmResults().getHistoricGlucose() != null) {
            for(GlucoseValue glucoseValue : data_processing_outputs.getAlgorithmResults().getHistoricGlucose()) {
                Log.e(TAG, "  id " + glucoseValue.getId() + " value " + glucoseValue.getValue() + " quality " + glucoseValue.getDataQuality());
            }
            OOPResults.setHistoricBg(data_processing_outputs.getAlgorithmResults().getHistoricGlucose());
        } else {
            Log.e(TAG,"getAlgorithmResults.getHistoricGlucose() returned null");
        }
        Log.e(TAG,"gson:"+OOPResults.toGson());

        return OOPResults;

    }

    static public String getPackageCodePathNoCreate(Context context) {
        return MyContextWrapper.getPackageCodePathNoCreate(context);
    }


    static final  String TAG = "xOOPAlgorithm";

}

class MyContextWrapper extends ContextWrapper {

    Context mBase;
    static final  String TAG = "xOOPAlgorithm";

    MyContextWrapper(Context base) {
        super(base);
        mBase = base;
        Log.e(TAG,"MyContextWrapper.MyContextWrapper() called ");
    }

    static String getPackageCodePathNoCreate(Context context) {
        return context.getFilesDir().getPath() + "base111.apk";
    }

    @Override
    public String getPackageCodePath() {
        Log.e(TAG,"MyContextWrapper.getPackageCodePath() called mBase.getPackageCodePath() = " + mBase.getPackageCodePath());

        // Create the new path
        String originalApkName = getPackageCodePathNoCreate(mBase);
        Log.e(TAG,"MyContextWrapper newpath = " + originalApkName);

        // Check if file already exists
        File f = new File(originalApkName);
        if(f.exists() && !f.isDirectory()) {
            Log.e(TAG,"MyContextWrapper apk exists, returning it = " + originalApkName);
            return originalApkName;
        }

        // Does not exist, we will read it as a resource and write it to new place.
        try {
            Resources res = getResources();
            int id = getResources().getIdentifier("original_apk", "raw", getPackageName());
            InputStream in_s = res.openRawResource(id);
            FileOutputStream out = new FileOutputStream(originalApkName);

            byte[] b = new byte[1024*1024];

            while (true) {
                int readBytes = in_s.read(b);
                if (readBytes < 0) {
                    break;
                }
                out.write(b, 0 ,readBytes);
                Log.e(TAG,"MyContextWrapper succesfully read  = " + readBytes);
            }

            Log.e(TAG,"MyContextWrapper succesfully wrote file  = " + originalApkName);
            out.close();

        } catch (IOException e) {
            Log.e(TAG,"Error: reading resource file", e);
        }

        return originalApkName;
    }

}
