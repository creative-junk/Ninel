package com.crysoft.me.pichat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.crysoft.me.pichat.Network.Urls;
import com.crysoft.me.pichat.R;
import com.crysoft.me.pichat.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxx on 6/20/2016.
 */
public class FavouriteListAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater layoutInflater;
    private List<UserDetails> list;
    private List<UserDetails> displayList;
    private AQuery aQuery;

    public FavouriteListAdapter(LayoutInflater inflater, List<UserDetails> list, Context context){
        this.list = list;
        displayList = new ArrayList<UserDetails>();
        displayList.addAll(list);
        this.layoutInflater = inflater;
        aQuery = new AQuery(context);
    }

    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public Object getItem(int position) {
        return displayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item_user_list,null);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.iv_user_image);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_username);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserDetails userDetails = displayList.get(position);

        aQuery.id(viewHolder.ivImage).image(Urls.BASE_IMAGE + userDetails.getImage());
        viewHolder.tvName.setText(userDetails.getName());

        return convertView;
    }
    private MyFilter filter;

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new MyFilter();
        }
        return filter;
    }

    private class ViewHolder {
        ImageView ivImage;
        TextView tvName,tvStatus;
    }

    private class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            UserDetails userDetails;
            displayList.clear();

            if (constraint.toString().trim().length() == 0){
                displayList.addAll(list);
            }else{
                for (int i = 0; i < list.size();i++){
                    userDetails = list.get(i);
                    if (userDetails.getName().toLowerCase().trim().contains(constraint.toString().toLowerCase().trim())){
                        displayList.add(userDetails);
                    }
                }
            }
            filterResults.count = displayList.size();
            filterResults.values = filterResults;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
        }
    }
}
