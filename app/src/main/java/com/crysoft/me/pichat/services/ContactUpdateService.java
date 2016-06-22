package com.crysoft.me.pichat.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.crysoft.me.pichat.Network.AHttpResponse;
import com.crysoft.me.pichat.Network.HttpRequest;
import com.crysoft.me.pichat.Network.RequestCallback;
import com.crysoft.me.pichat.Network.Urls;
import com.crysoft.me.pichat.database.DatabaseAdapter;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.helpers.Utilities;
import com.crysoft.me.pichat.models.UserDetails;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxx on 6/20/2016.
 */
public class ContactUpdateService extends Service implements RequestCallback {
    private static final String TAG = "Contacts Update Service";

    private UserDetails myDetails;

    public static final int CONTACT_LIST_UPDATE_SUCCESS = 101;
    public static final int CONTACT_LIST_UPDATE_NOT_SUCCESSFUL = 102;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case CONTACT_LIST_UPDATE_SUCCESS:
                    ContactUpdateService.this.stopSelf();
                    sendSuccessBroadCast();
                    break;
                case  CONTACT_LIST_UPDATE_NOT_SUCCESSFUL:
                    ContactUpdateService.this.stopSelf();
                    break;
                default:
                    break;
            }
        }
    };

    private void sendSuccessBroadCast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent intent = new Intent(Constants.MyActions.CONTACT_LIST_UPDATE);
        intent.putExtra("success",true);
        manager.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myDetails = new MyPreferences(getApplicationContext()).getUserDetails();
        updateContacts();
        return START_STICKY;
    }

    private void updateContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mum = getContactNumbers();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id",myDetails.getUserId() + ""));
                params.add(new BasicNameValuePair("num",mum));

                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());

                if (Utilities.isOnline(getApplicationContext())){
                    try {
                        String responseString = new HttpRequest().postData(Urls.UPDATE_CONTACTS, params);
                        if (responseString != null) {
                            JSONObject jsonObject = new JSONObject(responseString);
                            if (jsonObject.getBoolean("success")){
                                JSONArray array = jsonObject.getJSONArray("details");
                                UserDetails userDetails;

                                databaseAdapter.openForWriting();
                                JSONObject currentObj;
                                for (int i =0; i < array.length(); i++){
                                    userDetails = new UserDetails();
                                    currentObj = array.getJSONObject(i);
                                    userDetails.setUserId(Integer.parseInt(currentObj.getString("id")));
                                    userDetails.setStatus(currentObj.getString("status"));
                                    userDetails.setImage(currentObj.getString("image"));
                                    userDetails.setPhoneCode(currentObj.getString("phone_code"));
                                    userDetails.setPhoneNumber(currentObj.getString("phone"));

                                    databaseAdapter.insertOrUpdateUser(userDetails);
                                }
                                databaseAdapter.close();
                                mHandler.sendEmptyMessage(CONTACT_LIST_UPDATE_SUCCESS);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(CONTACT_LIST_UPDATE_SUCCESS);
                    }
                }
                if (databaseAdapter.isOpen()){
                    databaseAdapter.close();
                }
            }
        }).start();
    }

    private String getContactNumbers() {
        StringBuilder builder = null;
        ContentResolver contentResolver = getContentResolver();
        //Get a Cursor with the Contacts List
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        //Check if the Cursor has records and loop through them
        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                //Get the Record ID, we will use this later in our where clause
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //Get also the display name and store it
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0) {
                    //if the Record has a phone number, let's go to that Table and get it
                    Cursor pCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?", new String[] { id }, null );
                    while(pCursor.moveToNext()){
                        if (builder == null){
                            //get the phone number and store it
                            builder = new StringBuilder(pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replaceAll("[^0-9,+]",""));
                        }else{
                            //if we still have records add them to our String Builder
                            builder.append("," + pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replaceAll("[^0-9,+]",""));
                        }
                    }
                    pCursor.close();
                }
            }
        }
        if (builder == null){
            return null;
        }else{
            return builder.toString();
        }
    }

    @Override
    public void onRequestComplete(AHttpResponse response) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
