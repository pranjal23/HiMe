package com.prs.kw.httpclient.helper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.prs.kw.httpclient.httpquery.QueryHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.LogRecord;

/**
 * Created by pranjal on 20/5/15.
 */
public class FBHelper {

    public static boolean isLoggedIn() {
        boolean loggedIn=false;
        if(AccessToken.getCurrentAccessToken()!=null)
            loggedIn=true;

        return loggedIn;
    }

    public static void getNameAsync(final Context context,final WeakReference<TextView> tvRef, final String id) {
        final String responseKey = "resp";
        final Handler nameHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String response = msg.getData().getString(responseKey);
                if (response != null && !response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        TextView tv = tvRef.get();
                        if (tv != null) {
                            String name = jsonObject.getString("name");
                            name = name == null ? "" : name;
                            tv.setText(name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    TextView tv = tvRef.get();
                    if (tv != null) {
                        String name = "Failed to get response from FB Server";
                        tv.setText(name);
                    }
                }
            }
        };

        try {
            final URL nameUrl = new URL("https://graph.facebook.com/" +id + "?access_token=" + AccessToken.getCurrentAccessToken().getToken());
            Log.d("FBHelper", "name url: " + nameUrl.toString());
            new Thread(){
                @Override
                public void run() {
                    String response = QueryHelper.GET(nameUrl,context);
                    Message msg = nameHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString(responseKey,response);
                    msg.setData(bundle);
                    nameHandler.sendMessage(msg);
                }
            }.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
