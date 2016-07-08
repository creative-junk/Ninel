package com.crysoft.me.pichat.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.crysoft.me.pichat.helpers.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static android.telephony.PhoneNumberUtils.formatNumber;
import static android.telephony.PhoneNumberUtils.formatNumberToE164;

/**
 * Created by Maxx on 7/5/2016.
 */
public class SyncUtilities {

    private static final String TAG = "Sync Utilities";

    public static String AUTHORITY = "com.android.contacts";

    public static void CreateAccount(Context context,String user,String password){
        Account account = new Account(user, Constants.ACCOUNT_TYPE);
        AccountManager am = AccountManager.get(context);
        if (am.addAccountExplicitly(account,password,null)){
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME,account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE,account.type);

            ContentResolver.setIsSyncable(account,"com.android.contacts",1);
            ContentResolver.setSyncAutomatically(account,AUTHORITY,true);
            Log.i(TAG,"Account Added Successfully");
        }
    }
    public static void sendContactsSync(final Context context,final String id,final String username){
        if (context!=null && id != null && username !=null){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    new SyncContact(context,id,username).execute();

                }
            });
        }
    }
    public static String getContactsForSync(Context context,String id,String username){
        int size=0;
        int totalRead=0;
        String contactType=null;

        JSONObject parent = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray contactsArray = new JSONArray();
        String json = null;
        ContentResolver contentResolver = context.getContentResolver();
        //Cursor cursor= contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        size = cursor.getCount();
        if (size > 0){
            Log.i("There are ",String.valueOf(size)+" Contacts");
            while (cursor.moveToNext()){
                String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int phoneType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                String countryCodeValue = tm.getNetworkCountryIso();

                //String formattedNumber = phoneNumber.replaceAll("[^0-9]", "");
                String formattedNumber = formatNumberToE164(phoneNumber,"KE");

                JSONObject contactObject = new JSONObject();
                //Put the Contact into a JSON Object
                try{
                    contactObject.put("Name",name);
                    contactObject.put("Number",formattedNumber);
                    contactObject.put("Type",phoneType);
                    contactsArray.put(contactObject);
                }catch (JSONException e){
                    e.printStackTrace();
                }

                Log.i("Contact Details: ",name+" : "+ " : "+formattedNumber+" : "+phoneType);
                //Log.i("Contacts In Array are",jsonArray.toString());
                ++totalRead;
            }
            cursor.close();
        }else {
            Log.i("There are ",String.valueOf(size)+" Contacts");
        }
        try{
            jsonObject.put("cid",id);
            jsonObject.put("user",username);

            jsonObject.put("contacts",contactsArray);
            parent.put("query",jsonObject);

            Log.d("JSON Sync",parent.toString());

            try{
                json = Base64.encodeToString(parent.toString().getBytes("utf-8"),Base64.DEFAULT);
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return json;
    }
}
