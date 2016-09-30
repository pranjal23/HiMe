package com.prs.kw.httpclient.fragment;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.prs.kw.hime.BuildConfig;
import com.prs.kw.httpclient.dialog.BlockViewDialog;
import com.prs.kw.httpserver.LocalWebService;
import com.prs.kw.hime.R;
import com.prs.kw.httpserver.constants.ApplicationConstants;
import com.prs.kw.httpclient.StarterActivity;
import com.prs.kw.httpclient.dialog.ProfileDialog;
import com.prs.kw.httpclient.httpquery.QueryHelper;
import com.prs.kw.httpclient.model.Profile;
import com.prs.kw.httpclient.viewadapter.NsdServiceListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();

    ListView mServicesLv;

    NsdManager mNsdManager;
    StarterActivity mActivity;
    static NsdServiceListAdapter mNsdServiceListAdapter;
    static LocalWebService webService;

    Gson mGson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);
        mStaticListUpdateHandler = new StaticListUpdateHandler(new WeakReference<>(this));
        mGson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getFacebookData();
        mActivity = (StarterActivity) getActivity();

        View rootView = inflater.inflate(R.layout.fragment_starter, container, false);
        mServicesLv = (ListView) rootView.findViewById(R.id.services_listView);
        mNsdServiceListAdapter = new NsdServiceListAdapter(getActivity());
        mServicesLv.setAdapter(mNsdServiceListAdapter);
        mServicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NsdServiceInfo service = (NsdServiceInfo) mNsdServiceListAdapter.getItem(position);
                mNsdManager.resolveService(service, new HiMeNSDResolveListener());
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        webService = mActivity.getBoundService();
        if (mDiscoveryListener == null) {
            initializeDiscoveryListener();
        }

        if(mActivity.getSwitchOnState())
            startServicesDiscovery();
    }

    @Override
    public void onPause() {
        super.onPause();
        // mDiscoveryThread.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getFacebookData() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        mProfileObject = object;
                        mActivity.enableSwitchBtn(true);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,cover,first_name,last_name,gender,location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    static JSONObject mProfileObject;

    public static JSONObject getProfileObject() {
        return mProfileObject;
    }


    private void startServicesDiscovery() {
        try {
            mNsdManager.discoverServices(
                    ApplicationConstants.SERVICE_TYPE,
                    NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            showBlockViewDialog();
        } catch (IllegalArgumentException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void stopServicesDiscovery() {
        try {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        } catch (IllegalArgumentException e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void setUserVisibility(boolean on) {
        Log.d(TAG, "Setting user visibility to :" + on);
        String id = "";
        String name = "no name";
        try {
            id = mProfileObject.getString("id");
            name = mProfileObject.getString("first_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalWebService webService = mActivity.getBoundService();
        if (webService != null) {
            if (on) {
                webService.registerService(id, name);
                startServicesDiscovery();
            } else {
                stopServicesDiscovery();
                webService.unregisterService();
            }
        } else {
            Toast.makeText(getActivity(), "Service not started!!",
                    Toast.LENGTH_LONG).show();
        }
    }

    StaticListUpdateHandler mStaticListUpdateHandler;

    static class StaticListUpdateHandler extends Handler {
        WeakReference<HomeFragment> mFragRef;

        public StaticListUpdateHandler(WeakReference<HomeFragment> fragRef) {
            mFragRef = fragRef;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mFragRef.get().mNsdServiceListAdapter
                            .updateDataSet(mFragRef.get().mServicesList);
                    mFragRef.get().hidelockViewDialog();
                    break;
                case 2:
                    StarterActivity a1 = (StarterActivity) mFragRef.get().getActivity();
                    if (!a1.getSwitchOnState())
                        a1.setSwitchOnState(true);
                    break;
                case 3:
                    StarterActivity a2 = (StarterActivity) mFragRef.get().getActivity();
                    if (a2.getSwitchOnState())
                        a2.setSwitchOnState(false);
                    break;
            }
        }
    }


    CopyOnWriteArrayList<NsdServiceInfo> mServicesList = new CopyOnWriteArrayList<>();
    NsdManager.DiscoveryListener mDiscoveryListener;

    public CopyOnWriteArrayList<NsdServiceInfo> getServicesList(){
        return new CopyOnWriteArrayList<>(mServicesList);
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                if (webService == null)
                    webService = mActivity.getBoundService();
                if (webService != null) {
                    Log.d(TAG, "Service discovery success: " + service);
                    if (!service.getServiceType().equals(ApplicationConstants.SERVICE_TYPE)) {
                        Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                    } else if (service.getServiceName().equals(webService.getServiceName())) {
                        mStaticListUpdateHandler.sendEmptyMessage(2);
                        Log.d(TAG, "Same machine: " + webService.getServiceName());

                        //TODO Remove this from here as it discovers the same device.
                        if (BuildConfig.DEBUG) {
                            if (getMatchingServiceFromList(service) == null) {mServicesList.add(service);}
                        }
                    } else if (service.getServiceName()
                            .contains(ApplicationConstants.SERVICE_NAME_PREFIX)) {
                        if (getMatchingServiceFromList(service) == null) {
                            mServicesList.add(service);
                        }
                    }
                    mStaticListUpdateHandler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "Service Lost:" + service.getServiceName());


                if (webService == null)
                    webService = mActivity.getBoundService();
                if (webService != null) {
                    if (service.getServiceName().equals(webService.getServiceName())) {
                        //mStaticListUpdateHandler.sendEmptyMessage(3);
                    }
                }

                NsdServiceInfo servInf = getMatchingServiceFromList(service);
                if (servInf != null)
                    mServicesList.remove(servInf);
                mStaticListUpdateHandler.sendEmptyMessage(1);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
                mServicesList.removeAll(mServicesList);
                mStaticListUpdateHandler.sendEmptyMessage(1);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                //mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                //mNsdManager.stopServiceDiscovery(this);
            }
        };
    }


    private NsdServiceInfo getMatchingServiceFromList(NsdServiceInfo serviceInfo) {

        for (NsdServiceInfo servInf : mServicesList) {
            if (serviceInfo.getServiceName().equalsIgnoreCase(servInf.getServiceName())) {
                return servInf;
            }
        }

        return null;
    }

    private class HiMeNSDResolveListener implements NsdManager.ResolveListener {


        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {

            showBlockViewDialog();

            InetAddress host = serviceInfo.getHost();
            int port = serviceInfo.getPort();
            String address = host.getHostName();
            String baseUrl = "http://" + address + ":" + port;
            String urlString = baseUrl + ApplicationConstants.BASE_URI;

            URL url = null;
            try {
                url = new URL(urlString);
                String jsonResponse = QueryHelper.GET(url, getActivity());

                if(jsonResponse==null)
                    Log.e(TAG, "jsonResponse is null ");
                else
                    Log.e(TAG, "jsonResponse -> " + jsonResponse);

                try {
                    Profile profileOnView = mGson.fromJson(jsonResponse, Profile.class);
                    hidelockViewDialog();
                    showProfileDialog(profileOnView,baseUrl);
                } catch (JsonSyntaxException e1) {
                    e1.printStackTrace();
                    Log.e(TAG, jsonResponse);
                    Toast.makeText(getActivity(), "JsonSyntaxException in Parsing Json:  " + jsonResponse, Toast.LENGTH_LONG).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Malformed Url:" + url, Toast.LENGTH_LONG).show();
            }

        }
    }

    private void showProfileDialog(Profile profile, String baseUrl) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("profile_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ProfileDialog profileDialog = new ProfileDialog();
        profileDialog.setProfile(profile);
        profileDialog.setBaseUrl(baseUrl);
        profileDialog.setTargetFragment(HomeFragment.this, -1);
        profileDialog.show(ft, "profile_dialog");
    }

    BlockViewDialog mBlockViewDialog;
    private void showBlockViewDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("block_view_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        mBlockViewDialog = new BlockViewDialog();
        mBlockViewDialog.show(ft, "block_view_dialog");
    }

    private void hidelockViewDialog() {
        if(mBlockViewDialog!=null && mBlockViewDialog.isVisible()){
            mBlockViewDialog.dismiss();
        }
    }

    public void blockTouch(View v){

    }
}
