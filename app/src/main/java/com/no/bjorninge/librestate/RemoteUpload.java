package com.no.bjorninge.librestate;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

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



public class RemoteUpload extends AsyncTask<String, Void, Void> {
    public static String TAG = "RemoteUpload";
    //public static String accesstoken = "you-should-fill-this-and-remove-comment";
    public static String makePostRequest(String reqUrl, String data){
        String response = null;

        Log.e(TAG, "got requrl: " + reqUrl);
        Log.e(TAG, "got data: " + data);



        try{
            Log.e(TAG, "creating url: ");
            URL url = new URL(reqUrl);
            Log.e(TAG, "opening url: ");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.e(TAG, "setting post ");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty( "charset", "utf-8");
            Log.e(TAG, "setting content length ");
            conn.setRequestProperty( "Content-Length", Integer.toString( data.length() ));

            Log.e(TAG, "creating output stream");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.e(TAG, "writing data ");
            wr.write(data);
            wr.flush();
            // read the response
            Log.e(TAG, "getting inputstream and converting");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + " stacktrace: ");
            e.printStackTrace();
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

    @Override
    protected Void doInBackground(String... params) {
        String contents = params[0];
        uploadContents(contents);

        return null;
    }

    public static void uploadContents( String contents){
        String site = "https://remoteblobstorage.azurewebsites.net/api/CreateRequestAsync";
        final String uploadUrl = site;
        final String data = "accesstoken=" + accesstoken + "&contents=" + urlEncode(contents);



        Log.e(TAG,"dabear: async upload starting:" + uploadUrl + " , data:" + data);

        String jsonStr2 = makePostRequest(uploadUrl, data);
        Log.e(TAG,"dabear: response after async upload: " + jsonStr2);


    }
}
