package com.crysoft.me.pichat;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crysoft.me.pichat.adapter.FavouriteListAdapter;
import com.crysoft.me.pichat.adapter.PastChatsAdapter;
import com.crysoft.me.pichat.database.DatabaseAdapter;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.models.UserDetails;



import java.util.List;

public class RecentChats extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener {
    private static String TAG ="Recent Chats";

    public static final int USER_ONLINE = 1;
    public static final int USER_OFFLINE = 0;

    private DatabaseAdapter databaseAdapter;
    private List<UserDetails> pastChats, mFavList;
    private ListView lvPastchats, lvFavchats;

    private PastChatsAdapter pastChatsAdapter;
    private FavouriteListAdapter favouriteListAdapter;

    private UserDetails myDetails;
    //Receivers
    private MyReceiver receiver;

    private Dialog mOptionsDialog;

    private LinearLayout llLoading;
    private ProgressBar pBar;
    private TextView tvLoadingMessage;

    private EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);

        myDetails = new MyPreferences(this).getUserDetails();
        databaseAdapter = new DatabaseAdapter(this);
        lvPastchats = (ListView) findViewById(android.R.id.list);

        loadData();
        synchContacts();
        updateGroupDetails();
        registerForContextMenu(lvFavchats);
    }



    private void loadData() {
        databaseAdapter.openForReading();
        //mFavList = databaseAdapter.getAllUsers();
        pastChats = databaseAdapter.getPastChats();
        databaseAdapter.close();
        databaseAdapter.openForWriting();
        databaseAdapter.addAllGroups(pastChats);
        databaseAdapter.close();

        pastChatsAdapter = new PastChatsAdapter(getLayoutInflater(),pastChats,this);
        lvPastchats.setAdapter(pastChatsAdapter);
        lvPastchats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecentChats.this,NewChatActivity.class);
                UserDetails userDetails = pastChats.get(position);
                if (userDetails.getParseUserId() == myDetails.getParseUserId()){
                    return;
                }
                intent.putExtra(Constants.Extra.FRIEND_DETAILS,pastChats.get(position));
                startActivity(intent);
            }
        });
    }
    private void setUpSearch(){
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                favouriteListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void manageActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
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
    public void registerReceivers(){
        receiver = new MyReceiver();

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

        IntentFilter filter = new IntentFilter(Constants.MyActions.CONTACT_LIST_UPDATE);
        manager.registerReceiver(receiver,filter);
        filter = new IntentFilter(Constants.MyActions.MESSAGE_UPDATE);
        manager.registerReceiver(receiver,filter);
        filter = new IntentFilter(Constants.MyActions.SEND_MESSAGE);
        manager.registerReceiver(receiver,filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseAdapter.openForReading();
        pastChats.clear();
        pastChats.addAll(databaseAdapter.getPastChats());
        if (!databaseAdapter.isOpen()){
            databaseAdapter.openForReading();
        }
        databaseAdapter.addAllGroups(pastChats);
        databaseAdapter.close();

        pastChatsAdapter.notifyDataSetChanged();

        if (pastChats.size() == 0){
            findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        }else{
            findViewById(android.R.id.empty).setVisibility(View.GONE);
        }
        setLastSeen(1);
    }

    private void setLastSeen(int i) {
        //We could set up our last seen here but we probably wont. This is too stalkerish for now
    }

    @Override
    public void onClick(View v) {
        if (mOptionsDialog != null && mOptionsDialog.isShowing()){
            mOptionsDialog.dismiss();
        }

        switch (v.getId()){
                    }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NewChatActivity.class);
        intent.putExtra(Constants.Extra.FRIEND_DETAILS, (UserDetails) favouriteListAdapter.getItem(position));
        startActivity(intent);
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null){
                mFavList.clear();
                if (!databaseAdapter.isOpen()){
                    databaseAdapter.openForReading();
                }

                mFavList.addAll(databaseAdapter.getAllUsers());
                favouriteListAdapter = new FavouriteListAdapter(getLayoutInflater(), mFavList, RecentChats.this);
                lvFavchats.setAdapter(favouriteListAdapter);

                favouriteListAdapter.notifyDataSetChanged();
                if (mFavList.size()==0){
                    llLoading.setVisibility(View.VISIBLE);
                    tvLoadingMessage.setText("No Contacts");
                    pBar.setVisibility(View.GONE);
                }else{
                    llLoading.setVisibility(View.GONE);
                }
                pastChats.clear();
                pastChats.addAll(databaseAdapter.getPastChats());
                if (!databaseAdapter.isOpen()){
                    databaseAdapter.openForReading();
                }
                databaseAdapter.addAllGroups(pastChats);
                databaseAdapter.close();
                pastChatsAdapter.notifyDataSetChanged();
            }
            if (pastChatsAdapter != null){
                pastChatsAdapter.notifyDataSetChanged();
            }
            if(favouriteListAdapter != null){
                favouriteListAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        setLastSeen(0);
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Delete Chat");
    }
/*
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AHttpRequest request = new AHttpRequest();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        UserDetails friendDetails = (UserDetails) pastChatsAdapter.getItem(menuInfo.position);

        String response;
        databaseAdapter.openForReading();

        if (friendDetails.getAdminId()!=0){
            databaseAdapter.deleteGroupAndMembers(friendDetails.getUserId());
            request.deleteChat(myDetails.getUserId() + "", + friendDetails.getUserId() + "", 2 + "");
        }else{
            request.deleteChat(myDetails.getUserId() + "",+friendDetails.getUserId() + "", 2 + "");
        }
        databaseAdapter.close();

        Intent intent = new Intent(this, RecentChats.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        return super.onContextItemSelected(item);
    }
    */
}
