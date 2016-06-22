package com.crysoft.me.pichat.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Maxx on 6/17/2016.
 */
public class HttpService extends IntentService{
    public final String TAG = "Http Service";

    public HttpService(){
        super(HttpService.class.getSimpleName());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent!=null){
            String otp = intent.getStringExtra("otp");
            verifyOtp(otp);
        }
    }

    /**
     * Verify Code
     * @param otp
     */
    private void verifyOtp(String otp) {
        //Verify code and move forward


    }
}
