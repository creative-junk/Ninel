package com.crysoft.me.pichat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crysoft.me.pichat.Message;
import com.crysoft.me.pichat.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Maxx on 6/29/2016.
 */
public class ChatsAdapter extends BaseAdapter {
    public static final int INCOMING_CHAT = 0;
    public static final int OUTGOING_CHAT = 1;
    public static final int QUEUED = 1;
    public static final int SENT = 2;
    public static final int DELIVERED = 3;

    private List<Pair<Message, Integer>> messages;
    private ArrayList<String> dates;
    private LayoutInflater layoutInflater;


    public ChatsAdapter(Activity activity){
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<Message, Integer>>();
        dates= new ArrayList();

    }
    public void addChat(Message message,int direction){
        messages.add(new Pair<Message, Integer>(message,direction));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).second;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int chatDirection = getItemViewType(position);

        //Place the message on the left or right depending on its direction
        if (convertView == null){
            int res = 0;
            if (chatDirection == INCOMING_CHAT){
                res = R.layout.message_right;
            }else if (chatDirection == OUTGOING_CHAT){
                res = R.layout.message_left;
            }
            convertView = layoutInflater.inflate(res,viewGroup,false);
        }

        Message message = messages.get(position).first;
        TextView tvMessage = (TextView) convertView.findViewById(R.id.tv_message);
        tvMessage.setText(message.getMessage());

        TextView tvDateSeparator = (TextView) convertView.findViewById(R.id.dateSeparator);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);

        DateFormat formatDate = new SimpleDateFormat("MMM dd, yyyy");
        String dateToDisplay = formatDate.format(message.getMessageDate());

        if (message.getSeparator()){
            tvDateSeparator.setVisibility(View.VISIBLE);
            if (DateUtils.isToday((message.getMessageDate()).getTime())){
                dateToDisplay = "Today";
            }else if (isYesterday((message.getMessageDate()).getTime())){
                dateToDisplay ="Yesterday";
            }
            tvDateSeparator.setText(dateToDisplay);
        }else{
            tvDateSeparator.setVisibility(View.GONE);
        }

        DateFormat dformat = new SimpleDateFormat("h:mm a");
        String messageDateTxt = dformat.format(message.getMessageDate());
        tvDate.setText(messageDateTxt);


        return convertView;
    }
    public static boolean isYesterday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        now.add(Calendar.DATE,-1);

        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }
}
