package com.crysoft.me.pichat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crysoft.me.pichat.adapter.SyncAdapter;

/**
 * Created by Maxx on 6/22/2016.
 */
public class SyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static SyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        Log.i("Service:", "Sync Service Created");
        synchronized (syncAdapterLock){
            if (syncAdapter==null){
                syncAdapter = new SyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service:", "Sync Service Binder");
        return syncAdapter.getSyncAdapterBinder();
    }
}
