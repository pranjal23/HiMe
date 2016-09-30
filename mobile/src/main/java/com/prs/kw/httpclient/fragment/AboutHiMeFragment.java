package com.prs.kw.httpclient.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prs.kw.hime.R;

/**
 * Created by pranjal on 19/6/15.
 */
public class AboutHiMeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_abouthime, container, false);


        return rootView;
    }
}
