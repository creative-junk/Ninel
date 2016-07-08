package com.crysoft.me.pichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crysoft.me.pichat.adapter.PastChatsAdapter;
import com.crysoft.me.pichat.database.DBAdapter;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {
    private UserDetails myDetails;
    private DBAdapter dbAdapter;
    private PastChatsAdapter pastChatsAdapter;

    private ListView lvContactsList;
    private List<UserDetails> contactsList;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        //Get Current User Details
        myDetails = new MyPreferences(this).getUserDetails();
        //Initialize The Database
        dbAdapter = DBAdapter.getInstance(this);
        //Set up Layout
        lvContactsList = (ListView) findViewById(R.id.contacts_list);
        getContacts();
    }
    private void getContacts() {
        contactsList = dbAdapter.getAllContacts();
        pastChatsAdapter = new PastChatsAdapter(getLayoutInflater(),contactsList,this);
        lvContactsList.setAdapter(pastChatsAdapter);
        lvContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Contacts.this,NewChatActivity.class);
                UserDetails userDetails = contactsList.get(position);
                //Cant chat with myself can i?
                if (userDetails.getUserId()==myDetails.getUserId()){
                    return;
                }
                intent.putExtra("friend_id", userDetails.getParseUserId());
                intent.putExtra("friend_name", userDetails.getName());
                intent.putExtra("friend_image", userDetails.getImage());
                startActivity(intent);
            }
        });
    }

}
