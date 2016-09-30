package com.prs.kw.httpclient.viewadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.prs.kw.hime.R;
import com.prs.kw.httpclient.fragment.NotificationFragment;
import com.prs.kw.httpclient.model.Profile;
import com.prs.kw.notification.NotificationItem;
import com.prs.kw.notification.NotificationManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by pranjal on 14/5/15.
 */
public class NotificationListAdapter extends BaseAdapter {

    private static final String TAG = "NotificationListAdapter";

    CopyOnWriteArrayList<NotificationItem> mList = new CopyOnWriteArrayList<>();
    NotificationFragment mFragment;
    Context mContext;
    NotificationManager mNotificationManager;
    Gson mGson;

    public NotificationListAdapter(NotificationFragment fragment, NotificationManager notificationManager) {
        mFragment = fragment;
        mContext = mFragment.getActivity().getApplicationContext();
        mNotificationManager = notificationManager;
        mGson = new Gson();
    }

    public void updateDataSet(CopyOnWriteArrayList<NotificationItem> list){
        mList.removeAll(mList);
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    class ViewHolder{
        ImageView serviceImageIv;
        TextView serviceNameTv;
        ImageButton dismissBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.notification_item, null);
            viewHolder = new ViewHolder();
            viewHolder.serviceImageIv = (ImageView) convertView.findViewById(R.id.notifications_profile_image);
            viewHolder.serviceNameTv = (TextView) convertView.findViewById(R.id.notifications_profile_name);
            viewHolder.dismissBtn = (ImageButton) convertView.findViewById(R.id.notifications_dismiss_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            JSONObject jsonObject = new JSONObject(mList.get(position).getBody());
            String serviceName = jsonObject.getString("name");
            try {
                URL imageUrl = new URL("https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?type=large");
                Picasso.with(mContext).load(imageUrl.toString()).into(viewHolder.serviceImageIv);
                viewHolder.serviceImageIv.setTag(jsonObject);
                viewHolder.serviceImageIv.setOnClickListener(mProfileImageClickListener);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            viewHolder.serviceNameTv.setText(serviceName);
            viewHolder.dismissBtn.setTag(mList.get(position));
            viewHolder.dismissBtn.setOnClickListener(mDismissBtnClickListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public Object getItem(int position) {
        return mList.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    public int getCount() {
        return mList.size();
    }

    View.OnClickListener mDismissBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Inside dismiss btn click");
            NotificationItem ni = (NotificationItem) v.getTag();
            ni.setDismissed(true);
            mNotificationManager.updateNotification(ni);
        }
    };

    View.OnClickListener mProfileImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject jsonObject = (JSONObject)v.getTag();
            Profile profileOnView = mGson.fromJson(jsonObject.toString(), Profile.class);
            mFragment.showProfileDialog(profileOnView);
        }
    };
}
