package com.crysoft.me.pichat.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by Maxx on 6/16/2016.
 */
public class Countries {
    public static final String TAG = "Countries";
    private ArrayList<String> countries;
    private String myCountry;
    private Context context;

    public Countries(Context context){
        this.context = context;
        countries = new ArrayList<String>();
        getCountryCodeFromAvailableLocales();
    }

    private void getCountryCodeFromAvailableLocales() {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales){
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)){
                countries.add(country);
                if (country.equals(Locale.getDefault().getDisplayCountry())){
                    myCountry = country;
                }
            }
        }
        sortCountries();
    }

    private void sortCountries() {
        Collections.sort(countries);
    }
    public String getJSONCountryCodeFromFile(int jsonResource){
        try {
            return ReadFiles.readRawFileAsString(context,jsonResource);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public int getCount(){
        return countries.size();
    }

    public ArrayList<String> getCountries() {
        return countries;
    }
    public static boolean isSimPresent(Context context){
        PackageManager pm = context.getPackageManager();
        boolean hasTelephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        return hasTelephony;
    }
    public static String getCurrentCountryCode(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String countryCode = tm.getSimCountryIso();
        return countryCode;
    }
}
