package com.hg4.oopalgorithm.oopalgorithm;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import no.bjorninge.LibreReading.LibreReading;

public class MainActivity extends AppCompatActivity {

    static final  String TAG = "OOPAlgorithm";

    private final String LIBRE_OOP_WEBSITE = "https://libreoopweb.azurewebsites.net";

    // This is a compile time option to enable the remote fetch of raw libre readings.
    // These readings will be downloaded from /api/FetchPendingRequests,
    // processed and uploaded to the libre oop site /api/UploadResults endpoint
    private final Boolean LIBRE_OOP_WEB_ENABLE = false;

    //the processing token will be given to you by the admin of the libre oop website
    private final String LIBRE_OOP_WEB_PROCESSING_TOKEN="processorX-YYYYYYYYYYY";

    private final int LIBRE_OOP_WEB_INTERVAL = 30000;//milliseconds

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public String makePostRequest(String reqUrl, String data){
        String response = null;


        try{
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( data.length() ));

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return response;

    }
    public String makeGetCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

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

        int sgv = (int) AlgorithmRunner.RunAlgorithm(0, getApplicationContext(), packet, null).currentBg;

        if(sgv == 63) {
            tv.setText("Algorithm worked correctly");

            if(LIBRE_OOP_WEB_ENABLE) {
                final Handler handler = new Handler();

                handler.postDelayed(new Runnable(){
                    public void run(){

                        try {
                            new FetchLibreRequests().execute();
                            Thread.sleep(9000);
                        } catch (InterruptedException ex){

                        }
                        //do something
                        handler.postDelayed(this, LIBRE_OOP_WEB_INTERVAL);

                    }
                }, LIBRE_OOP_WEB_INTERVAL);
                //new FetchLibreRequests().execute();
            }


        } else {
            String ApkName = AlgorithmRunner.getPackageCodePathNoCreate(getApplicationContext());
            File f = new File(ApkName);
            tv.setText("Algorithm returned " + sgv + " apk file size " + f.length());
            Log.e(TAG, "Deleting file due to apk failure" + ApkName);
            f.delete();
        }


    }

    private class FetchLibreRequests extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
           /* pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();*/

        }
        private void showmsg(final String msg){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            Log.e(TAG, "[" + currentDateandTime + "] dabear:: " + msg);

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String fetchUrl = LIBRE_OOP_WEBSITE +
                    "/api/FetchPendingRequests?processing_accesstoken=" +
                    LIBRE_OOP_WEB_PROCESSING_TOKEN;

            ArrayList<LibreReading> readouts = LibreReading.fetchForProcessing(fetchUrl);


            this.showmsg("read requests (" + readouts.size() + "): ");
            for (LibreReading reading : readouts) {

                this.showmsg("id: " +reading.id);
                this.showmsg("patch: " +reading.patch);

                byte[] decoded;
                byte[] oldState = null;
                try {
                    decoded = Base64.decode(reading.patch, Base64.DEFAULT);
                    this.showmsg("patch decoded:" + Arrays.toString(decoded));
                } catch(IllegalArgumentException ex) {
                    this.showmsg("patch decoded unsuccessfully");
                    continue;
                }

                //oldstate is optional, so continue even if this fails
                if(reading.oldState != null && reading.oldState != "null") {
                    try {
                        this.showmsg("oldstate before decoding: " + reading.oldState);
                        oldState = Base64.decode(reading.oldState, Base64.DEFAULT);
                        this.showmsg("oldstate decoded:" + Arrays.toString(oldState));
                    } catch (IllegalArgumentException ex) {
                        this.showmsg("oldstate either invalid or null, continuing");

                    }
                }
                this.showmsg("oldstate2 set to: " + oldState);
                this.showmsg("oldstate2 decoded:" + Arrays.toString(oldState));

                try{

                    /*int sensorStartTimestamp=0x0e181349;
                    int sensorScanTimestamp=0x0e1c4794;
                    int currentUtcOffset = 0x0036ee80;*/

                    int sensorStartTimestamp = reading.sensorStartTimestamp;
                    int sensorScanTimestamp = reading.sensorScanTimestamp;
                    int currentUtcOffset = reading.currentUtcOffset;

                    showmsg("sensorStartTimestamp in algorunner: " + sensorStartTimestamp);
                    showmsg("sensorScanTimestamp in algorunner: " + sensorScanTimestamp);
                    showmsg("currentUtcOffset in algorunner: " + currentUtcOffset);


                    OOPResults results = AlgorithmRunner.RunAlgorithm(0, getApplicationContext(), decoded, oldState,  sensorStartTimestamp, sensorScanTimestamp, currentUtcOffset);
                    int sgv = (int) results.currentBg;


                    String json  = results.toGson();
                    reading.algoResult = "currentBg: " + String.valueOf(sgv) + " FullAlgoResults: " + json;
                    try{
                        showmsg("base64encoding newState: " + Arrays.toString(results.newState));
                        reading.newState = Base64.encodeToString(results.newState, Base64.NO_WRAP);
                        showmsg("newState b64encoded: " + reading.newState);
                    } catch (Exception innerex) {
                        showmsg("newstate not encoded: " + innerex.getMessage());
                    }

                } catch(Exception ex){
                    reading.algoResult = "Exception: " + ex.getMessage();
                }


                String uploadUrl = LIBRE_OOP_WEBSITE  + "/api/UploadResults";

                LibreReading.uploadProcessedReading(uploadUrl, LIBRE_OOP_WEB_PROCESSING_TOKEN  , reading);


            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            /**
             * Updating parsed JSON data into ListView
             * */
            /*ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, readRequests,
                    R.layout.list_item, new String[]{"id",
                    "patch", "dummy1"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            lv.setAdapter(adapter);*/
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
