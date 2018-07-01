package com.hg4.oopalgorithm.oopalgorithm;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import java.io.File;

public class MainActivity extends AppCompatActivity {

    static final  String TAG = "OOPAlgorithm";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SetVersion();
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

        int sgv = (int) AlgorithmRunner.RunAlgorithm(0, getApplicationContext(), packet, true, null).currentBg;

        if(sgv == 63) {
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
