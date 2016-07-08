package com.crysoft.me.pichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crysoft.me.pichat.Network.Urls;
import com.crysoft.me.pichat.adapter.ChatsAdapter;
import com.crysoft.me.pichat.database.DBAdapter;
import com.crysoft.me.pichat.models.MessageModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewChatActivity extends AppCompatActivity {
    private Toolbar mToolBar;

    private DBAdapter dbAdapter;

    private String currentUserId;
    private int recipientId;

    private ListView chatsList;
    private ChatsAdapter chatsAdapter;
    private AQuery aQuery;

    private String chat;
    private String friendName;
    private String friendImage;

    private EditText chatBodyField;

    private ImageView ivImage;
    private ImageView ivAttach;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        //Set up the toolbar
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Hide the Keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dbAdapter = DBAdapter.getInstance(this);

        aQuery = new AQuery(this);

        // Get access to the custom title view
        mTitle = (TextView) mToolBar.findViewById(R.id.toolbar_title);
        ivImage = (ImageView) mToolBar.findViewById(R.id.iv_small_image);
        ivAttach = (ImageView) mToolBar.findViewById(R.id.iv_attach);

        mTitle.setVisibility(View.VISIBLE);
        ivImage.setVisibility(View.VISIBLE);
        ivAttach.setVisibility(View.VISIBLE);


        Intent intent = getIntent();
        recipientId = intent.getIntExtra("friend_id",0);
        friendName = intent.getStringExtra("friend_name");
        friendImage = intent.getStringExtra("friend_image");

        Log.i("Name:",friendName);

        mTitle.setText(friendName);

       if (friendImage==null) {
            aQuery.id(ivImage).background(R.drawable.prof_pic);

        } else {
            aQuery.id(ivImage).image(Urls.BASE_IMAGE + friendImage);
        }

        currentUserId = ParseUser.getCurrentUser().getObjectId();

        chatsList = (ListView) findViewById(R.id.messagesList);
        chatsAdapter = new ChatsAdapter(this);
        chatsList.setAdapter(chatsAdapter);
        populateChatList();

        chatBodyField = (EditText) findViewById(R.id.et_message_body);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfile(v);
            }
        });



    }
    private void viewProfile(View v){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("friend_id", recipientId);
        intent.putExtra("friend_name", friendName);
        intent.putExtra("friend_image", friendImage);
        startActivity(intent);
    }

    private void populateChatList() {
      final String[] usersIds = {currentUserId, String.valueOf(recipientId)};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
        query.whereContainedIn("user_from_id", Arrays.asList(usersIds));
        query.whereContainedIn("user_to_id", Arrays.asList(usersIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, ParseException e) {
                if (e == null) {
                    Log.i("Chat Results", messageList.size() + "Messages received");
                    ArrayList<String> dates = new ArrayList<String>();
                    for (int i = 0; i < messageList.size(); i++) {
                        Message message = new Message();
                        String messageTxt = messageList.get(i).get("message").toString();
                        message.setMessage(messageList.get(i).get("message").toString());
                        message.setUserFromId(messageList.get(i).getString("user_from_id"));

                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
                        String createdAt = df.format(messageList.get(i).getCreatedAt());

                        DateFormat ft = new SimpleDateFormat("dd-MM-yyy");
                        String dateString = ft.format(messageList.get(i).getCreatedAt());
                        if (!dates.contains(dateString)){
                            dates.add(dateString);
                            message.setSeparator(true);
                        }else{
                            message.setSeparator(false);
                        }

                        message.setSentOn(createdAt);
                        message.setMessageDate(messageList.get(i).getCreatedAt());

                        if (messageList.get(i).get("user_from_id").equals(currentUserId)) {
                            message.setUserToId(messageList.get(i).get("user_from_id").toString());
                            message.setUserFromId(currentUserId);
                            chatsAdapter.addChat(message, ChatsAdapter.OUTGOING_CHAT);

                        } else {
                            message.setUserFromId(messageList.get(i).getString("user_from_id"));
                            message.setUserToId(currentUserId);
                            chatsAdapter.addChat(message, ChatsAdapter.INCOMING_CHAT);
                        }
                    }
                }

            }
        });
    }


    private void sendMessage() {
        chat = chatBodyField.getText().toString();
        if (chat.isEmpty()) {
            Toast.makeText(this, "Please Type a Message", Toast.LENGTH_LONG).show();
            return;
        }
        String dateNow=DateFormat.getDateInstance().format(new Date());
        //Create the Parse Message Model
        final Message chatMessage = new Message();
        chatMessage.setUserFromId(currentUserId);
        chatMessage.setUserToId(String.valueOf(recipientId));
        chatMessage.setMessage(chat);
        chatMessage.setSentOn(dateNow);
        chatMessage.setMessageDate(new Date());
        chatMessage.setStatus(ChatsAdapter.QUEUED);
        //Create the Local Message Model
        MessageModel localMessage = new MessageModel();
        localMessage.setUserID(Integer.valueOf(currentUserId));
        localMessage.setFriendId(recipientId);
        localMessage.setMessage(chat);
        localMessage.setTime(new Date().toString());
        localMessage.setMessageStatus(ChatsAdapter.QUEUED);
        dbAdapter.insertOrUpdateMessage(localMessage);

        //Save to Parse in the background
        ParseObject parseMessage = new ParseObject("ParseMessage");
        parseMessage.put("user_from_id", currentUserId);
        parseMessage.put("user_to_id", recipientId);
        parseMessage.put("message", chat);
        parseMessage.put("status",ChatsAdapter.SENT);
        parseMessage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //populateChatList();
            }
        });

        chatsAdapter.addChat(chatMessage, ChatsAdapter.OUTGOING_CHAT);
        chatBodyField.setText("");
    }
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_chat_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
