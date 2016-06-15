package com.crysoft.me.pichat.Network;

import android.app.Activity;
import android.os.Handler;

import com.crysoft.me.pichat.helpers.helpers;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Maxx on 6/15/2016.
 */
public class AHttpRequest {
    private AHttpResponse response;
    private RequestCallback callback;
    private Activity context;
    private Handler mHandler = new Handler();

    public AHttpRequest() {

    }

    public AHttpRequest(Activity context, boolean showProgress) {
        this(context);
    }

    public AHttpRequest(Activity context) {
        this.context = context;
    }

    public AHttpRequest(Activity context, RequestCallback callback) {
        this(context);
        this.callback = callback;
    }

    public void registerUser(final String code, final String mobile, final String regId, final String timezone) {
        if (helpers.isOnline(context)) {
            helpers.showNoInternetConnection(context);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();

                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("code", code));
                params.add(new BasicNameValuePair("mobile",mobile));
                params.add(new BasicNameValuePair("regId",regId));
                params.add(new BasicNameValuePair("timezone",timezone));
                params.add(new BasicNameValuePair("device_type","0")); //0 because we are on Android

                HttpRequest httpRequest = new HttpRequest();
                try{
                    String responseString = httpRequest.postData(Urls.REGISTER_USER,params);
                    response = new AHttpResponse(responseString,true);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
