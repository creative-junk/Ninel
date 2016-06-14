package com.crysoft.me.pichat.fragments;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.crysoft.me.pichat.R;
import com.crysoft.me.pichat.helpers.helpers;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseRegisterFragment {

    private static final String TAG = "RegisterFragment";
    private static final int PHONE_CODE_INT = 100;

    private EditText etNo;
    private Button btnPhoneCode;

    private ProgressDialog progressDialog;




    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView =  inflater.inflate(R.layout.fragment_register, container, false);
        findViewById(R.id.btn_register).setOnClickListener(this);
        etNo = (EditText) findViewById(R.id.et_no_register);
        btnPhoneCode = (Button) findViewById(R.id.btn_phone_code);
        btnPhoneCode.setOnClickListener(this);

        return contentView;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
    private void onRegisterClick(){
        boolean isValid = true;

        if (etNo.getText().toString().trim().length() == 0){
            etNo.setError("Enter Your No");
            isValid = false;
        }
        //If the number is not valid, no need to continue
        if (!isValid){
            return;
        }
        //No use continuing if there is no internet connection
        if (!helpers.isOnline(getActivity())){
            helpers.showToast("No Internet Connection",getActivity());
            return;
        }
        //is all good, proceed
        progressDialog = ProgressDialog.show(getActivity(), "Loading...", "Please Wait");

      //  AHttpRequest request = new AhttpRequest(getActivity(),this);
    }
    private void onRegisterCodeClicked(){

    }
}
