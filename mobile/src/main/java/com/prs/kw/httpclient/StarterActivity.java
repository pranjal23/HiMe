package com.prs.kw.httpclient;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.FacebookSdk;
import com.prs.kw.httpclient.fragment.AboutHiMeFragment;
import com.prs.kw.httpclient.fragment.NoWifiFragment;
import com.prs.kw.httpclient.fragment.NotificationFragment;
import com.prs.kw.httpserver.LocalWebService;
import com.prs.kw.hime.R;
import com.prs.kw.httpserver.constants.PreferenceKeys;
import com.prs.kw.httpclient.fragment.HomeFragment;
import com.prs.kw.httpclient.fragment.LoginFragment;
import com.prs.kw.httpclient.helper.FBHelper;
import com.prs.kw.httpclient.model.MenuItem;
import com.prs.kw.httpclient.viewadapter.MenuListAdapter;
import com.prs.kw.notification.NotificationFacade;
import com.prs.kw.notification.NotificationManager;
import com.prs.kw.util.NetworkUtil;

import java.util.concurrent.CopyOnWriteArrayList;

public class StarterActivity extends AppCompatActivity {

    public static final String TAG = "StarterActivity";

    int[] menu_icons = {
            R.drawable.home,
            R.drawable.profile,
            R.drawable.settings,
            R.drawable.about,
            };
    SharedPreferences mPreferences;
    boolean mIsBound = false;
    private LocalWebService mBoundService;
    private CopyOnWriteArrayList<MenuItem> mMenuItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Button mNotificationCountBtn;
    MenuListAdapter mMenuListAdapter;
    Switch mSwitch;

    TextView mTopBarTitle;
    ToggleButton mTopBarToggleBtn;

    HomeFragment mHomeFragment;
    LoginFragment mLoginFragment;
    NotificationFragment mNotificationFragment;
    AboutHiMeFragment mAboutHiMeFragment;
    NoWifiFragment mNoWifiFragment;

    NotificationManager mNotificationManager;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((LocalWebService.WebServiceBinder) service).getService();
            Toast.makeText(StarterActivity.this, "Service Connected",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            Toast.makeText(StarterActivity.this, "Service Disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationFacade.init(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        doBindService();

        mNotificationManager = NotificationManager.getNotificationManagerInstance(getApplicationContext());

        setContentView(R.layout.activity_starter);

        String[] items = getResources().getStringArray(R.array.menu_items);
        mMenuItems = new CopyOnWriteArrayList<>();
        for(int i=0; i<items.length; i++){
            MenuItem item = new MenuItem();
            item.setName(items[i]);
            item.setDrawableId(menu_icons[i]);
            mMenuItems.add(i,item);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mMenuListAdapter = new MenuListAdapter(this);
        mDrawerList.setAdapter(mMenuListAdapter);
        mMenuListAdapter.updateDataSet(mMenuItems);
        mMenuListAdapter.notifyDataSetChanged();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.drawer_icon,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mTopBarTitle.setText(getString(R.string.app_name));
                mTopBarToggleBtn.invalidate();
                mTopBarToggleBtn.setChecked(false);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mTopBarTitle.setText(getString(R.string.menu));
                mTopBarToggleBtn.invalidate();
                mTopBarToggleBtn.setChecked(true);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_layout);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.saffron));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setFragmentForState(0);
                        break;
                    case 1:
                        setFragmentForState(1);
                        break;
                    case 2:
                        setFragmentForState(2);
                        break;
                    case 3:
                        setFragmentForState(3);
                        break;
                }
                mDrawerLayout.closeDrawers();
            }
        });

        mTopBarTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbar_title);
        mTopBarToggleBtn = (ToggleButton) getSupportActionBar().getCustomView().findViewById(R.id.action_tb);
        mSwitch = (Switch) getSupportActionBar().getCustomView().findViewById(R.id.visible_switch);
        mNotificationCountBtn = (Button) getSupportActionBar().getCustomView().findViewById(R.id.actionbar_hi_count);
        mNotificationCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(2);
            }
        });
        enableSwitchBtn(false);
        mTopBarToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    mDrawerLayout.openDrawer(Gravity.START);
                else
                    mDrawerLayout.closeDrawer(Gravity.START);
            }
        });
        mTopBarToggleBtn.setBackgroundResource(R.drawable.drawer_icon);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Switch Checked to :" + isChecked);
                if (mHomeFragment.isVisible()) {
                    if (programaticCheckChange)
                        programaticCheckChange = false;
                    else
                        mHomeFragment.setUserVisibility(isChecked);
                }
            }
        });

        mTopBarTitle.setText(getString(R.string.app_name));
        setState(0);
    }

    public void enableSwitchBtn(boolean enabled){
        mSwitch.setEnabled(enabled);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCount();

        if(NetworkUtil.isConnected(this))
            mSwitch.setVisibility(View.VISIBLE);
        else
            mSwitch.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mNotificationBroadcastReceiver);
        unregisterReceiver(mConnectvityBroadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(NotificationManager.NOTIFICATION_REFRESH_ACTION);
        registerReceiver(mNotificationBroadcastReceiver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnectvityBroadcastReceiver,intentFilter1);
    }

    BroadcastReceiver mNotificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateCount();
        }
    };

    BroadcastReceiver mConnectvityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(NetworkUtil.isConnected(StarterActivity.this)) {
                mSwitch.setVisibility(View.VISIBLE);

            } else {
                mSwitch.setVisibility(View.INVISIBLE);
            }
            setState(0);
        }
    };

    public void updateCount(){
        int count = mNotificationManager.getNotifications().size();
        String countStr = count<=99?Integer.toString(count):"99+";
        mNotificationCountBtn.setText(countStr);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    boolean programaticCheckChange = false;
    public void setSwitchOnState(boolean onOff){
        programaticCheckChange = true;
        mSwitch.setChecked(onOff);
    }

    public boolean getSwitchOnState(){
        return mSwitch.isChecked();
    }

    public void showSwitch(boolean show){
        if(show)
            mSwitch.setVisibility(View.VISIBLE);
        else
            mSwitch.setVisibility(View.INVISIBLE);
    }

    public void setState(int menu_state) {
        mPreferences.edit().putInt(PreferenceKeys.SAVED_STATE_PREFS_KEY, menu_state);
        setFragmentForState(menu_state);
    }

    void setFragmentForState(int menu_state) {
        showSwitch(false);
        boolean isConnected = NetworkUtil.isConnected(this);
        switch (menu_state) {
            case 0:
                if(FBHelper.isLoggedIn() && isConnected)
                {
                    showSwitch(true);
                    setFragment0();
                }
                else if(isConnected)
                {
                    showSwitch(false);
                    setFragment1();
                }
                else
                {
                    showSwitch(false);
                    setFragmentNoWifi();
                }
                mMenuListAdapter.setSelectedPosition(0);
                break;
            case 1:
                setFragment1();
                mMenuListAdapter.setSelectedPosition(1);
                break;
            case 2:
                showSwitch(false);
                if(FBHelper.isLoggedIn())
                {
                    setFragment2();
                }
                else
                {
                    setFragment1();
                }
                mMenuListAdapter.setSelectedPosition(2);
                break;
            case 3:
                showSwitch(false);
                setFragment3();
                mMenuListAdapter.setSelectedPosition(3);
                break;
            default:
                if(FBHelper.isLoggedIn() && isConnected)
                {
                    showSwitch(true);
                    setFragment0();
                }
                else if(isConnected)
                {
                    showSwitch(false);
                    setFragment1();
                }
                else
                {
                    showSwitch(false);
                    setFragmentNoWifi();
                }
                mMenuListAdapter.setSelectedPosition(0);
                break;
        }
        mMenuListAdapter.notifyDataSetChanged();
    }

    public HomeFragment getHomeFragment(){
        return mHomeFragment;
    }

    void setFragment0() {
        if(mHomeFragment==null)
            mHomeFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, mHomeFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    void setFragment1() {
        if(mLoginFragment==null)
            mLoginFragment = new LoginFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, mLoginFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    void setFragment2() {
        if(mNotificationFragment==null)
            mNotificationFragment = new NotificationFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, mNotificationFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    void setFragment3() {
        if(mAboutHiMeFragment==null)
            mAboutHiMeFragment = new AboutHiMeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, mAboutHiMeFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    void setFragmentNoWifi() {
        if(mNoWifiFragment==null)
            mNoWifiFragment = new NoWifiFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, mNoWifiFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    void doBindService() {
        bindService(new Intent(StarterActivity.this,
                LocalWebService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getBoundService().unregisterService();
        }catch (Exception e){
            e.printStackTrace();
        }
        doUnbindService();
    }

    public LocalWebService getBoundService() {
        return mBoundService;
    }
}
