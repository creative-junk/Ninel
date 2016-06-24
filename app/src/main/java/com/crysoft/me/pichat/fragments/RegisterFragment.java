package com.crysoft.me.pichat.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crysoft.me.pichat.CountryCodesActivity;
import com.crysoft.me.pichat.R;
import com.crysoft.me.pichat.RegisterActivity;
import com.crysoft.me.pichat.helpers.Constants.Extra;
import com.crysoft.me.pichat.helpers.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;

import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseRegisterFragment{

    private static final String TAG = "RegisterFragment";
    private static final int PHONE_CODE_INT = 100;

    private EditText etNo;
    private EditText etPassword;
    private Button btnPhoneCode;

    private ProgressDialog progressDialog;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.register, container, false);
        findViewById(R.id.btn_register).setOnClickListener(this);
        etNo = (EditText) findViewById(R.id.et_no_register);
        etPassword = (EditText) findViewById(R.id.password);
        btnPhoneCode = (Button) findViewById(R.id.btn_phone_code);
        btnPhoneCode.setOnClickListener(this);

        return contentView;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_phone_code:
                onRegisterCodeClicked();
                break;
            case R.id.btn_register:
                onRegisterClick();
                break;
            default:
                break;

        }
    }

    private void onRegisterClick() {
        boolean isValid = true;

        if (etNo.getText().toString().trim().length() == 0) {
            etNo.setError("Please Enter Your Phone Number");
            isValid = false;
        }
        if (etPassword.getText().toString().trim().length() == 0) {
            etPassword.setError("Please Enter Your Password");
            isValid = false;
        }
        //If the number is not valid, no need to continue
        if (!isValid) {
            return;
        }
        //No use continuing if there is no internet connection
        if (!Utilities.isOnline(getActivity())) {
            Utilities.showToast("No Internet Connection", getActivity());
            Log.i("NetInfo: ","No connection Should Exit");
            return;
        } else {
            Log.i("NetInfo: ","There is connection Should Continue");
            //is all good, proceed
            progressDialog = ProgressDialog.show(getActivity(), "Registering...", "Please Wait");

            register(btnPhoneCode.getText().toString().trim(),etNo.getText().toString().trim(),etPassword.getText().toString().trim());
        }
    }

    private void register(final String countryCode, final String phoneNumber,final String password) {
         /*
            AHttpRequest request = new AHttpRequest(getActivity(), this);
            request.registerUser(btnPhoneCode.getText().toString().trim(),
                    etNo.getText().toString().trim(), ((RegisterActivity) getActivity()).regId,
                    TimeZone.getDefault().getID());
                    */
        String timezone = TimeZone.getDefault().getID();
        String verificationCode = generateVerificationCode().toString();
        String email = countryCode + phoneNumber;
        String username = countryCode + phoneNumber + "@crysoft.me";

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("timezone",timezone);
        user.put("device_type","0");
        user.put("verification_code",verificationCode);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phoneNumber",phoneNumber);
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (e==null){
                    setStepAsComplete(1);
                    RegisterActivity activity = (RegisterActivity) getActivity();
                    activity.addFragment(new VerifyFragment(), true);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Registration Failed.Please Try again", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    private Integer generateVerificationCode(){
        int random = (int )(Math.random() * 99999 + 1);
        return random;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PHONE_CODE_INT) {
            btnPhoneCode.setText(data.getExtras().getString(Extra.CODES));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void onRegisterCodeClicked() {
        Intent i = new Intent(getActivity(), CountryCodesActivity.class);
        startActivityForResult(i, PHONE_CODE_INT);
    }
/*
    @Override
    public void onRequestComplete(AHttpResponse response) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        if (response != null) {
            if (response.isSuccess) {
                try {
                    storePhoneNumber(response.getRootObject().getString("old_phone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setStepAsComplete(1);
                showUserSuccessDialog(response.message);
                RegisterActivity activity = (RegisterActivity) getActivity();
                activity.addFragment(new VerifyFragment(), true);
            } else {

            }
        }
    }
*/
    private void showUserSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RegisterActivity activity = (RegisterActivity) getActivity();
                activity.addFragment(new VerifyFragment(), true);
            }
        });
    }


}
