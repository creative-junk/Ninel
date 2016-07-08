package com.crysoft.me.pichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Maxx on 6/30/2016.
 */
public class ProfileActivity extends AppCompatActivity {
    private Toolbar mToolBar;

    private String friendName;
    private String friendImage;
    private String recipientId;

    private TextView tvProfileName;
    private TextView tvProfileNumber;
    private TextView tvProfileStatus;
    private ImageView ivProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tvProfileName = (TextView) findViewById(R.id.profile_name);
        tvProfileNumber = (TextView) findViewById(R.id.profile_phone);
        tvProfileStatus = (TextView) findViewById(R.id.profile_status);
        ivProfileImage = (ImageView) findViewById(R.id.profile_picture);


        Intent intent = getIntent();
        recipientId = intent.getStringExtra("friend_id");
        friendName = intent.getStringExtra("friend_name");
        friendImage = intent.getStringExtra("friend_image");

        tvProfileName.setText(friendName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}