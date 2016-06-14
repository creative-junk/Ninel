package com.crysoft.me.pichat.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.crysoft.me.pichat.helpers.Constants.Pref;

import com.crysoft.me.pichat.models.UserDetails;

/**
 * Created by Maxx on 6/14/2016.
 * This is a helper class to sort us out with Shared Preferences
 *
 */
public class MyPreferences {
    private SharedPreferences sharedPreferences;

    public MyPreferences(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void SaveUserDetails(UserDetails details){
        Editor editor = sharedPreferences.edit();
        editor.putInt(Pref.USER_ID,details.getUserId());
        editor.putString(Pref.USER_NAME, details.getName());
        editor.putString(Pref.USER_IMAGE, details.getImage());
        editor.putString(Pref.USER_PHONE_CODE,details.getPhoneCode());
        editor.putString(Pref.USER_PHONE_NO,details.getPhoneCode());
        editor.putString(Pref.USER_STATUS,details.getStatus());
        editor.commit();
    }
    public UserDetails getUserDetails(){
        UserDetails userDetails = new UserDetails();
        userDetails.setUserId(sharedPreferences.getInt(Pref.USER_ID, -1));
        userDetails.setName(sharedPreferences.getString(Pref.USER_NAME, null));
        userDetails.setImage(sharedPreferences.getString(Pref.USER_IMAGE, null));
        userDetails.setPhoneCode(sharedPreferences.getString(Pref.USER_PHONE_CODE, null));
        userDetails.setPhoneNumber(sharedPreferences.getString(Pref.USER_PHONE_NO, null));
        userDetails.setStatus(sharedPreferences.getString(Pref.USER_STATUS, null));
        return userDetails;
    }
    public boolean isStepOneDone(){
        return sharedPreferences.getBoolean(Pref.REGISTER_STEP_ONE, false);
    }
    public boolean isStepTwoDone(){
        return sharedPreferences.getBoolean(Pref.REGISTER_STEP_TWO, false);
    }
    public boolean isStepThreeDone(){
        return sharedPreferences.getBoolean(Pref.REGISTER_STEP_THREE, false);
    }
}
