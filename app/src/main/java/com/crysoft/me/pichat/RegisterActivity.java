package com.crysoft.me.pichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crysoft.me.pichat.helpers.MyPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterActivity extends FragmentActivity {

    private static final String TAG ="RegisterActivity";

    public String regId;
    public MyPreferences preferences;

    private GoogleCloudMessaging gcm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferences = new MyPreferences(this);
       // getGCMId();

        if (preferences.isStepThreeDone()){
            startActivity(new Intent(this,RecentChats.class));
            finish();
        }else if (preferences.isStepTwoDone()){
           // addFragment(new Reg)
        }
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
}
