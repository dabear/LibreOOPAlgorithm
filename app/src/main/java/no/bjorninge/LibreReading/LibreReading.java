package no.bjorninge.LibreReading;

import android.util.Log;

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
import java.util.Date;



/**
 * Created by bjorninge on 19.04.2018.
 */

public class LibreReading {
    public String id;
    public String patch;
    public String oldState;
    public int sensorStartTimestamp;
    public int sensorScanTimestamp;
    public int currentUtcOffset;

    public String algoResult;
    public String newState;

    public static final String TAG="LibreReading";
    public static String makeGetRequest(String reqUrl) {
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

    public static String makePostRequest(String reqUrl, String data){
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

    public static String urlEncode(String source) {
        try {
            return URLEncoder.encode(source, "UTF-8");
        } catch (Exception e) {
            return "encoding-exception";
        }
    }

    public static void uploadProcessedReading(String site, String processing_token, LibreReading reading){
        //String site = "http://requestbin.fullcontact.com/yjembdyj";
        String uploadUrl = site;
        String data = "processing_accesstoken=" + processing_token + "&uuid=" +
                urlEncode(reading.id) + "&result=" +
                urlEncode("some POST value from android: " + reading.algoResult ) +
                "&newState=" + urlEncode(reading.newState);


        String jsonStr2 = makePostRequest(uploadUrl, data);
        showmsg("response after upload: " + jsonStr2);

    }
    public static ArrayList<LibreReading> fetchForProcessing(String fetchUrl) {


        // Making a request to url and getting response
        //String jsonStr = "test string not to be decoded successfully"; //makeGetRequest(fetchUrl);
        String jsonStr = LibreReading.makeGetRequest(fetchUrl);
        ArrayList<LibreReading> readRequests = new ArrayList<>();

        Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr != null) {
            try {

                JSONObject jsonObj = new JSONObject(jsonStr);


                Boolean isError = jsonObj.getBoolean("Error");

                showmsg("iserror:" + (isError ? "true" : "false"));

                if (isError) {
<<<<<<< HEAD
                    showmsg("errorcontents in jsonstring was: " + jsonStr);
=======
>>>>>>> separate upload/download of readings to another class, supports advanced parameters and upload newstate
                    return null;
                }

                // Getting JSON Array node
                JSONArray reqs = jsonObj.getJSONArray("Result");

                // looping through All Contacts
                for (int i = 0; i < reqs.length(); i++) {
                    JSONObject c = reqs.getJSONObject(i);

                    String id = c.getString("uuid");

                    String patch = c.getString("b64contents");

                    String oldState = c.optString("oldState", null);

                    int sensorStartTimestamp = c.optInt("sensorStartTimestamp", 0x0e181349);
                    int sensorScanTimestamp = c.optInt("sensorScanTimestamp", 0x0e1c4794);
                    int currentUtcOffset = c.optInt("currentUtcOffset", 0x0036ee80);


                    // tmp hash map for single contact
                    LibreReading reading = new LibreReading();
                    reading.id = id;
                    reading.patch = patch;
                    reading.oldState = oldState;
                    reading.sensorScanTimestamp = sensorScanTimestamp;
                    reading.sensorStartTimestamp = sensorStartTimestamp;
                    reading.currentUtcOffset = currentUtcOffset;


                    // adding contact to contact list
                    readRequests.add(reading);
                }
            } catch (final JSONException e) {
                showmsg("Json parsing error initial: " + e.getMessage());


            }


        } else {
            showmsg("jsonStr was null");
        }

        return readRequests;
    }

    private static void showmsg(final String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Log.e(TAG, "[" + currentDateandTime + "] dabear:: " + msg);

    }


    private static String convertStreamToString(InputStream is) {
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
}

