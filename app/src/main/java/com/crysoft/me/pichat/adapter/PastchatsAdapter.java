package com.crysoft.me.pichat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.crysoft.me.pichat.Network.Urls;
import com.crysoft.me.pichat.R;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.EmoticonsHelper;
import com.crysoft.me.pichat.models.UserDetails;

import java.util.List;

/**
 * Created by Maxx on 6/20/2016.
 */
public class PastChatsAdapter extends BaseAdapter {

    private List<UserDetails> list;
    private LayoutInflater layoutInflater;
    private AQuery aQuery;
    private SharedPreferences sharedPreferences;
    private String key;

    public PastChatsAdapter(LayoutInflater inflater, List<UserDetails> list, Context context){
        this.layoutInflater = inflater;
        aQuery = new AQuery(context);
        this.list = list;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            viewHolder  =    new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_past_chats,null);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_user_image);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.tv_last_message);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserDetails userDetails = list.get(position);
        if (userDetails.getImage()==null){
            aQuery.id(viewHolder.ivImage).background(R.drawable.prof_pic);

        }else {
            aQuery.id(viewHolder.ivImage).image(Urls.BASE_IMAGE + userDetails.getImage());
        }

        viewHolder.tvName.setText(userDetails.getName());
        viewHolder.tvName.setTextColor(layoutInflater.getContext().getResources().getColor(R.color.black));

        if (userDetails.getLastMessage()==null || userDetails.getLastMessage()==""){
            viewHolder.tvStatus.setText(EmoticonsHelper.getSmiledText(layoutInflater.getContext(), "Hey"));
        }else{
            viewHolder.tvStatus.setText(EmoticonsHelper.getSmiledText(layoutInflater.getContext(), userDetails.getLastMessage()));
        }

        key =userDetails.getUserId() + Constants.Pref.USER_LAST_MSG_STATUS;

        /*if (sharedPreferences.getInt(key, Constants.READ_MSG) == Constants.READ_MSG){
            viewHolder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.double_right,0,0,0);
        }else{
            viewHolder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_play,0,0,0);
        }*/

        return convertView;
    }

    private class ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvStatus;
    }
}
