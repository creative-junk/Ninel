package com.crysoft.me.pichat.adapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Maxx on 6/22/2016.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private Context mContext;

    public SyncAdapter(Context context,boolean autoInitialize){
        super(context,autoInitialize);
        Log.i("Service:","Sync Adapter Created");
        mContext = context;

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i("Service:","Sync Adapter Called");
    }
}
