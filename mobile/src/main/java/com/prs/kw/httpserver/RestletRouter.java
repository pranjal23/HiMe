package com.prs.kw.httpserver;

import android.content.Context;
import android.util.Log;

import com.prs.kw.httpserver.constants.ApplicationConstants;
import com.prs.kw.httpclient.fragment.HomeFragment;
import com.prs.kw.notification.NotificationFacade;
import com.prs.kw.notification.NotificationItem;
import com.prs.kw.notification.NotificationItemBuilder;
import com.prs.kw.notification.NotificationManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.routing.Router;

import java.lang.ref.WeakReference;

/**
 * Created by pranjal on 14/5/15.
 */
public class RestletRouter extends Application {

    public static final String TAG = RestletRouter.class.getSimpleName();


    WeakReference<Component> mServerComponent;
    NotificationManager mNotificationManager;

    public RestletRouter(WeakReference<Component> serverComp, Context context){
        mServerComponent = serverComp;
        mNotificationManager = NotificationManager.getNotificationManagerInstance(context);
    }

    @Override
    public Restlet createInboundRoot() {
        Router router;
        if(mServerComponent.get()!=null) {
            router = new Router(mServerComponent.get().getContext().createChildContext());
            router.attach(ApplicationConstants.BASE_URI, baseUriHandler);
            router.attach(ApplicationConstants.POST_HI, postHiUriHandler);
        } else {
            router = new Router();
        }

        return router;
    }

    Restlet baseUriHandler = new Restlet() {
        @Override
        public void handle(Request request, Response response) {
            super.handle(request, response);
            JSONObject profileObj = HomeFragment.getProfileObject();

            String responseStr="{}";
            if(profileObj!=null) {
                responseStr = profileObj.toString();
            }
            Log.e(TAG, "Sending Response ->" + responseStr);

            response.setEntity(responseStr, MediaType.APPLICATION_JSON);
            response.setStatus(Status.SUCCESS_OK);
            releaseRestletResource(request, response);
        }
    };

    Restlet postHiUriHandler = new Restlet() {
        @Override
        public void handle(Request request, Response response) {
            super.handle(request, response);

            JSONObject jsonObject = new JSONObject();
            Status retStatus;
            Method requestMethod = request.getMethod();
            if(requestMethod.getName().equalsIgnoreCase("POST")) {
                Log.d(TAG, "Inside post hi handler POST 1");
                JSONObject profileObj = null;
                try {
                    profileObj = new JSONObject(request.getEntityAsText().toString());//Verify its a valid profile json string
                    NotificationItem ni = NotificationItemBuilder.build(profileObj.getString("id"),profileObj.toString());
                    mNotificationManager.addNotificationItem(ni);
                    retStatus = Status.SUCCESS_OK;
                    jsonObject.put("STATUS", "OK");
                } catch (JSONException e) {
                    e.printStackTrace();
                    retStatus = Status.CLIENT_ERROR_NOT_ACCEPTABLE;
                    try {
                        jsonObject.put("STATUS", "FAILED");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                Log.d(TAG, "Inside post hi handler, method not allowed!!");
                retStatus = Status.CLIENT_ERROR_METHOD_NOT_ALLOWED;
                try {
                    jsonObject.put("STATUS","METHOD_NOT_ALLOWED");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            Log.d(TAG, "Inside post hi handler POST 3, sending response - " + jsonObject.toString());
            response.setEntity(jsonObject.toString(), MediaType.APPLICATION_JSON);
            response.setStatus(retStatus);
            releaseRestletResource(request, response);
        }
    };

    public static void releaseRestletResource(Request request, Response response) {
        request.release();
        Log.d(TAG, "releaseRestletResource() :Releaseing the resource =");
    }

}
