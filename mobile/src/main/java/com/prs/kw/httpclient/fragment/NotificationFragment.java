package com.prs.kw.httpclient.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.prs.kw.httpclient.StarterActivity;
import com.prs.kw.httpclient.dialog.ProfileDialog;
import com.prs.kw.httpclient.model.Profile;
import com.prs.kw.httpclient.viewadapter.NotificationListAdapter;
import com.prs.kw.hime.R;
import com.prs.kw.notification.NotificationItem;
import com.prs.kw.notification.NotificationManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by pranjal on 19/6/15.
 */
public class NotificationFragment  extends Fragment {

    NotificationManager mNotificationManager;
    NotificationListAdapter mNotificationListAdapter;

    ListView mNotificationsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationManager = NotificationManager.getNotificationManagerInstance(getActivity().getApplicationContext());
        mNotificationListAdapter = new NotificationListAdapter(this,mNotificationManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        mNotificationsListView = (ListView) rootView.findViewById(R.id.notifications_listView);
        mNotificationsListView.setAdapter(mNotificationListAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotifications();
    }

    public void updateNotifications(){
        List notificationList = mNotificationManager.getNotifications();
        mNotificationListAdapter.updateDataSet(new CopyOnWriteArrayList<NotificationItem>(notificationList));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mNotificationBroadcastReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(NotificationManager.NOTIFICATION_REFRESH_ACTION);
        getActivity().registerReceiver(mNotificationBroadcastReceiver, intentFilter);
    }

    BroadcastReceiver mNotificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(NotificationFragment.this.isVisible())
                updateNotifications();
        }
    };

    public void showProfileDialog(Profile profile) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("profile_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ProfileDialog profileDialog = new ProfileDialog();
        profileDialog.setProfile(profile);
        profileDialog.setTargetFragment(((StarterActivity)getActivity()).getHomeFragment(),-1);
        profileDialog.show(ft, "profile_dialog");
    }
}
