package com.crysoft.me.pichat.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crysoft.me.pichat.Network.AHttpRequest;
import com.crysoft.me.pichat.Network.AHttpResponse;
import com.crysoft.me.pichat.Network.RequestCallback;
import com.crysoft.me.pichat.R;
import com.crysoft.me.pichat.RegisterActivity;
import com.crysoft.me.pichat.helpers.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyFragment extends BaseRegisterFragment implements RequestCallback {
    private EditText etVerifyCode;

    public VerifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.verify, container, false);

        findViewById(R.id.iv_user_picture).setVisibility(View.GONE);
        etVerifyCode = (EditText) findViewById(R.id.et_display_name);
        etVerifyCode.setHint("Verification Code");
        Button btnVerify = (Button) findViewById(R.id.btn_save);
        btnVerify.setText("Verify");
        btnVerify.setOnClickListener(this);

        TextView tvTitle = (TextView) findViewById(R.id.title_tv);
        tvTitle.setText("Verify");
        return contentView;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
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

            progressDialog = ProgressDialog.show(getActivity(),"Loading...","Please Wait");

            AHttpRequest request = new AHttpRequest(getActivity(),this);
            request.verifyUser(getPhoneNumber(),etVerifyCode.getText().toString().trim(),((RegisterActivity) getActivity()).regId);
        }else{
            etVerifyCode.setError("Verification Code");
        }

    }

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
                        ((RegisterActivity) getActivity()).addFragment(new RegisterStepThree(),true);
                    }else{
                        Utilities.showToast(response.message, getActivity());
                    }

                }else{
                    Utilities.showToast("Check the Verification Code again", getActivity());
                }
            }
        });
    }
}
