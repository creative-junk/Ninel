package com.crysoft.me.pichat.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.crysoft.me.pichat.database.DBAdapter;
import com.crysoft.me.pichat.models.UserDetails;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxx on 7/5/2016.
 */
public class SyncContact extends AsyncTask<String,Void,String> {
    Context mContext;
    String usertoken;
    String username,id;
    int type;

    public SyncContact(Context context,String mId,String mUsername){
        this.mContext = context;
        this.id = mId;
        this.username = mUsername;
    }

    @Override
    protected String doInBackground(String... params) {
        String jsonContacts=SyncUtilities.getContactsForSync(mContext,id,username);
        String contacts=null;
        try {
            byte[] data = Base64.decode(jsonContacts, Base64.DEFAULT);
            contacts = new String(data, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        try {
            JSONObject contactsRootObject = new JSONObject(contacts);
            JSONObject contactsObject = contactsRootObject.getJSONObject("query");

            JSONArray contactsArray = contactsObject.optJSONArray("contacts");
            //Log.i("Contacts Root Object",contactsArray.toString());
            ArrayList<String> names = new ArrayList<String>();
            if (contactsArray!=null) {
                for (int i = 0; i < contactsArray.length(); i++) {
                    JSONObject singleContact = contactsArray.getJSONObject(i);
                    final String displayName = singleContact.optString("Name");
                    final String number = singleContact.optString("Number");
                    final String type = singleContact.optString("Type");
                    //names.add(singleContact.optString("Number").toString());
                   // Log.i("Name:", singleContact.optString("Name").toString() + " Number " + singleContact.optString("Number").toString());
                    final DBAdapter db = DBAdapter.getInstance(mContext);
                    ParseQuery<ParseUser> query =ParseUser.getQuery();
                    query.whereEqualTo("username",number);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> userList, ParseException e) {
                            if (e==null){

                                if (userList.size()>0){
                                    for (int i=0;i<userList.size();i++) {
                                        //The user is registered
                                        Log.i("The user", number + " is registered");
                                        UserDetails userDetails = new UserDetails();
                                        userDetails.setName(displayName);
                                        userDetails.setPhoneNumber(number);
                                        userDetails.setPhoneType(type);
                                        if (userList.get(i).get("remote_image")=="") {
                                            userDetails.setImage(userList.get(i).get("profile_image").toString());
                                        }else{
                                            //download Parse file and set it as the profile image
                                        }
                                        db.insertOrUpdateUser(userDetails);
                                    }
                                }else{

                                    //User is probably not registered
                                    Log.i("The user",number+" is not registered");
                                }


                            }else{
                            }
                        }
                    });
                }

            }
        }catch (JSONException e){

        }

        //Here we do the actual sync by sending data to the server, updating the DB the getting the data back and updating the local DB
        Log.i("Synced Contacts",contacts);
        return "";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && result.length() != 0){
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("CONTACTS",result).commit();
            Log.i("Sync Contact","Contacts Sent");
        }
    }
}
