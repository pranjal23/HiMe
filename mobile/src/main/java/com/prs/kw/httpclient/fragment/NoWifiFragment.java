package com.prs.kw.httpclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prs.kw.hime.R;

/**
 * Created by pranjal on 19/6/15.
 */
public class NoWifiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nowifi, container, false);
        Button wifiSettingsBtn = (Button) rootView.findViewById(R.id.wifi_settings_button);
        wifiSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        return rootView;
    }
}
