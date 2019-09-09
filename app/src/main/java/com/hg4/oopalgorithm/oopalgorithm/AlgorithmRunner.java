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
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingType;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.GlucoseValue;
import com.no.bjorninge.librestate.LibreState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.AttenuationConfiguration;

public class AlgorithmRunner {

    static public OOPResults RunAlgorithm(long timestamp, Context context, byte[] packet){

        //inferred from l1 sensor test
        byte[] patchUid = { (byte) 0xDF, 0x00, 0x00, 0x04, 0x00, (byte)0xa0};
        byte[] patchInfo = {(byte)0xe0, 0x07, (byte)0xa0, 0x00, 0x04, 0x3d, 0x7f, 0x7b};

        String sensorid = "anid";
        return RunAlgorithm(timestamp, context, packet, patchUid, patchInfo, true, sensorid);

    }
    static public OOPResults RunAlgorithm(long timestamp, Context context, byte[] packet,  byte[] patchUid, byte[] patchInfo, boolean usedefaultstatealways, String sensorid) {
        DataProcessingNative data_processing_native= new DataProcessingNative(1095774808 /*DataProcessingType.APOLLO_PG2*/);

        MyContextWrapper my_context_wrapper = new MyContextWrapper(context);

        data_processing_native.initialize(my_context_wrapper);
        // us data
        byte[] bDat = {(byte)0xe5, 0x00, 0x03, 0x02, (byte)0xe0, 0x12};

        boolean bret = data_processing_native.isPatchSupported(bDat , ApplicationRegion.LEVEL_2); //LEVEL 2 is us, level_4 is IL
        Log.e(TAG,"data_processing_native.isPatchSupported11 returned " + bret);
        if(!bret) {
            Log.e(TAG,"gson:");
            return new OOPResults(timestamp,-1, 0, null);
        }

        AlarmConfiguration alarm_configuration = new AlarmConfiguration(70, 240);
        NonActionableConfiguration non_actionable_configuration = new NonActionableConfiguration (false, true, 0, 70, 500, -2, 2); //??? First should be true???

        
        
        int sensorStartTimestamp = 303288019;
        int sensorScanTimestamp = 303604158;//sensorStartTimestamp + 3600*10 - did not change anything;
        int currentUtcOffset = 10800000;
        if(usedefaultstatealways) {
            Log.e(TAG, "dabear: using default oldstate");
            //oldState = LibreState.getDefaultState();
        } else {
            Log.e(TAG, "dabear:  getting state from persistent storage:");
            //oldState = LibreState.getStateForSensor(sensorid, context);

        }



        //Log.e(TAG, "dabear: oldstate is now :" + Arrays.toString(oldState));


        
        DataProcessingOutputs data_processing_outputs = null;
        
        final int ATTENUATION_MINIMUM_ID_TO_ENABLE = 20160;
        AttenuationConfiguration attenuationConfiguration = new AttenuationConfiguration(ATTENUATION_MINIMUM_ID_TO_ENABLE, false, false, false, false); // Here they are all different ???
        //byte[] patchInfo = {(byte)0xdf, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x94, (byte)0x03};//this.rf.getPatchInfo(tag);// il
        
        
        byte[] compositeState = null;// some us version {(byte)0x1f, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x4a, (byte)0x2a, (byte)0xcb, (byte)0xf3, (byte)0xa0, (byte)0x52, (byte)0x5b, (byte)0x40, (byte)0x1f, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x76, (byte)0xc4, (byte)0xf6, (byte)0x4b, (byte)0x66, (byte)0x59, (byte)0xc7, (byte)0xbf };
        byte[] attenuationState = null;// some us version{(byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7f, (byte)0x7e, (byte)0x73, (byte)0x79, (byte)0x70, (byte)0x6b, (byte)0x68, (byte)0x65, (byte)0x63, (byte)0x5f, (byte)0x5b, (byte)0x5a, (byte)0x57, (byte)0x55, (byte)0x55, (byte)0x54, (byte)0x56, (byte)0x58, (byte)0x59, (byte)0x5d, (byte)0x5d, (byte)0x5c, (byte)0x5e, (byte)0x55, (byte)0x51, (byte)0x55, (byte)0x62, (byte)0x66, (byte)0x75, (byte)0x7c, (byte)0x7e, (byte)0x7d, (byte)0x7e, (byte)0x7f, (byte)0x7e, (byte)0x7e, (byte)0x74, (byte)0x6c, (byte)0x63, (byte)0x5a, (byte)0x52, (byte)0x49, (byte)0x48, (byte)0x51, (byte)0x5f, (byte)0x6b, (byte)0x74, (byte)0x7b, (byte)0x7e, (byte)0x7e, (byte)0x7e, (byte)0x7e, (byte)0x7e, (byte)0x7e, (byte)0x7e, (byte)0x7a, (byte)0x73, (byte)0x6f, (byte)0x19, (byte)0x23, (byte)0x0c, (byte)0x41, (byte)0x40, (byte)0x5c, (byte)0x75, (byte)0x78, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
        
        
        try {
            data_processing_outputs = data_processing_native.processScan(alarm_configuration, non_actionable_configuration, attenuationConfiguration, patchUid, patchInfo,  packet, sensorStartTimestamp, sensorScanTimestamp,
            		60, 20160 /* PatchTimeValues getPatchTimeValues */,  currentUtcOffset, compositeState , attenuationState );

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
        Log.e(TAG,"data_processing_native.processScan returned successfully bg = " + 
           data_processing_outputs.getAlgorithmResults().getRealTimeGlucose().getValue()+ " id = " +
           data_processing_outputs.getAlgorithmResults().getRealTimeGlucose().getId());

        byte[] newState = null; //???data_processing_outputs.getNewState();

        if(sensorid != null) {
            LibreState.saveSensorState(sensorid, newState, context);
        }

        //byte[] newState = data_processing_outputs.
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
