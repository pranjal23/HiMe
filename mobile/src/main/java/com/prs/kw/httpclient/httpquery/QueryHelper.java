package com.prs.kw.httpclient.httpquery;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by pranjal on 6/6/15.
 */
public class QueryHelper {

    static String TAG = "QueryHelper";

    public static String GET(URL url, Context context) {

        if(url!=null){
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                Log.e(TAG, "Server return response code: " + urlConnection.getResponseCode());

                if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return streamToString(in);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to open url: " + e.getMessage());
            }
        }
        return null;
    }

    public static String POST(URL url,JSONObject jsonObject, Context context) {

        if(url!=null){
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                //Post data
                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream ());
                printout.writeBytes(jsonObject.toString());
                printout.flush();
                printout.close();

                //Receive response
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return streamToString(in);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to open url: " + e.getMessage());
            }
        }
        return null;
    }

    public static String streamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

}
