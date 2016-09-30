package com.prs.kw.httpclient.viewadapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prs.kw.hime.R;
import com.prs.kw.httpclient.model.MenuItem;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by pranjal on 14/5/15.
 */
public class MenuListAdapter extends BaseAdapter {

    CopyOnWriteArrayList<MenuItem> mList = new CopyOnWriteArrayList<>();
    Context mContext;
    SpannableStringBuilder mSpannableStringBuilder;

    private int selectedPosition = 0;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public MenuListAdapter(Context context) {
        mContext = context;
        mSpannableStringBuilder = new SpannableStringBuilder();
    }

    public void updateDataSet(CopyOnWriteArrayList<MenuItem> list){
        mList.removeAll(mList);
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    class ViewHolder{
        View background;
        ImageView menuItemIv;
        TextView menuItemNameTv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.menu_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.menuItemNameTv = (TextView) convertView.findViewById(R.id.menu_name_tv);
            viewHolder.menuItemIv = (ImageView) convertView.findViewById(R.id.menu_iv);
            viewHolder.background = convertView.findViewById(R.id.menu_item_back);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.menuItemIv.setBackgroundResource(mList.get(position).getDrawableId());

        if(position==getSelectedPosition()){
            mSpannableStringBuilder.clear();
            String itemLabel = mList.get(position).getName();
            mSpannableStringBuilder.append(itemLabel);
            mSpannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, itemLabel.length(), 0);
            viewHolder.menuItemNameTv.setText(mSpannableStringBuilder);
            viewHolder.background.setBackgroundResource(R.drawable.red_strip);
        } else {
            viewHolder.menuItemNameTv.setText(mList.get(position).getName());
            viewHolder.background.setBackground(null);
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
