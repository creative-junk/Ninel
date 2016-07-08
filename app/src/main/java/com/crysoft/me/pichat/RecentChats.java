package com.crysoft.me.pichat;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crysoft.me.pichat.adapter.FavouriteListAdapter;
import com.crysoft.me.pichat.adapter.PastChatsAdapter;
import com.crysoft.me.pichat.database.DBAdapter;
import com.crysoft.me.pichat.database.DatabaseAdapter;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.models.UserDetails;
import com.crysoft.me.pichat.sync.SyncUtilities;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.List;

public class RecentChats extends AppCompatActivity {
    private static String TAG = "Recent Chats";
    private Toolbar mToolBar;

    private String currentUserId = ParseUser.getCurrentUser().getObjectId();
    private ParseUser currentUser = ParseUser.getCurrentUser();
    private UserDetails userDetails;

    private LayoutInflater layoutInflater;

    public static final int USER_ONLINE = 1;
    public static final int USER_OFFLINE = 0;

    private DBAdapter databaseAdapter;
    private List<UserDetails> recentChats;
    private ListView lvPastchats;

    private PastChatsAdapter pastChatsAdapter;

    private UserDetails myDetails;

    private FloatingActionButton fab;

    private EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);
        //Set Up Toolbar
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentChats.this,Contacts.class);
                startActivity(intent);
            }
        });

        //Get Current User Details
        myDetails = new MyPreferences(this).getUserDetails();
        //Initialize The Database
        databaseAdapter = DBAdapter.getInstance(this);
        //Set up Layout
        lvPastchats = (ListView) findViewById(R.id.chat_list);
        getPastChats();
        //setPastChats();

        //synchContacts();
        //Populate Contact Json and post to server
       SyncUtilities.sendContactsSync(this, "3456", "+254720893982");

        // Add Sync Account
        SyncUtilities.CreateAccount(this, "+254720893982", "1129");

        //updateGroupDetails();
        // registerForContextMenu(lvFavchats);
    }


    private void getPastChats() {
        recentChats = databaseAdapter.getRecentChats();
        pastChatsAdapter = new PastChatsAdapter(getLayoutInflater(),recentChats,this);
        lvPastchats.setAdapter(pastChatsAdapter);
        lvPastchats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecentChats.this,NewChatActivity.class);
                UserDetails userDetails = recentChats.get(position);
                //Cant chat with myself can i?
                if (userDetails.getUserId()==myDetails.getUserId()){
                    return;
                }
                intent.putExtra(Constants.Extra.FRIEND_DETAILS,recentChats.get(position));
                startActivity(intent);
            }
        });
    }

    private void setPastChats() {
        final List<UserDetails> usersList = new ArrayList<UserDetails>();
        layoutInflater = getLayoutInflater();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        //query.whereNotEqualTo("objectId",currentUserId);
        //query.include("objectId");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    Log.i("Retrieved " + userList.size(), "Users");
                    int count = userList.size();
                    for (int i = 0; i < count; i++) {
                        userDetails = new UserDetails();
                        userDetails.setParseUserId(userList.get(i).getObjectId());
                        Log.i("User ID", String.valueOf(userDetails.getParseUserId()));
                        userDetails.setName(userList.get(i).getString("display_name"));
                        userDetails.setImage(userList.get(i).getString("profile_image"));
                        userDetails.setStatus("Hey");
                        usersList.add(userDetails);

                        Log.i("Size is", String.valueOf(userDetails));
                    }
                    if (usersList.size() == 0) {
                        findViewById(R.id.emptyChat).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.emptyChat).setVisibility(View.GONE);
                    }

                    pastChatsAdapter = new PastChatsAdapter(layoutInflater, usersList, getApplicationContext());
                    lvPastchats.setAdapter(pastChatsAdapter);
                    lvPastchats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(RecentChats.this, NewChatActivity.class);
                            UserDetails userDetails = usersList.get(position);
                            if (userDetails.getParseUserId() == myDetails.getParseUserId()) {
                                return;
                            }
                            // Log.i("Friend Id",String.valueOf(userDetails.getParseUserId()));
                            intent.putExtra("friend_id", userDetails.getParseUserId());
                            intent.putExtra("friend_name", userDetails.getName());
                            intent.putExtra("friend_image", userDetails.getImage());
                            startActivity(intent);
                        }
                    });
                } else {
                    e.printStackTrace();
                    Log.i("Status", "Failed");
                }

            }


        });

    }

    private void setUpSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //favouriteListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void synchContacts() {
      /*  Intent intent = new Intent(this, ContactUpdateService.class);
        startService(intent);*/


    }

    private void updateGroupDetails() {
        /*AHttpRequest aHttpRequest = new AHttpRequest(this);
        aHttpRequest.updateAllGroupsDetail(myDetail.getUserId() + ""); */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recent_chats, menu);
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


    @Override
    protected void onResume() {
        super.onResume();
        recentChats.clear();
        recentChats.addAll(databaseAdapter.getRecentChats());
        //add all groups
        pastChatsAdapter.notifyDataSetChanged();
        if (recentChats.size() == 0) {
            findViewById(R.id.emptyChat).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.emptyChat).setVisibility(View.GONE);
        }
        setLastSeen(1);
    }

    private void setLastSeen(int i) {
        //We could set up our last seen here but we probably wont. This is too stalkerish for now
    }


}
