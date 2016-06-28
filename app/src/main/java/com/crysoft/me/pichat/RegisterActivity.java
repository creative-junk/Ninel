package com.crysoft.me.pichat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.crysoft.me.pichat.fragments.RegisterFragment;
import com.crysoft.me.pichat.fragments.RegisterStepThree;
import com.crysoft.me.pichat.fragments.VerifyFragment;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.Constants.Pref;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.helpers.Utilities;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG ="RegisterActivity";
    private Toolbar mToolBar;

    public String regId;
    public MyPreferences preferences;



    private GoogleCloudMessaging gcm;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);




        mToolBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolBar);

        preferences = new MyPreferences(this);
       // getGCMId();

        if (preferences.stepThreeIsDone()){
            startActivity(new Intent(this,RecentChats.class));
            finish();
        }else if (preferences.stepTwoIsDone()){
            addFragment(new RegisterStepThree(),true);
        }else if (preferences.stepOneIsDone()){
            addFragment(new VerifyFragment(), true);
        }else{
            addFragment(new RegisterFragment(),true);
        }

        File file = new File(Utilities.getBasePath());
        if (!file.exists()){
            file.mkdir();
        }
    }
    

    public void addFragment(Fragment fragment, boolean animate) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        if (animate){
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_right,R.anim.slide_out_left);

        }
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();

    }

    private void getGCMId() {
        regId = getRegistrationId(this);
        if (regId.length() == 0){
         registerInBackground();
        }
    }

    private void registerInBackground() {
        Void [] params = null;
        new RegisterGCMId().execute(params);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = preferences.getString(Pref.GCM_ID,"");
        if (registrationId.length() == 0){
            return "";
        }
        return registrationId;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class RegisterGCMId extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                regId = gcm.register(Constants.SENDER_ID);
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            if (regId.length() == 0){
                registerInBackground();
            }else{
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                Editor editor = sharedPreferences.edit();
                editor.putString(Pref.GCM_ID,regId);
                editor.commit();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
