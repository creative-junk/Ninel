package com.crysoft.me.pichat.fragments;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.View.OnClickListener;

import com.crysoft.me.pichat.helpers.Constants.Pref;


public abstract class BaseRegisterFragment extends BaseFragment implements OnClickListener {
    private static final String PREF_PHONE_NO = "phone_no";

    protected void storePhoneNumber(String phoneNumber){
        if (getActivity()==null){
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Editor editor = sharedPreferences.edit();
        editor.putString(PREF_PHONE_NO,phoneNumber);
        editor.commit();
    }

    protected String getPhoneNumber(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPreferences.getString(PREF_PHONE_NO,null);
    }
    protected void setStepAsComplete(int step){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Editor editor = sharedPreferences.edit();

        switch (step){
            case 1:
                editor.putBoolean(Pref.REGISTER_STEP_ONE,true);
                break;
            case 2:
                editor.putBoolean(Pref.REGISTER_STEP_TWO,true);
                break;
            case 3:
                editor.putBoolean(Pref.REGISTER_STEP_THREE,true);
                break;
        }
        editor.commit();
    }



}
