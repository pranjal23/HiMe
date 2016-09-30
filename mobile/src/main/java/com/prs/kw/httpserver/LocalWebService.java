package com.prs.kw.httpserver;

/**
 * Created by pranjal on 13/5/15.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.prs.kw.httpclient.helper.EncryptionHelper;
import com.prs.kw.httpserver.constants.ApplicationConstants;

import java.io.IOException;
import java.net.ServerSocket;

public class LocalWebService extends Service {

    public static final String TAG = "LocalWebService";

    public static int NSD_PORT = 24031;
    static LocalWebService thisService = null;
    private final IBinder mBinder = new WebServiceBinder();
    NsdManager mNsdManager;
    NsdManager.RegistrationListener mRegistrationListener;

    String mServiceName = "";

    static int getNsdPort() {
        int port = NSD_PORT;

        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return port;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterService();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisService = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mNsdManager != null) {
                mNsdManager.unregisterService(mRegistrationListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getServiceName() {
        return mServiceName;
    }

    public void registerService(String id, String name) {
        Log.d(TAG, "Register Service Called");
        if (mNsdManager == null) {
            mNsdManager = (NsdManager) LocalWebService.this.getSystemService(Context.NSD_SERVICE);
            initializeRegistrationListener();
        }

        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        NSD_PORT = getNsdPort();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName(ApplicationConstants.SERVICE_NAME_PREFIX + "{" + EncryptionHelper.encrypt(id) + "}" + "[" + name + "]");
        serviceInfo.setServiceType(ApplicationConstants.SERVICE_TYPE);
        serviceInfo.setPort(NSD_PORT);
        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
        if (!RestletServer.getInstance().startRestletServer(NSD_PORT, getApplicationContext())) {
            unregisterService();
            Toast.makeText(getApplicationContext(), "Failed to start server component ", Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "Register Service Finished");
    }

    public void unregisterService() {
        Log.d(TAG, "Unregister Service Called");
        if (mNsdManager != null) {
            mNsdManager.unregisterService(mRegistrationListener);
        }
        RestletServer.getInstance().stopRestletServer();
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();
                Toast.makeText(getApplicationContext(), "Service registered on port: " + NSD_PORT,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Toast.makeText(getApplicationContext(), "Registration failed!, error code:"
                        + errorCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };
    }

    public class WebServiceBinder extends Binder {
        public LocalWebService getService() {
            return thisService;
        }
    }
}


