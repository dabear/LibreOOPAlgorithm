package com.hg4.oopalgorithm.oopalgorithm;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;


import java.io.File;

public class MainActivity extends AppCompatActivity {

    static final  String TAG = "xOOPAlgorithm";
    Intent mServiceIntent;
    private AlwaysOnService mAlwaysOnService;
    Button stop_service_button;

    void SetVersion() {
        TextView version = (TextView) findViewById(R.id.version);
        String versionName;
        try {
            versionName = "version " + getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA).versionName;
            versionName += "\nBuilt on: "+BuildConfig.buildVersion;
            version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
            Log.e(TAG,"PackageManager.NameNotFoundException:" + e.getMessage());
        }
    }

    static private boolean DoesForegroundMatch (Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                boolean ret =  service.foreground == getPerfBoolean(ctx, "UseForegroundService",false);
                Log.i (TAG, "DoesForegroundMatch my service found returning " + ret);
                return ret;

            }
        }
        Log.i (TAG, "DoesForegroundMatch not found - returning false");
        return false;
    }

    static private boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i (TAG, "isMyServiceRunning - true");
                return true;
            }
        }
        Log.i (TAG, "isMyServiceRunning - false");
        return false;
    }

    public static void savePerfBoolean(Context ctx, String valueKey, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    static boolean getPerfBoolean(Context ctx, String key, boolean valueDefault) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(key, valueDefault);

    }

    public static void savePerfInt(Context ctx, String valueKey, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(valueKey, value);
        edit.commit();
    }

    static int getPerfInt(Context ctx, String key, int valueDefault) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getInt(key, valueDefault);

    }


    static private void StopService(Context ctx, boolean isMyServiceRunning) {
        if(!isMyServiceRunning) {
            return;
        }
        AlwaysOnService AlwaysOnService = new AlwaysOnService(ctx);
        Intent ServiceIntent = new Intent(ctx, AlwaysOnService.getClass());
        ctx.stopService(ServiceIntent);
    }

    static public void StartServiceIfNeeded(Context ctx) {
        boolean isMyServiceRunning = isMyServiceRunning(ctx, new AlwaysOnService().getClass());
        boolean RunService = getPerfBoolean(ctx, "RunService", true);

        // If we are running, and in the forground is also right, do nothing
        if(RunService && isMyServiceRunning) {
            // We are running, is it in forground?
            if(DoesForegroundMatch(ctx, new AlwaysOnService().getClass())) {
                Log.e(TAG, "We are running, all is ok, return");
                return;
            }
        }
        // We always stop the service, in order to make sure we create it in foreground
        // as needed.
        StopService(ctx, isMyServiceRunning);

        if(!RunService) {
            // Service was already stopped, so nothing to do
            return;
        }

        Log.e(TAG,"StartService called isMyServiceRunning = " + isMyServiceRunning + " RunService.ischecked = " + RunService);
        AlwaysOnService AlwaysOnService = new AlwaysOnService(ctx);
        Intent ServiceIntent = new Intent(ctx, AlwaysOnService.getClass());
        ctx.startService(ServiceIntent);
    }

    public void addListenerOnRunService(int id, final String key) {
        CheckBox chkIos = findViewById(id );
        chkIos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                savePerfBoolean(getApplicationContext(), key, ((CheckBox) v).isChecked());
                StartServiceIfNeeded(getApplicationContext());
            }
        });
    }

    private void SetRadioButon() {
        int duration = MainActivity.getPerfInt(this, "TimerDurationSeconds",5*60);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        switch(duration) {
            case 1:
                radioGroup.check(R.id.radio_1_sec);
                break;
            case 5*60:
                radioGroup.check(R.id.radio_5_minutes);
                break;
            case 60 * 60:
                radioGroup.check(R.id.radio_1_hour);
                break;
            case 365 * 24 *60 * 60:
                radioGroup.check(R.id.radio_1_year);
                break;
            default:
                Log.e(TAG, "Error wrong duration" + duration);
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_1_sec:
                if (checked) {
                    Log.i(TAG, "radio_1_sec checked = " + checked);
                    savePerfInt(getApplicationContext(), "TimerDurationSeconds",1);
                    break;
                }
            case R.id.radio_5_minutes:
                if (checked) {
                    Log.i(TAG, "radio_5_minutes checked = " + checked);
                    savePerfInt(getApplicationContext(), "TimerDurationSeconds",300);
                    break;
                }
            case R.id.radio_1_hour:
                if (checked) {
                    Log.i(TAG, "radio_1_hour checked = " + checked);
                    savePerfInt(getApplicationContext(), "TimerDurationSeconds",60*60);
                    break;
                }
            case R.id.radio_1_year:
                if (checked) {
                    Log.i(TAG, "radio_1_year checked = " + checked);
                    savePerfInt(getApplicationContext(), "TimerDurationSeconds",365 * 24 *60*60);
                    break;
                }
        }
        boolean isMyServiceRunning = isMyServiceRunning(getApplicationContext(), new AlwaysOnService().getClass());
        StopService(getApplicationContext(),isMyServiceRunning);
        StartServiceIfNeeded(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SetVersion();
        SetRadioButon();
/*
        stop_service_button = (Button) findViewById(R.id.stop_service);
        stop_service_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mServiceIntent = new Intent(getApplicationContext(), mAlwaysOnService.getClass());
                if (isMyServiceRunning(getApplicationContext(), mAlwaysOnService.getClass())) {
                    stopService(mServiceIntent);
                }
            }
        });
*/

        CheckBox RunService = findViewById(R.id.RunService);
        RunService.setChecked(getPerfBoolean(getApplicationContext(),"RunService",true));
        CheckBox UseForegroundService = findViewById(R.id.UseForegroundService);
        UseForegroundService.setChecked(getPerfBoolean(getApplicationContext(),"UseForegroundService",false));

        addListenerOnRunService(R.id.RunService,"RunService");
        addListenerOnRunService(R.id.UseForegroundService,"UseForegroundService");


        StartServiceIfNeeded(this);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
        */
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);

        byte[] packet = {(byte)0x3a, (byte)0xcf, (byte)0x10, (byte)0x16, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x4f, (byte)0x11, (byte)0x08, (byte)0x10, (byte)0xad, (byte)0x02, (byte)0xc8, (byte)0xd4,
                (byte)0x5b, (byte)0x00, (byte)0xaa, (byte)0x02, (byte)0xc8, (byte)0xb4, (byte)0x1b, (byte)0x80,
                (byte)0xa9, (byte)0x02, (byte)0xc8, (byte)0x9c, (byte)0x5b, (byte)0x00, (byte)0xa9, (byte)0x02,
                (byte)0xc8, (byte)0x8c, (byte)0x1b, (byte)0x80, (byte)0xb0, (byte)0x02, (byte)0xc8, (byte)0x30,
                (byte)0x5c, (byte)0x80, (byte)0xb0, (byte)0x02, (byte)0x88, (byte)0xe6, (byte)0x9c, (byte)0x80,
                (byte)0xb8, (byte)0x02, (byte)0xc8, (byte)0x3c, (byte)0x9d, (byte)0x80, (byte)0xb8, (byte)0x02,
                (byte)0xc8, (byte)0x60, (byte)0x9d, (byte)0x80, (byte)0xa1, (byte)0x02, (byte)0xc8, (byte)0xdc,
                (byte)0x9e, (byte)0x80, (byte)0xab, (byte)0x02, (byte)0xc8, (byte)0x14, (byte)0x9e, (byte)0x80,
                (byte)0xa9, (byte)0x02, (byte)0xc8, (byte)0xc0, (byte)0x9d, (byte)0x80, (byte)0xab, (byte)0x02,
                (byte)0xc8, (byte)0x78, (byte)0x9d, (byte)0x80, (byte)0xaa, (byte)0x02, (byte)0xc8, (byte)0x40,
                (byte)0x9d, (byte)0x80, (byte)0xa8, (byte)0x02, (byte)0xc8, (byte)0x08, (byte)0x9d, (byte)0x80,
                (byte)0xa8, (byte)0x02, (byte)0xc8, (byte)0x2c, (byte)0x5c, (byte)0x80, (byte)0xad, (byte)0x02,
                (byte)0xc8, (byte)0xf8, (byte)0x5b, (byte)0x00, (byte)0x29, (byte)0x06, (byte)0xc8, (byte)0xf4,
                (byte)0x9b, (byte)0x80, (byte)0xc9, (byte)0x05, (byte)0xc8, (byte)0x8c, (byte)0xde, (byte)0x80,
                (byte)0xc3, (byte)0x05, (byte)0xc8, (byte)0x28, (byte)0x9e, (byte)0x80, (byte)0x2c, (byte)0x06,
                (byte)0xc8, (byte)0xd0, (byte)0x9e, (byte)0x80, (byte)0x7b, (byte)0x06, (byte)0x88, (byte)0xa6,
                (byte)0x9e, (byte)0x80, (byte)0xf9, (byte)0x05, (byte)0xc8, (byte)0xb0, (byte)0x9e, (byte)0x80,
                (byte)0x99, (byte)0x05, (byte)0xc8, (byte)0xf0, (byte)0x9e, (byte)0x80, (byte)0x2e, (byte)0x05,
                (byte)0xc8, (byte)0x00, (byte)0x9f, (byte)0x80, (byte)0x81, (byte)0x04, (byte)0xc8, (byte)0x48,
                (byte)0xa0, (byte)0x80, (byte)0x5d, (byte)0x04, (byte)0xc8, (byte)0x38, (byte)0x9d, (byte)0x80,
                (byte)0x12, (byte)0x04, (byte)0xc8, (byte)0x10, (byte)0x9e, (byte)0x80, (byte)0xcf, (byte)0x03,
                (byte)0xc8, (byte)0x4c, (byte)0x9e, (byte)0x80, (byte)0x6f, (byte)0x03, (byte)0xc8, (byte)0xb8,
                (byte)0x9e, (byte)0x80, (byte)0x19, (byte)0x03, (byte)0xc8, (byte)0x40, (byte)0x9f, (byte)0x80,
                (byte)0xc5, (byte)0x02, (byte)0xc8, (byte)0xf4, (byte)0x9e, (byte)0x80, (byte)0xaa, (byte)0x02,
                (byte)0xc8, (byte)0xf8, (byte)0x5b, (byte)0x00, (byte)0xa2, (byte)0x04, (byte)0xc8, (byte)0x38,
                (byte)0x9a, (byte)0x00, (byte)0xd1, (byte)0x04, (byte)0xc8, (byte)0x28, (byte)0x9b, (byte)0x80,
                (byte)0xe4, (byte)0x04, (byte)0xc8, (byte)0xe0, (byte)0x1a, (byte)0x80, (byte)0x8f, (byte)0x04,
                (byte)0xc8, (byte)0x20, (byte)0x9b, (byte)0x80, (byte)0x22, (byte)0x06, (byte)0xc8, (byte)0x50,
                (byte)0x5b, (byte)0x80, (byte)0xbc, (byte)0x06, (byte)0xc8, (byte)0x54, (byte)0x9c, (byte)0x80,
                (byte)0x7f, (byte)0x05, (byte)0xc8, (byte)0x24, (byte)0x5c, (byte)0x80, (byte)0xc9, (byte)0x05,
                (byte)0xc8, (byte)0x38, (byte)0x5c, (byte)0x80, (byte)0x38, (byte)0x05, (byte)0xc8, (byte)0xf4,
                (byte)0x1a, (byte)0x80, (byte)0x37, (byte)0x07, (byte)0xc8, (byte)0x84, (byte)0x5b, (byte)0x80,
                (byte)0xfb, (byte)0x08, (byte)0xc8, (byte)0x4c, (byte)0x9c, (byte)0x80, (byte)0xfb, (byte)0x09,
                (byte)0xc8, (byte)0x7c, (byte)0x9b, (byte)0x80, (byte)0x77, (byte)0x0a, (byte)0xc8, (byte)0xe4,
                (byte)0x5a, (byte)0x80, (byte)0xdf, (byte)0x09, (byte)0xc8, (byte)0x88, (byte)0x9f, (byte)0x80,
                (byte)0x6d, (byte)0x08, (byte)0xc8, (byte)0x2c, (byte)0x9f, (byte)0x80, (byte)0xc3, (byte)0x06,
                (byte)0xc8, (byte)0xb0, (byte)0x9d, (byte)0x80, (byte)0xd9, (byte)0x11, (byte)0x00, (byte)0x00,
                (byte)0x72, (byte)0xc2, (byte)0x00, (byte)0x08, (byte)0x82, (byte)0x05, (byte)0x09, (byte)0x51,
                (byte)0x14, (byte)0x07, (byte)0x96, (byte)0x80, (byte)0x5a, (byte)0x00, (byte)0xed, (byte)0xa6,
                (byte)0x0e, (byte)0x6e, (byte)0x1a, (byte)0xc8, (byte)0x04, (byte)0xdd, (byte)0x58, (byte)0x6d};


        byte[] us1_packet = { (byte)0x89, (byte)0xef, (byte)0x9a, (byte)0x47, (byte)0x20, (byte)0x8a, (byte)0xb3, (byte)0x09, (byte)0x49, (byte)0x24, (byte)0xf5, (byte)0x89, (byte)0x52, (byte)0xec, (byte)0x9e, (byte)0x59, (byte)0xa3, (byte)0x6c, (byte)0xe4, (byte)0xd5, (byte)0xd0, (byte)0x67, (byte)0x1d, (byte)0x3c, (byte)0x24, (byte)0xb2, (byte)0xf2, (byte)0xcb, (byte)0x9d, (byte)0x99, (byte)0x45, (byte)0xed, (byte)0x66, (byte)0x22, (byte)0x8a, (byte)0xf9, (byte)0x87, (byte)0x7f, (byte)0xa1, (byte)0x29, (byte)0x89, (byte)0x3b, (byte)0xf0, (byte)0x1b, (byte)0xa4, (byte)0x49, (byte)0x1a, (byte)0x7c, (byte)0xda, (byte)0xe5, (byte)0x41, (byte)0x09, (byte)0xcd, (byte)0x9d, (byte)0xf8, (byte)0xfc, (byte)0xf6, (byte)0xd5, (byte)0x66, (byte)0xdb, (byte)0x7c, (byte)0xda, (byte)0x87, (byte)0x84, (byte)0xd6, (byte)0xa5, (byte)0x8a, (byte)0x1d, (byte)0xcb, (byte)0x82, (byte)0x4f, (byte)0x1a, (byte)0x63, (byte)0x84, (byte)0x67, (byte)0xfa, (byte)0xd8, (byte)0xe0, (byte)0x53, (byte)0x76, (byte)0xdb, (byte)0xf4, (byte)0x1e, (byte)0xa2, (byte)0x6a, (byte)0x53, (byte)0x82, (byte)0x2b, (byte)0xd5, (byte)0xec, (byte)0x9b, (byte)0x3d, (byte)0x49, (byte)0x09, (byte)0xc4, (byte)0x7f, (byte)0xe5, (byte)0x99, (byte)0xb3, (byte)0x51, (byte)0xe2, (byte)0xbe, (byte)0x4b, (byte)0xbb, (byte)0xc9, (byte)0xcd, (byte)0x6f, (byte)0x82, (byte)0xad, (byte)0xf4, (byte)0x34, (byte)0xaf, (byte)0x35, (byte)0x20, (byte)0xc9, (byte)0xc4, (byte)0x75, (byte)0x4d, (byte)0x54, (byte)0xa7, (byte)0x6a, (byte)0x00, (byte)0x24, (byte)0x0b, (byte)0xd7, (byte)0x2d, (byte)0xa8, (byte)0xfe, (byte)0xbd, (byte)0x35, (byte)0x4d, (byte)0xd5, (byte)0xd6, (byte)0x66, (byte)0x95, (byte)0x6e, (byte)0x33, (byte)0x2c, (byte)0xb6, (byte)0x77, (byte)0xb7, (byte)0x90, (byte)0xa7, (byte)0x3a, (byte)0x39, (byte)0xa5, (byte)0xb8, (byte)0x5b, (byte)0x9c, (byte)0x1f, (byte)0x2b, (byte)0x3b, (byte)0x16, (byte)0x7d, (byte)0xda, (byte)0x88, (byte)0x54, (byte)0x41, (byte)0x16, (byte)0x0b, (byte)0x2a, (byte)0x47, (byte)0x62, (byte)0x14, (byte)0x83, (byte)0xce, (byte)0xe2, (byte)0x8c, (byte)0x2b, (byte)0xc0, (byte)0xcd, (byte)0x7b, (byte)0xdd, (byte)0xab, (byte)0x9d, (byte)0x9f, (byte)0x1c, (byte)0xcb, (byte)0x56, (byte)0x6f, (byte)0x7d, (byte)0x6c, (byte)0xac, (byte)0x72, (byte)0xf0, (byte)0xd2, (byte)0xa8, (byte)0x48, (byte)0x1c, (byte)0xd6, (byte)0x3b, (byte)0x27, (byte)0x8a, (byte)0xa6, (byte)0xdf, (byte)0x4f, (byte)0x84, (byte)0xaf, (byte)0x3a, (byte)0x5c, (byte)0xa4, (byte)0xbe, (byte)0x15, (byte)0x9d, (byte)0x98, (byte)0xb0, (byte)0x46, (byte)0xe8, (byte)0x36, (byte)0xf3, (byte)0x20, (byte)0x78, (byte)0x48, (byte)0x47, (byte)0xc9, (byte)0x88, (byte)0x60, (byte)0x76, (byte)0x8e, (byte)0x13, (byte)0x1d, (byte)0x24, (byte)0xb9, (byte)0xd1, (byte)0x20, (byte)0xd3, (byte)0xf7, (byte)0x36, (byte)0xac, (byte)0x46, (byte)0x5e, (byte)0x59, (byte)0x55, (byte)0xca, (byte)0x5a, (byte)0x48, (byte)0x49, (byte)0xf4, (byte)0x6d, (byte)0x0c, (byte)0x83, (byte)0x2a, (byte)0x9f, (byte)0xbe, (byte)0xc3, (byte)0x60, (byte)0xc1, (byte)0x8c, (byte)0xaf, (byte)0x3a, (byte)0xa8, (byte)0x6c, (byte)0x26, (byte)0x53, (byte)0xbe, (byte)0x54, (byte)0x97, (byte)0x8f, (byte)0x15, (byte)0x5c, (byte)0x1d, (byte)0xa4, (byte)0x08, (byte)0x87, (byte)0xd7, (byte)0xab, (byte)0x74, (byte)0xb2, (byte)0x71, (byte)0xc7, (byte)0xb1, (byte)0x2e, (byte)0x23, (byte)0xdf, (byte)0x5c, (byte)0xe9, (byte)0xbc, (byte)0x45, (byte)0x6e, (byte)0xb6, (byte)0xed, (byte)0xc5, (byte)0x04, (byte)0xcc, (byte)0xd3, (byte)0x2f, (byte)0xc9, (byte)0xe1, (byte)0x51, (byte)0x02, (byte)0x61, (byte)0x18, (byte)0x36, (byte)0x99, (byte)0xa9, (byte)0x63, (byte)0x3f, (byte)0xe6, (byte)0x07, (byte)0xc9, (byte)0x7b, (byte)0xae, (byte)0x94, (byte)0x33, (byte)0xe0, (byte)0x17, (byte)0x62, (byte)0xcb, (byte)0x90, (byte)0x2b, (byte)0xc0, (byte)0x98, (byte)0xc1, (byte)0x51, (byte)0x8d, (byte)0x3a, (byte)0x19, (byte)0x6e, (byte)0x47, (byte)0xce, (byte)0x87, (byte)0x2b, (byte)0x68, (byte)0x52, (byte)0x88, (byte)0x1a, (byte)0xd3, (byte)0xba, (byte)0xba, (byte)0xe3, (byte)0x42, (byte)0x07, (byte)0xa4, (byte)0x75, (byte)0x63, (byte)0x1d, (byte)0x54, (byte)0x26, (byte)0xdf, (byte)0x13, (byte)0x78, (byte)0x7c, (byte)0x95, (byte)0xbd};
        
        byte[] us2_packet = {(byte)0x89, (byte)0xef, (byte)0x9a, (byte)0x47, (byte)0x20, (byte)0x8a, (byte)0xb3, (byte)0x09, (byte)0x49, (byte)0x24, (byte)0xf5, (byte)0x89, (byte)0x52, (byte)0xec, (byte)0x9e, (byte)0x59, (byte)0xa3, (byte)0x6c, (byte)0xe4, (byte)0xd5, (byte)0xd0, (byte)0x67, (byte)0x1d, (byte)0x3c, (byte)0xb3, (byte)0xe6, (byte)0x35, (byte)0xe3, (byte)0x4d, (byte)0xfc, (byte)0xa3, (byte)0x72, (byte)0x71, (byte)0x41, (byte)0x32, (byte)0x89, (byte)0xa2, (byte)0x14, (byte)0xc4, (byte)0x18, (byte)0xdd, (byte)0x82, (byte)0x49, (byte)0xf2, (byte)0x8c, (byte)0x6d, (byte)0x5e, (byte)0x23, (byte)0xcd, (byte)0x32, (byte)0x2f, (byte)0x78, (byte)0x24, (byte)0x2b, (byte)0x9d, (byte)0x51, (byte)0xb4, (byte)0xd3, (byte)0x6a, (byte)0xfd, (byte)0x6d, (byte)0x32, (byte)0x61, (byte)0x7b, (byte)0xd5, (byte)0x8b, (byte)0xb8, (byte)0x37, (byte)0x0c, (byte)0xfa, (byte)0x18, (byte)0x35, (byte)0x50, (byte)0x48, (byte)0x82, (byte)0x6c, (byte)0xf1, (byte)0x0b, (byte)0xcc, (byte)0x1a, (byte)0x67, (byte)0xdb, (byte)0x03, (byte)0x00, (byte)0x6d, (byte)0x3f, (byte)0xfa, (byte)0x04, (byte)0x47, (byte)0x2e, (byte)0x15, (byte)0x98, (byte)0xd4, (byte)0xb6, (byte)0xd0, (byte)0xcc, (byte)0x1f, (byte)0x56, (byte)0x9c, (byte)0xde, (byte)0x6b, (byte)0xd2, (byte)0xf5, (byte)0x76, (byte)0xbf, (byte)0xb1, (byte)0xbc, (byte)0x39, (byte)0x09, (byte)0xd3, (byte)0x6e, (byte)0xbd, (byte)0xf5, (byte)0x87, (byte)0xe6, (byte)0x57, (byte)0xdc, (byte)0x21, (byte)0x20, (byte)0xdb, (byte)0xc5, (byte)0xfe, (byte)0xaa, (byte)0xd2, (byte)0x4a, (byte)0x92, (byte)0x95, (byte)0x4d, (byte)0x92, (byte)0xa6, (byte)0x67, (byte)0xa1, (byte)0x38, (byte)0x64, (byte)0xb0, (byte)0x0b, (byte)0x93, (byte)0x64, (byte)0x4b, (byte)0x37, (byte)0x54, (byte)0x01, (byte)0x65, (byte)0x30, (byte)0x16, (byte)0x36, (byte)0x92, (byte)0x2f, (byte)0x72, (byte)0x1d, (byte)0x0e, (byte)0x5e, (byte)0xc7, (byte)0xc7, (byte)0x0d, (byte)0x79, (byte)0xac, (byte)0x5f, (byte)0xad, (byte)0xbe, (byte)0xae, (byte)0x98, (byte)0x14, (byte)0xb3, (byte)0x4f, (byte)0x03, (byte)0xda, (byte)0xf7, (byte)0x20, (byte)0xc4, (byte)0x6c, (byte)0xe8, (byte)0x1c, (byte)0xf5, (byte)0x42, (byte)0x8b, (byte)0x98, (byte)0x14, (byte)0x20, (byte)0xc8, (byte)0xb1, (byte)0xa1, (byte)0x94, (byte)0x09, (byte)0x21, (byte)0x68, (byte)0x7f, (byte)0xb9, (byte)0xe4, (byte)0xc8, (byte)0x80, (byte)0x92, (byte)0xa5, (byte)0x35, (byte)0xf5, (byte)0x3b, (byte)0x6a, (byte)0xad, (byte)0x1f, (byte)0x39, (byte)0x04, (byte)0xf6, (byte)0xe8, (byte)0xdd, (byte)0x7b, (byte)0x21, (byte)0x84, (byte)0xe2, (byte)0x19, (byte)0x60, (byte)0x0a, (byte)0x0c, (byte)0xa6, (byte)0x45, (byte)0xec, (byte)0xed, (byte)0xcf, (byte)0xb4, (byte)0x00, (byte)0xca, (byte)0x80, (byte)0x9b, (byte)0x84, (byte)0x62, (byte)0xda, (byte)0x74, (byte)0xd8, (byte)0xb9, (byte)0x05, (byte)0x2a, (byte)0xe0, (byte)0x24, (byte)0x20, (byte)0xb6, (byte)0xa2, (byte)0xf3, (byte)0xed, (byte)0x0b, (byte)0x34, (byte)0x1e, (byte)0x79, (byte)0x8d, (byte)0xb0, (byte)0x31, (byte)0x6a, (byte)0x0c, (byte)0x7f, (byte)0xf1, (byte)0x00, (byte)0xf8, (byte)0x26, (byte)0xb5, (byte)0xbb, (byte)0xec, (byte)0x83, (byte)0xe7, (byte)0x2b, (byte)0xa0, (byte)0x27, (byte)0xfe, (byte)0x1a, (byte)0xdc, (byte)0xf6, (byte)0xa9, (byte)0xe4, (byte)0x63, (byte)0x1d, (byte)0x25, (byte)0x7b, (byte)0x2c, (byte)0x2e, (byte)0x6e, (byte)0x9f, (byte)0xf0, (byte)0x6e, (byte)0x4b, (byte)0xbb, (byte)0x3d, (byte)0x16, (byte)0x99, (byte)0xaf, (byte)0xc3, (byte)0xcb, (byte)0xeb, (byte)0xc2, (byte)0x4b, (byte)0x2f, (byte)0x1e, (byte)0x46, (byte)0x61, (byte)0x0f, (byte)0x69, (byte)0x13, (byte)0x2e, (byte)0xcc, (byte)0x52, (byte)0xa7, (byte)0x5e, (byte)0xbe, (byte)0x8c, (byte)0x53, (byte)0x8a, (byte)0x16, (byte)0x6d, (byte)0xf7, (byte)0x74, (byte)0x0c, (byte)0xba, (byte)0xb5, (byte)0x9c, (byte)0xa5, (byte)0xa9, (byte)0x83, (byte)0x57, (byte)0x42, (byte)0x1d, (byte)0x10, (byte)0x0a, (byte)0xa1, (byte)0x31, (byte)0x87, (byte)0x2b, (byte)0x68, (byte)0x52, (byte)0x88, (byte)0x1a, (byte)0xd3, (byte)0xba, (byte)0xba, (byte)0xe3, (byte)0x42, (byte)0x07, (byte)0xa4, (byte)0x75, (byte)0x63, (byte)0x1d, (byte)0x54, (byte)0x26, (byte)0xdf, (byte)0x13, (byte)0x78, (byte)0x7c, (byte)0x95, (byte)0xbd };

        byte[] us1_patchInfo = {(byte)0xe5, 0x00, 0x03, 0x02, (byte)0xe0, 0x12};
        byte[] us2_patchInfo = { (byte)0xe5, 0x00, 0x03, 0x02, (byte)0xf5, (byte)0x08};

      	// byte[] patchUid = {(byte)0x75, (byte)0x84, (byte)0x95, 0x03, 0x00, (byte)0xa0, 0x07, (byte)0xe0}; // Tag.getId() - il
        byte[] us_patchUid = {(byte)0xd5, (byte)0x82, (byte)0x98, 0x04, 0x00, (byte)0xa0, 0x07, (byte)0xe0}; // us


byte []packet1 = {(byte)0x65 ,(byte)0xc5 ,(byte)0xf0 ,(byte)0x14 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x82 ,(byte)0x45
,(byte)0x08 ,(byte)0x14 ,(byte)0x1c ,(byte)0x05 ,(byte)0xc8 ,(byte)0x74 ,(byte)0x99 ,(byte)0x01 ,(byte)0x1d ,(byte)0x05 ,(byte)0xc8 ,(byte)0x74 ,(byte)0x99 ,(byte)0x01 ,(byte)0x1f ,(byte)0x05 ,(byte)0xc8 ,(byte)0x74 ,(byte)0x99 ,(byte)0x01 ,(byte)0x22 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x78 ,(byte)0x99 ,(byte)0x01 
,(byte)0x26 ,(byte)0x05 ,(byte)0x88 ,(byte)0xb2 ,(byte)0x99 ,(byte)0x01 ,(byte)0x2d ,(byte)0x05 ,(byte)0x88 ,(byte)0xbe ,(byte)0x99 ,(byte)0x00 ,(byte)0x31 ,(byte)0x05 ,(byte)0xc8 ,(byte)0xd4 ,(byte)0x99 ,(byte)0x01 ,(byte)0x30 ,(byte)0x05 ,(byte)0x88 ,(byte)0xea ,(byte)0x99 ,(byte)0x01 ,(byte)0xef ,(byte)0x04
,(byte)0xc8 ,(byte)0x30 ,(byte)0x99 ,(byte)0x01 ,(byte)0xf2 ,(byte)0x04 ,(byte)0xc8 ,(byte)0x48 ,(byte)0x99 ,(byte)0x01 ,(byte)0xf7 ,(byte)0x04 ,(byte)0xc8 ,(byte)0x74 ,(byte)0x99 ,(byte)0x01 ,(byte)0x06 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x94 ,(byte)0x99 ,(byte)0x01 ,(byte)0x0e ,(byte)0x05 ,(byte)0xc8 ,(byte)0x90
,(byte)0x99 ,(byte)0x01 ,(byte)0x13 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x88 ,(byte)0x99 ,(byte)0x01 ,(byte)0x13 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x80 ,(byte)0x99 ,(byte)0x01 ,(byte)0x18 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x78 ,(byte)0x99 ,(byte)0x01 ,(byte)0x86 ,(byte)0x06 ,(byte)0xc8 ,(byte)0x3c ,(byte)0x5b ,(byte)0x01
,(byte)0x14 ,(byte)0x07 ,(byte)0xc8 ,(byte)0x68 ,(byte)0x1c ,(byte)0x01 ,(byte)0xc8 ,(byte)0x07 ,(byte)0xc8 ,(byte)0x80 ,(byte)0x5b ,(byte)0x01 ,(byte)0x96 ,(byte)0x06 ,(byte)0xc8 ,(byte)0x1c ,(byte)0x1d ,(byte)0x01 ,(byte)0x43 ,(byte)0x06 ,(byte)0xc8 ,(byte)0xf0 ,(byte)0x1c ,(byte)0x01 ,(byte)0x5b ,(byte)0x07 
,(byte)0xc8 ,(byte)0x80 ,(byte)0x5b ,(byte)0x01 ,(byte)0x97 ,(byte)0x08 ,(byte)0xc8 ,(byte)0x90 ,(byte)0xd9 ,(byte)0x01 ,(byte)0xbe ,(byte)0x07 ,(byte)0xc8 ,(byte)0xe8 ,(byte)0x59 ,(byte)0x01 ,(byte)0xd3 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x88 ,(byte)0x99 ,(byte)0x01 ,(byte)0xfd ,(byte)0x03 ,(byte)0xc8 ,(byte)0x58 
,(byte)0x18 ,(byte)0x02 ,(byte)0xb9 ,(byte)0x02 ,(byte)0xc8 ,(byte)0xd0 ,(byte)0x18 ,(byte)0x02 ,(byte)0x68 ,(byte)0x01 ,(byte)0xc8 ,(byte)0x14 ,(byte)0xd9 ,(byte)0x01 ,(byte)0xf6 ,(byte)0x00 ,(byte)0xc8 ,(byte)0x7c ,(byte)0x18 ,(byte)0x02 ,(byte)0x69 ,(byte)0x01 ,(byte)0xc8 ,(byte)0x10 ,(byte)0xd8 ,(byte)0x01 
,(byte)0xdd ,(byte)0x01 ,(byte)0xc8 ,(byte)0xcc ,(byte)0xd7 ,(byte)0x01 ,(byte)0xcc ,(byte)0x02 ,(byte)0xc8 ,(byte)0xb4 ,(byte)0xd7 ,(byte)0x01 ,(byte)0x75 ,(byte)0x03 ,(byte)0xc8 ,(byte)0xc8 ,(byte)0x17 ,(byte)0x02 ,(byte)0x83 ,(byte)0x03 ,(byte)0xc8 ,(byte)0x00 ,(byte)0xd8 ,(byte)0x01 ,(byte)0xa1 ,(byte)0x04 
,(byte)0xc8 ,(byte)0x30 ,(byte)0xd8 ,(byte)0x01 ,(byte)0x03 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x94 ,(byte)0x99 ,(byte)0x01 ,(byte)0x2e ,(byte)0x06 ,(byte)0xc8 ,(byte)0x48 ,(byte)0x5d ,(byte)0x01 ,(byte)0xc2 ,(byte)0x06 ,(byte)0xc8 ,(byte)0xa4 ,(byte)0x1d ,(byte)0x01 ,(byte)0x3d ,(byte)0x06 ,(byte)0xc8 ,(byte)0xb0 
,(byte)0x1d ,(byte)0x01 ,(byte)0x56 ,(byte)0x05 ,(byte)0xc8 ,(byte)0x24 ,(byte)0x5d ,(byte)0x01 ,(byte)0x86 ,(byte)0x04 ,(byte)0xc8 ,(byte)0xac ,(byte)0x5b ,(byte)0x01 ,(byte)0x4e ,(byte)0x04 ,(byte)0xc8 ,(byte)0xf8 ,(byte)0x5a ,(byte)0x01 ,(byte)0xd4 ,(byte)0x03 ,(byte)0xc8 ,(byte)0xf8 ,(byte)0x9a ,(byte)0x01 
,(byte)0x27 ,(byte)0x03 ,(byte)0xc8 ,(byte)0xf0 ,(byte)0xd9 ,(byte)0x01 ,(byte)0xa3 ,(byte)0x03 ,(byte)0xc8 ,(byte)0x54 ,(byte)0x9a ,(byte)0x01 ,(byte)0xde ,(byte)0x04 ,(byte)0xc8 ,(byte)0xe4 ,(byte)0x99 ,(byte)0x01 ,(byte)0x87 ,(byte)0x04 ,(byte)0xc8 ,(byte)0x00 ,(byte)0x5b ,(byte)0x01 ,(byte)0xc3 ,(byte)0x05 
,(byte)0xc8 ,(byte)0x24 ,(byte)0x5b ,(byte)0x01 ,(byte)0x59 ,(byte)0x1d ,(byte)0x00 ,(byte)0x00 ,(byte)0xf1 ,(byte)0x8a ,(byte)0x00 ,(byte)0x08 ,(byte)0x67 ,(byte)0x08 ,(byte)0x16 ,(byte)0x51 ,(byte)0x14 ,(byte)0x07 ,(byte)0x96 ,(byte)0x80 ,(byte)0x5a ,(byte)0x00 ,(byte)0xed ,(byte)0xa6 ,(byte)0x00 ,(byte)0x4e 
,(byte)0x1a ,(byte)0xc8 ,(byte)0x04 ,(byte)0x86 ,(byte)0x69 ,(byte)0x6e};

        byte []patchUid = {(byte)0xa0, (byte)0x60, (byte)0xbc, (byte)0x03, (byte)0x00, (byte)0xa0, (byte)0x07, (byte)0xe0};
        

        int sgv = (int) AlgorithmRunner.RunAlgorithm(0, getApplicationContext(), us1_packet, us_patchUid, us1_patchInfo, true, null).currentBg;

        if(sgv == 109) {
            tv.setText("Algorithm worked correctly");
        } else {
            String ApkName = AlgorithmRunner.getPackageCodePathNoCreate(getApplicationContext());
            File f = new File(ApkName);
            tv.setText("Algorithm returned " + sgv + " apk file size " + f.length());
            Log.e(TAG, "Deleting file due to apk failure" + ApkName);
            f.delete();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        Log.i(TAG, "Main activity onDestroy called");
        super.onDestroy();

    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
