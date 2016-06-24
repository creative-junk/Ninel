package com.crysoft.me.pichat.fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crysoft.me.pichat.R;
import com.crysoft.me.pichat.RegisterActivity;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyFragment extends BaseRegisterFragment {
    private EditText etVerifyCode;
    private SMSReceiver smsReceiver;

    public VerifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.verify, container, false);



        findViewById(R.id.codeText).setVisibility(View.VISIBLE);

        findViewById(R.id.iv_user_picture).setVisibility(View.GONE);
        etVerifyCode = (EditText) findViewById(R.id.et_display_name);
        etVerifyCode.setHint("Verification Code");
        Button btnVerify = (Button) findViewById(R.id.btn_save);
        btnVerify.setText("Verify");
        btnVerify.setOnClickListener(this);

        TextView tvTitle = (TextView) findViewById(R.id.title_tv);
        tvTitle.setText("SMS Verification");



        return contentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(smsReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SMSReceiver();
        getActivity().registerReceiver(smsReceiver, smsFilter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            //progressDialog = ProgressDialog.show(getActivity(),"Verifying...","Please Wait");
            onVerify();
        }
    }

    private ProgressDialog progressDialog;

    private void onVerify() {
        if (etVerifyCode.getText().toString().trim().length() != 0) {
            if (!Utilities.isOnline(getActivity())){
                Utilities.showNoInternetConnection(getActivity());
                return;
            }

            progressDialog = ProgressDialog.show(getActivity(),"Verifying...","Please Wait");
            /*
            AHttpRequest request = new AHttpRequest(getActivity(),this);
            request.verifyUser(getPhoneNumber(),etVerifyCode.getText().toString().trim(),((RegisterActivity) getActivity()).regId);*/

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            RegisterActivity activity = (RegisterActivity) getActivity();
            activity.addFragment(new RegisterStepThree(), true);
        }else{
            etVerifyCode.setError("Verification Code");
        }

    }
/*
    @Override
    public void onRequestComplete(final AHttpResponse response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (response != null) {
                    if (response.isSuccess){
                        setStepAsComplete(2);
                        ((RegisterActivity) getActivity()).addFragment(new RegisterStepThree(), true);
                    }else{
                        Utilities.showToast(response.message, getActivity());
                    }

                }else{
                    Utilities.showToast("Check the Verification Code again", getActivity());
                }
            }
        });
    }*/
    public class SMSReceiver extends BroadcastReceiver {
        public final String TAG ="SMS Receiver";
        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();


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

                        Log.i(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);
                        /*
                        if (!senderAddress.toLowerCase().contains(Constants.SMS_ORIGIN.toLowerCase())){
                            return;
                        }
                        */
                        String verificationCode = getVerificationCode(message);
                        Log.i(TAG, "OTP received: " + verificationCode);

                        VerifyCode(verificationCode);

                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private void VerifyCode(String verificationCode) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            String phoneNumber = preferences.getString("phoneNumber", "");

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("code",verificationCode);
            query.whereEqualTo("number",phoneNumber);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e==null){

                        setStepAsComplete(2);
                        ((RegisterActivity) getActivity()).addFragment(new RegisterStepThree(),true);
                    }else{

                    }
                }
            });
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
}
