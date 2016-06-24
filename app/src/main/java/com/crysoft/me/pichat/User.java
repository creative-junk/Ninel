package com.crysoft.me.pichat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Random;

/**
 * Created by Maxx on 6/23/2016.
 * Subclass Parse to make it easier to work with a User Object
 */
@ParseClassName("User")
public class User extends ParseObject {

    public User() {
        /* Default empty constructor */
    }

    public String getPhoneCode() {
        return getString("phone_code");
    }

    public void setPhoneCode(String phone_code) {
        put("phone_code", phone_code);
    }

    public String getPhoneNumber() {
        return getString("number");
    }

    public void setNumber(String number) {
        put("number", number);
    }

    public String getUsername() {
        return getString("username");
    }

    public void setUsername(String username) {
        put("username", username);
    }

    public String getStatus() {
        return getString("status");
    }

    public void setStatus(String status) {
        put("status", status);
    }

    public Boolean getIsOnline() {
        return getBoolean("online");
    }

    public void setOnline(boolean online) {
        put("online", online);
    }

    public Boolean getIsConfirmed() {
        return getBoolean("confirmed");
    }

    public void setIsconfirmed(boolean is_confirmed) {
        put("is_confirmed", is_confirmed);
    }

    public String getProfileImage() {
        return getString("profile_image");
    }

    public void setProfileImage(String image) {
        put("profile_image", image);
    }

    public String getTimezone() {
        return getString("timezone");
    }

    public void setTimezone(String timezone) {
        put("timezone", timezone);
    }

    public String getDeviceType() {
        return getString("device_type");
    }

    public void setDeviceType(String deviceType) {
        put("device_type", deviceType);
    }

    public String getCode() {
        return getString("code");
    }
    public void setCode(String code){
        put("code",code);
    }


}
