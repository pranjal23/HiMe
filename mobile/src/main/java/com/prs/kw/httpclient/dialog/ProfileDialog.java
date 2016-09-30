package com.prs.kw.httpclient.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.prs.kw.httpclient.fragment.HomeFragment;
import com.prs.kw.httpclient.httpquery.QueryHelper;
import com.prs.kw.httpclient.model.Profile;
import com.prs.kw.hime.R;
import com.prs.kw.httpserver.constants.ApplicationConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pranjal on 18/6/15.
 */
public class ProfileDialog extends DialogFragment {

    final static String TAG = "ProfileDialog";

    ImageView mCoverImage;
    ImageView mProfileImage;
    TextView mNameTv;
    TextView mDetailsTv;

    Profile mProfile;
    String mBaseUrl;
    Button mCancelBtn;
    Button mViewOnFBBtn;
    Button mSayHiBtn;

    NsdManager mNsdManager;

    static Pattern mIdPattern = Pattern.compile("\\{(.*?)\\}");

    public ProfileDialog() {

    }

    public void setProfile(Profile profile) {
        mProfile = profile;
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mNsdManager = (NsdManager) getActivity().getSystemService(Context.NSD_SERVICE);
        mStaticListUpdateHandler = new StaticListUpdateHandler(new WeakReference<>(this));
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_profile, container);

        try {
            if (mProfile != null) {
                Log.e(TAG, "Profile is not null");
                mCoverImage = (ImageView) view.findViewById(R.id.dialog_cover_image);
                Picasso.with(getActivity()).load(mProfile.getCover().getSource()).into(mCoverImage);
                try {
                    URL imageUrl = new URL("https://graph.facebook.com/" + mProfile.getId() + "/picture?type=large");
                    mProfileImage = (ImageView) view.findViewById(R.id.dialog_profile_image);
                    Picasso.with(getActivity()).load(imageUrl.toString()).into(mProfileImage);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                getDialog().setTitle(mProfile.getName());
                mNameTv = (TextView) view.findViewById(R.id.dialog_profile_name);
                mNameTv.setText(mProfile.getGender());

                String location = "";
                if (mProfile.getLocation() != null) {
                    location = mProfile.getLocation().getName();
                }
                mDetailsTv = (TextView) view.findViewById(R.id.dialog_profile_details);
                mDetailsTv.setText(location);

                mCancelBtn = (Button) view.findViewById(R.id.cancel_btn);
                mCancelBtn.setOnClickListener(mBtnClickListener);

                mViewOnFBBtn = (Button) view.findViewById(R.id.fb_btn);
                mViewOnFBBtn.setOnClickListener(mBtnClickListener);

                mSayHiBtn = (Button) view.findViewById(R.id.hi_btn);
                setHiBtnVisibility();
            }
            else
            {
                Log.e(TAG, "Profile is null");
            }
        } catch (Exception e) {
        }

        return view;
    }

    private void setHiBtnVisibility() {
        Log.e(TAG,"BaseUrl: " + mBaseUrl);
        if (mBaseUrl != null && !mBaseUrl.isEmpty()) {
            mSayHiBtn.setOnClickListener(mBtnClickListener);
            mSayHiBtn.setVisibility(View.VISIBLE);
        } else {
            mSayHiBtn.setVisibility(View.INVISIBLE);
            new ProfileAsyncSearch().execute();
        }
    }


    View.OnClickListener mBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.equals(mCancelBtn)) {
                ProfileDialog.this.dismiss();
            } else if (v.equals(mViewOnFBBtn)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mProfile.getLink()));
                startActivity(intent);
            } else if (v.equals(mSayHiBtn)) {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                postProfile();
            }
        }
    };

    public void postProfile() {
        String postUrl = mBaseUrl + ApplicationConstants.POST_HI;

        URL url = null;
        try {
            url = new URL(postUrl);
            JSONObject myProfile = HomeFragment.getProfileObject();
            String jsonResponse = QueryHelper.POST(url, myProfile, getActivity());
            try {
                Toast.makeText(getActivity(), jsonResponse, Toast.LENGTH_LONG).show();
            } catch (JsonSyntaxException e1) {
                e1.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Malformed Url:" + url, Toast.LENGTH_LONG).show();
        }
    }

    class ProfileAsyncSearch extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String currentId = mProfile.getId();

            Fragment targetFragment = ProfileDialog.this.getTargetFragment();
            if (targetFragment != null) {
                if (targetFragment instanceof HomeFragment) {
                    CopyOnWriteArrayList<NsdServiceInfo> servicesList = ((HomeFragment) targetFragment).getServicesList();
                    listIteration:for (NsdServiceInfo nsdServiceInfo : servicesList) {
                        String serviceName = nsdServiceInfo.getServiceName();

                        Matcher m1 = mIdPattern.matcher(serviceName);
                        while (m1.find()) {
                            String id = m1.group(1);
                            if (id.equalsIgnoreCase(currentId)) {
                                mNsdManager.resolveService(nsdServiceInfo, new ProfileNSDResolveListener());
                                break listIteration;
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    private class ProfileNSDResolveListener implements NsdManager.ResolveListener {


        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {

            InetAddress host = serviceInfo.getHost();
            int port = serviceInfo.getPort();
            String address = host.getHostName();
            String baseUrl = "http://" + address + ":" + port;

            if (ProfileDialog.this.isVisible()) {
                mBaseUrl = baseUrl;
                mStaticListUpdateHandler.sendEmptyMessage(1);
            }
        }
    }

    StaticListUpdateHandler mStaticListUpdateHandler;

    static class StaticListUpdateHandler extends Handler {
        WeakReference<ProfileDialog> mFragRef;

        public StaticListUpdateHandler(WeakReference<ProfileDialog> fragRef) {
            mFragRef = fragRef;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFragRef.get() != null) {
                mFragRef.get().setHiBtnVisibility();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }
}
