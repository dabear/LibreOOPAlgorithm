package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.util.Log;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.AlarmConfiguration;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.ApplicationRegion;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.NonActionableConfiguration;
import com.hg4.oopalgorithm.oopalgorithm.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class AlgorithmRunner {

    static public int RunAlgorithm(Context context, byte[] packet) {
        DataProcessingNative data_processing_native= new DataProcessingNative(1095774808 /*DataProcessingType.APOLLO_PG2*/);

        MyContextWrapper my_context_wrapper = new MyContextWrapper(context);

        data_processing_native.initialize(my_context_wrapper);
        byte[] bDat = {(byte)0xdf, 0x00, 0x00, 0x01, 01, 02};
        boolean bret = data_processing_native.isPatchSupported(bDat , ApplicationRegion.LEVEL_1);

        Log.e(TAG,"data_processing_native.isPatchSupported11 returned " + bret);





        AlarmConfiguration alarm_configuration = new AlarmConfiguration(70, 180);
        NonActionableConfiguration non_actionable_configuration = new NonActionableConfiguration (true, true, 0, 40, 500, -2, 2);

        int sensorStartTimestamp = 0x0e181349;
        int sensorScanTimestamp = 0x0e1c4794;
        int currentUtcOffset = 0x0036ee80;
        byte[] oldState = {(byte)0xff, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0xff, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};



        DataProcessingOutputs data_processing_outputs;
        try {
            data_processing_outputs = data_processing_native.processScan(alarm_configuration, non_actionable_configuration, packet, sensorStartTimestamp, sensorScanTimestamp, currentUtcOffset, oldState);
        } catch (DataProcessingException e) {
            Log.e(TAG,"cought exception on data_processing_native.processScan ", e);
            return -1;//?????
        }
        Log.e(TAG,"data_processing_native.processScan returned successfully");
        Log.e(TAG,"data_processing_native.processScan returned successfully " + data_processing_outputs.getAlgorithmResults().getRealTimeGlucose().getValue());

        return data_processing_outputs.getAlgorithmResults().getRealTimeGlucose().getValue();

    }


    static final  String TAG = "Xposed";

}

class MyContextWrapper extends ContextWrapper {

    Context mBase;
    static final  String TAG = "Xposed";

    MyContextWrapper(Context base) {
        super(base);
        mBase = base;
        Log.e(TAG,"MyContextWrapper.MyContextWrapper() called ");

    }

    @Override
    public String getPackageCodePath() {
        Log.e(TAG,"MyContextWrapper.getPackageCodePath() called mBase.getPackageCodePath() = " + mBase.getPackageCodePath());

        // Create the new path
        String originalApkName = mBase.getFilesDir().getPath() + "base111.apk";
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

            Log.e(TAG,"MyContextWrapper succesfully wrote file  = ");
            out.close();

        } catch (IOException e) {
            Log.e(TAG,"Error: reading resource file", e);
        }

        return originalApkName;
    }

}
