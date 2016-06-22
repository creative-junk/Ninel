package com.crysoft.me.pichat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crysoft.me.pichat.helpers.Authenticator;

/**
 * Created by Maxx on 6/22/2016.
 */
public class AuthenticationService extends Service {
    private final String TAG = "AuthenticationService";

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        if (android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE)) {
            android.util.Log.v(TAG, "SyncAdapter Authentication Service started.");
        }
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public void onDestroy() {
        if (android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE)) {
            Log.v(TAG, "SyncAdapter Authentication Service stopped.");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "getBinder()...  returning the AccountAuthenticator binder for intent "
                    + intent);
        }
        return mAuthenticator.getIBinder();
    }
}
