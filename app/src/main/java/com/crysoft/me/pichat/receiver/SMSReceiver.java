package com.crysoft.me.pichat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.services.HttpService;

/**
 * Created by Maxx on 6/17/2016.
 */
public class SMSReceiver extends BroadcastReceiver {
    public final String TAG ="SMS Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try{
            if (bundle != null){
                //Protocol Data Unit(pdus) basically SMS
                Object[] pdusObject = (Object[]) bundle.get("pdus");
                for (Object aPdusObject : pdusObject){
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObject);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    if (!senderAddress.toLowerCase().contains(Constants.SMS_ORIGIN.toLowerCase())){
                        return;
                    }

                    String verificationCode = getVerificationCode(message);
                    Log.e(TAG, "OTP received: " + verificationCode);

                    Intent httpIntent = new Intent(context, HttpService.class);
                    httpIntent.putExtra("otp", verificationCode);
                    context.startService(httpIntent);


                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get the OTP from the message Body
     * look for ":" which is the code delimiter
     * @param message
     * @return OTP Code
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Constants.OTP_DELIMITER);

        if (index != -1){
            int start = index + 2;
            int length = 6;
            code = message.substring(start,start + length);
            return code;
        }
        return code;
    }
}
