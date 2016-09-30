package com.prs.kw.httpclient.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prs.kw.hime.R;

/**
 * Created by pranjal on 29/6/15.
 */
public class BlockViewDialog extends DialogFragment {

    public BlockViewDialog(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.block_view, container);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }
}
