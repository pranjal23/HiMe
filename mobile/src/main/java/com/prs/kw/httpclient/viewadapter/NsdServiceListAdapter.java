package com.prs.kw.httpclient.viewadapter;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prs.kw.httpclient.helper.EncryptionHelper;
import com.prs.kw.httpclient.helper.FBHelper;
import com.prs.kw.hime.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pranjal on 14/5/15.
 */
public class NsdServiceListAdapter extends BaseAdapter {

    CopyOnWriteArrayList<NsdServiceInfo> mList = new CopyOnWriteArrayList<>();
    Context mContext;
    static Pattern mIdPattern = Pattern.compile("\\{(.*?)\\}");
    static Pattern mNamePattern = Pattern.compile("\\[(.*?)\\]");

    public NsdServiceListAdapter(Context context) {
        mContext = context;
    }

    private static final String TAG = "NsdServiceListAdapter";

    public void updateDataSet(CopyOnWriteArrayList<NsdServiceInfo> list){
        mList.removeAll(mList);
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    class ViewHolder{
        ImageView serviceImageIv;
        TextView serviceNameTv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.services_item, null);
            viewHolder = new ViewHolder();
            viewHolder.serviceImageIv = (ImageView) convertView.findViewById(R.id.services_profile_image);
            viewHolder.serviceNameTv = (TextView) convertView.findViewById(R.id.services_profile_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String serviceName = mList.get(position).getServiceName();
        //Log.d(TAG,"service name: " + serviceName);
        Matcher m = mIdPattern.matcher(serviceName);
        while (m.find()) {
            String id = m.group(1);
            try {
                String decryptedId = EncryptionHelper.decrypt(id);
                URL imageUrl = new URL("https://graph.facebook.com/" + decryptedId + "/picture?type=large");
                Picasso.with(mContext).load(imageUrl.toString()).into(viewHolder.serviceImageIv);
                //FBHelper.getNameAsync(mContext,new WeakReference<>(viewHolder.serviceNameTv), new String(decryptedId));
                break;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            break;
        }


        Matcher m1 = mNamePattern.matcher(serviceName);
        while (m1.find()) {
            String first_name = m1.group(1);
            viewHolder.serviceNameTv.setText(first_name);
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
}
