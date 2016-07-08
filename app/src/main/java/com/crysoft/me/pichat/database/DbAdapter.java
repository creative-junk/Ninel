package com.crysoft.me.pichat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.models.MessageModel;
import com.crysoft.me.pichat.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxx on 7/4/2016.
 */
public class DBAdapter extends SQLiteOpenHelper {
    //First let's make this class a singleton to avoid memory leaks and some other scary stuff like unnecessary relocations
    private static DBAdapter sInstance;
    private Context context;
    //Let's tag this b*
    private static final String TAG = "DBAdapter";

    //Database Information
    private static final String DATABASE_NAME = "Ninel";
    private static final int DATABASE_VERSION = 3;
    //Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CHATS = "chats";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_GROUP_MEMBERS = "group_members";

    //USERS table columns
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_USER_DISPLAY_NAME = "display_name";
    private static final String KEY_USER_PROFILE_IMAGE = "profile_image";
    private static final String KEY_USER_STATUS = "status";
    private static final String KEY_USER_LAST_MESSAGE = "last_message";
    private static final String KEY_USER_PHONE_NUMBER = "phone_number";

    //CHATS table columns
    private static final String KEY_CHAT_ID = "_id";
    private static final String KEY_CHAT_USER_ID = "user_id";
    //Foreign Key to the Users Table
    private static final String KEY_CHAT_FRIEND_ID_FK = "friend_id";
    //Is is a group message or Personal Chat
    private static final String KEY_CHAT_WHAT_ID = "what_id";
    //Is is a Text Message,Photo, Video or Location
    private static final String KEY_CHAT_TYPE = "chat_type";
    //Is it local, sent or delivered
    private static final String KEY_CHAT_STATUS = "chat_status";
    //When was the message delivered
    private static final String KEY_CHAT_DELIVERY_TIME = "delivery_time";
    //The actual text of the message
    private static final String KEY_CHAT = "message";
    //the size of the media if it's not a text chat
    private static final String KEY_CHAT_MEDIA_SIZE = "media_size";
    //the location of the downloaded media
    private static final String KEY_CHAT_MEDIA_URL = "media_url";
    //Thumbnail for the media
    private static final String KEY_CHAT_THUMBNAIL = "thumbnail";
    //Sent on Timestamp for messages sent from this user
    private static final String KEY_CHAT_SENT_ON = "sent_on";

    //GROUPS table
    private static final String KEY_GROUP_ID = "_id";
    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_GROUP_IMAGE = "group_image";
    private static final String KEY_GROUP_STATUS = "status";

    //GROUP_MEMBERS table
    private static final String KEY_GROUPMEMBERS_ID = "_id";
    //Which Group is this
    private static final String KEY_GROUPMEMBERS_GROUPID = "group_id";
    //Who is the member
    private static final String KEY_GROUPMEMBERS_USERID = "user_id";
    //Is the member an admin
    private static final String KEY_GROUPMEMBERS_IS_ADMIN = "is_admin";
    //Other Misc User details
    private static final String KEY_GROUPMEMBERS_USERSTATUS = "user_status";
    private static final String KEY_GROUPMEMBERS_USERIMAGE = "user_image";
    private static final String KEY_GROUPMEMBERS_USERNUMBER = "user_number";

    // lets create a method for giving back an instance of this class so there will only ever be one instance at a time. If we have one we just return it,otherwise we create a new one
    public static synchronized DBAdapter getInstance(Context context) {
        //We use the application context so we don't accidentally leak an activities Context. see http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBAdapter(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    //Initialize the Database AND CREATE our tables if needed
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_DISPLAY_NAME + " TEXT," +
                KEY_USER_PHONE_NUMBER + " TEXT," +
                KEY_USER_PROFILE_IMAGE + " TEXT," +
                KEY_USER_STATUS + " TEXT," +
                KEY_USER_LAST_MESSAGE + " INTEGER" +
                ")";
        String CREATE_CHATS_TABLE = "CREATE TABLE " + TABLE_CHATS +
                "(" +
                KEY_CHAT_ID + " INTEGER PRIMARY KEY," +
                KEY_CHAT_USER_ID + " INTEGER NOT NULL," +
                KEY_CHAT_FRIEND_ID_FK + " INTEGER NOT NULL," +
                KEY_CHAT_WHAT_ID + " INTEGER NOT NULL," +
                KEY_CHAT_TYPE + " INTEGER NOT NULL," +
                KEY_CHAT_STATUS + " TEXT," +
                KEY_CHAT_DELIVERY_TIME + " TEXT," +
                KEY_CHAT + " TEXT NOT NULL," +
                KEY_CHAT_MEDIA_SIZE + " TEXT," +
                KEY_CHAT_MEDIA_URL + " TEXT," +
                KEY_CHAT_THUMBNAIL + " TEXT," +
                KEY_CHAT_SENT_ON + " TEXT" +
                ")";
        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS +
                "(" +
                KEY_GROUP_ID + " INTEGER PRIMARY KEY," +
                KEY_GROUP_NAME + " TEXT," +
                KEY_GROUP_IMAGE + " TEXT," +
                KEY_GROUP_STATUS + " TEXT" +
                ")";
        String CREATE_GROUP_MEMBERS_TABLE = "CREATE TABLE " + TABLE_GROUP_MEMBERS +
                "(" +
                KEY_GROUPMEMBERS_ID + " INTEGER PRIMARY KEY," +
                KEY_GROUPMEMBERS_GROUPID + " INTEGER REFERENCES " + TABLE_GROUPS + "," +
                KEY_GROUPMEMBERS_USERID + " INTEGER REFERENCES " + TABLE_USERS + "," +
                KEY_GROUPMEMBERS_IS_ADMIN + " INTEGER," +
                KEY_GROUPMEMBERS_USERNUMBER + " TEXT," +
                KEY_GROUPMEMBERS_USERSTATUS + " TEXT," +
                KEY_GROUPMEMBERS_USERIMAGE + " TEXT" +
                ")";
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_GROUP_MEMBERS_TABLE);
        db.execSQL(CREATE_CHATS_TABLE);


    }

    //If we upgrade, we drop the database for now. Not really sure is this is the best approach but for now let's stick to basic implementations
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_MEMBERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);

        }
    }

    //Let's create CRUD methods for our models
    //USERS
    //InsertOrUpdate User
    public long insertOrUpdateUser(UserDetails userDetails) {
        //We have a cached connection so getting this code below isnt really that expensive
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        //Let's wrap this insert to ensure consistency and enhance DB  performance
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_DISPLAY_NAME, userDetails.getName());
            values.put(KEY_USER_PROFILE_IMAGE, userDetails.getImage());
            values.put(KEY_USER_STATUS, userDetails.getStatus());
            values.put(KEY_USER_LAST_MESSAGE, userDetails.getLastMessage());
            values.put(KEY_USER_PHONE_NUMBER, userDetails.getPhoneNumber());
            //Let's first try to update the user. We use the number because it will always be unique
            int rows = db.update(TABLE_USERS, values, KEY_USER_PHONE_NUMBER + "= ?", new String[]{userDetails.getPhoneNumber()});

            //Let's check if the update worked
            if (rows == 1) {
                //Ok, we have someone. Let's get their Primary Key first
                String userSelectQuery = String.format("SELECT % FROM % WHERE %s=", KEY_USER_ID, TABLE_USERS, KEY_USER_PHONE_NUMBER);
                Cursor cursor = db.rawQuery(userSelectQuery, new String[]{String.valueOf(userDetails.getPhoneNumber())});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                //No such user, do the insert
                userId = db.insertOrThrow(TABLE_USERS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error trying to insert or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    //insert group
    public void addGroup(UserDetails userDetails) {
        //We have a cached connection so getting this code below isnt really that expensive
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        //Let's wrap this insert to ensure consistency and enhance DB  performance
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_GROUP_NAME, userDetails.getName());
            values.put(KEY_GROUP_IMAGE, userDetails.getImage());
            values.put(KEY_GROUP_STATUS, "Tap for Group Details");
            if (getGroupDetails(userDetails.getUserId()) != null) {
                //Group exists, update it
                db.update(TABLE_USERS, values, KEY_GROUP_ID + "=?", new String[]{String.valueOf(userDetails.getUserId())});
            } else {
                //It's a new Group, insert it
                db.insertOrThrow(TABLE_GROUPS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error trying to insert or update group");
        } finally {
            db.endTransaction();
        }


    }

    //Get Group Details
    public UserDetails getGroupDetails(int groupId) {
        SQLiteDatabase db = getWritableDatabase();
        String userSelectQuery = String.format("SELECT % FROM % WHERE %s=", KEY_USER_ID, TABLE_USERS, KEY_GROUP_ID);
        Cursor cursor = db.rawQuery(userSelectQuery, new String[]{String.valueOf(groupId)});
        if (cursor.moveToFirst()) {
            UserDetails userDetail = new UserDetails();
            userDetail.setUserId(cursor.getInt(0));
            userDetail.setName(cursor.getString(1));
            userDetail.setImage(cursor.getString(2));
            //userDetail.setAdminId(cursor.getInt(3));
            userDetail.setLastMessage(cursor.getString(4));
            userDetail.setIsAGroup(true);
            cursor.close();
            return userDetail;

        }
        return null;
    }

    public long insertOrUpdateGroupMember(UserDetails userDetails) {
        //We have a cached connection so getting this code below isnt really that expensive
        SQLiteDatabase db = getWritableDatabase();
        int returnValues = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_GROUPMEMBERS_GROUPID, userDetails.getGroupId());
        values.put(KEY_GROUPMEMBERS_USERID, userDetails.getUserId());
        values.put(KEY_GROUPMEMBERS_USERNUMBER, userDetails.getPhoneNumber());
        values.put(KEY_GROUPMEMBERS_USERSTATUS, userDetails.getStatus());
        values.put(KEY_GROUPMEMBERS_IS_ADMIN, userDetails.isAdmin());
        values.put(KEY_GROUPMEMBERS_USERIMAGE, userDetails.getImage());

        if (isUserPresent(userDetails)) {
            returnValues = db.update(TABLE_GROUP_MEMBERS, values, KEY_GROUPMEMBERS_USERID
                    + "=" + userDetails.getUserId() + " and " + KEY_GROUPMEMBERS_GROUPID + "="
                    + userDetails.getGroupId(), null);
        } else {
            returnValues = (int) db.insert(TABLE_GROUP_MEMBERS, null, values);
        }

        return returnValues;
    }

    public boolean isUserPresent(UserDetails userDetails) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GROUP_MEMBERS,
                new String[]{KEY_GROUPMEMBERS_ID}, KEY_GROUPMEMBERS_USERID + "=" + userDetails.getUserId()
                        + " and " + KEY_GROUPMEMBERS_GROUPID + "=" + userDetails.getGroupId(),
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            return true;
        }

        return false;
    }

    public void deleteUserFromGroup(int userId, int groupId) {
        SQLiteDatabase db = getWritableDatabase();
        int i = db.delete(TABLE_GROUP_MEMBERS, KEY_GROUPMEMBERS_GROUPID + "=" + groupId +
                " and " + KEY_GROUPMEMBERS_USERID + "=" + userId, null);
    }

    public void emptyGroup(int groupId) {
        SQLiteDatabase db = getWritableDatabase();
        int i = db.delete(TABLE_GROUP_MEMBERS, KEY_GROUPMEMBERS_GROUPID + "=" + groupId, null);
    }

    public ArrayList<UserDetails> getGroupMembers(int groupId) {
        ArrayList<UserDetails> groupMembers = new ArrayList<UserDetails>();
        SQLiteDatabase db = getReadableDatabase();
        String GROUP_MEMBERS_QUERY = String.format("SELECT %s FROM % WHERE %s=", new String[]{
                KEY_GROUPMEMBERS_GROUPID, KEY_GROUPMEMBERS_USERID, KEY_GROUPMEMBERS_USERNUMBER, KEY_GROUPMEMBERS_USERSTATUS,
                KEY_GROUPMEMBERS_USERIMAGE, KEY_GROUPMEMBERS_IS_ADMIN}, TABLE_GROUP_MEMBERS, KEY_GROUPMEMBERS_GROUPID);
        Cursor cursor = db.rawQuery(GROUP_MEMBERS_QUERY, new String[]{String.valueOf(groupId)});
        try {
            if (cursor.moveToFirst()) {
                UserDetails userDetails;
                do {
                    userDetails = new UserDetails();
                    userDetails.setIsAGroup(false);
                    userDetails.setUserId(cursor.getInt(1));
                    userDetails.setPhoneNumber(cursor.getString(2));
                    userDetails.setStatus(cursor.getString(3));
                    userDetails.setImage(cursor.getString(4));
                    userDetails.setAdmin(cursor.getInt(5) == 1);
                    groupMembers.add(userDetails);
                } while (cursor.moveToNext());
            } else if (cursor == null || !cursor.moveToFirst()) {
                return groupMembers;
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to get Group Members");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return groupMembers;
    }
    public void exitAndDeleteGroup(int groupId){
        //delete Records of the Group's members
        emptyGroup(groupId);

        SQLiteDatabase db = getWritableDatabase();

        //Delete the Group
        db.delete(TABLE_GROUPS, KEY_GROUP_ID + "=" + groupId, null);

        //Delete Messages from the Group
        int i = db.delete(TABLE_CHATS, "((" + KEY_CHAT_FRIEND_ID_FK + "="
                + groupId + ")" + " OR " + "(" + KEY_USER_ID + "=" + groupId
                + ")) AND " + KEY_CHAT_WHAT_ID + "=" + MessageModel.CHAT_GROUP, null);
    }

    public List<UserDetails> getRecentChats(){
        List<UserDetails> list = new ArrayList<UserDetails>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,new String[]{
                KEY_USER_ID,KEY_USER_DISPLAY_NAME,KEY_USER_PHONE_NUMBER,KEY_USER_PROFILE_IMAGE,KEY_USER_STATUS,KEY_USER_LAST_MESSAGE}, KEY_USER_LAST_MESSAGE + "!=0",null,null,null,null);
        try{
            if (cursor.moveToFirst()){
                do{
                    UserDetails userDetails;

                        userDetails = new UserDetails();
                        userDetails.setUserId(cursor.getInt(0));
                        userDetails.setName(cursor.getString(1));
                        userDetails.setPhoneNumber(cursor.getString(2));
                        userDetails.setImage(cursor.getString(3));
                        userDetails.setStatus(cursor.getString(4));
                        userDetails.setMessageId(cursor.getInt(5));
                        setUserMessage(userDetails);
                        list.add(userDetails);


                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG,"Unable to get Recent Chats");
        }finally {
            if (cursor!=null || !cursor.isClosed()){
                cursor.close();
            }
        }

        return list;
    }
    public void setUserMessage(UserDetails userDetails){
        SQLiteDatabase db= getWritableDatabase();
        Cursor cursor = db.query(TABLE_CHATS, new String[] {
                KEY_CHAT, KEY_CHAT_SENT_ON, KEY_CHAT_USER_ID,KEY_CHAT_FRIEND_ID_FK, KEY_CHAT_TYPE }, KEY_CHAT_ID
                + "="
                + userDetails.getMessageId()
                + " and "
                + KEY_CHAT_WHAT_ID
                + "="
                + (userDetails.isAGroup() ? MessageModel.CHAT_GROUP
                : MessageModel.CHAT_PERSONAL), null, null, null, null);
        if (cursor!=null && cursor.moveToFirst()){
            int type= cursor.getInt(4);
            switch (type){
                case MessageModel.MESSAGE_TYPE_MESSAGE:
                    userDetails.setLastMessage(cursor.getString(0));
                    break;
                case MessageModel.MESSAGE_TYPE_IMAGE:
                    userDetails.setLastMessage("image");
                    break;
                case MessageModel.MESSAGE_TYPE_VIDEO:
                    userDetails.setLastMessage("video");
                    break;
                case MessageModel.MESSAGE_TYPE_LOCATION:
                    userDetails.setLastMessage("location");
                    break;
                case MessageModel.MESSAGE_TYPE_CONTACT:
                    userDetails.setLastMessage("contact");
                    break;
            }
            userDetails.setMessageTime(cursor.getString(1));
            UserDetails myDetails = new MyPreferences(context).getUserDetails();
            int id = cursor.getInt(2);
            if (myDetails.getUserId()==id){
                //Are we the Friend if yes then set the friend as the owner. This is used to cater to Groups
                userDetails.setUserId(cursor.getInt(3));
            }else{
                userDetails.setUserId(id);
            }
            cursor.close();
            close();
        }
    }
    //This displays all users(Contacts in this case)
    public List<UserDetails> getAllContacts(){
        List<UserDetails> list = new ArrayList<UserDetails>();
//        String ALL_USERS_QUERY = String.format("SELECT %s FROM %", new String[]{
//                KEY_USER_ID,KEY_USER_DISPLAY_NAME,KEY_USER_PHONE_NUMBER,KEY_USER_PROFILE_IMAGE,KEY_USER_STATUS,KEY_USER_LAST_MESSAGE}, TABLE_USERS);
//
        //Log.i("Query is",ALL_USERS_QUERY);
        SQLiteDatabase db = getReadableDatabase();
       //Cursor cursor = db.rawQuery(ALL_USERS_QUERY,null);
        Cursor cursor = db.query(TABLE_USERS,new String[]{
                KEY_USER_ID,KEY_USER_DISPLAY_NAME,KEY_USER_PHONE_NUMBER,KEY_USER_PROFILE_IMAGE,KEY_USER_STATUS,KEY_USER_LAST_MESSAGE}, KEY_USER_ID + "!=0",null,null,null,null);


        try{
            if (cursor.moveToFirst()){
                do{
                    UserDetails userDetails;

                    userDetails = new UserDetails();
                    userDetails.setUserId(cursor.getInt(0));
                    userDetails.setName(cursor.getString(1));
                    userDetails.setPhoneNumber(cursor.getString(2));
                    userDetails.setImage(cursor.getString(3));
                    userDetails.setStatus(cursor.getString(4));
                    userDetails.setMessageId(cursor.getInt(5));
                    list.add(userDetails);


                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG,"Unable to get Contact List");
        }finally {
            if (cursor!=null || !cursor.isClosed()){
                cursor.close();
            }
        }

        return list;
    }
    public ArrayList<MessageModel> getChats(int userId,int friendId,int what, int temp){
        ArrayList<MessageModel> list = new ArrayList<MessageModel>();
        Cursor mCursor;
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = new String[]{KEY_CHAT_ID,KEY_CHAT_USER_ID,KEY_CHAT_FRIEND_ID_FK,KEY_CHAT_WHAT_ID,KEY_CHAT_TYPE,KEY_CHAT_STATUS,
                KEY_CHAT_DELIVERY_TIME,KEY_CHAT,KEY_CHAT_MEDIA_SIZE,KEY_CHAT_MEDIA_URL,KEY_CHAT_THUMBNAIL,KEY_CHAT_SENT_ON };
        if (what==MessageModel.CHAT_GROUP){
            mCursor = db.query(TABLE_CHATS,columns,"((" + KEY_CHAT_FRIEND_ID_FK + "="
                    + friendId + ")" + " OR " + "(" + KEY_CHAT_USER_ID + "=" + friendId +
                    ")) AND " + KEY_CHAT_WHAT_ID + "=" + what,null,null,null,null);
        }else{
            mCursor = db.query(TABLE_CHATS,columns,"((" + KEY_CHAT_USER_ID + "=" + userId  + " AND "
                    + KEY_CHAT_FRIEND_ID_FK + "=" + friendId + ") OR " + KEY_CHAT_USER_ID + "=" + friendId + " AND "+ KEY_CHAT_FRIEND_ID_FK + "="+ userId + "))"+
                    KEY_CHAT_WHAT_ID + "="+ what,null,null,null,null);
        }
        if (mCursor!=null){
            if (mCursor.moveToFirst()){
                MessageModel messageModel;
                do {
                    messageModel = new MessageModel();
                    messageModel.setMessageId(mCursor.getInt(0));
                    messageModel.setUserID(mCursor.getInt(1));
                    messageModel.setFriendId(mCursor.getInt(2));
                    messageModel.setWhat(mCursor.getInt(3));
                    messageModel.setMessageType(mCursor.getInt(4));
                    messageModel.setMessageStatus(mCursor.getInt(5));
                    messageModel.setTime(mCursor.getString(6));
                    messageModel.setMessage(mCursor.getString(7));
                    list.add(messageModel);


                }while (mCursor.moveToNext());

            }
            mCursor.close();

        }
        return list;
    }
    public int insertOrUpdateMessage(MessageModel messageModel){
        int i =0;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values= new ContentValues();

        values.put(KEY_CHAT,messageModel.getMessage());
        values.put(KEY_CHAT_ID,messageModel.getMessageId());
        values.put(KEY_CHAT_TYPE,messageModel.getMessageType());
        values.put(KEY_CHAT_USER_ID,messageModel.getUserID());
        values.put(KEY_CHAT_FRIEND_ID_FK,messageModel.getFriendId());
        values.put(KEY_CHAT_SENT_ON,messageModel.getTime());
        values.put(KEY_CHAT_STATUS,messageModel.getMessageStatus());
        values.put(KEY_CHAT_DELIVERY_TIME,messageModel.getTime());
        values.put(KEY_CHAT_WHAT_ID,messageModel.getWhat());

        if (messageModel.getId() != 0){
            i = (int) db.insert(TABLE_CHATS,null,values);
        }else{
            i = db.update(TABLE_CHATS,values,KEY_CHAT_ID + "=" + messageModel.getId(),null);
        }
        return i;
    }
    //Delete Conversation
    public boolean deleteConversation(int userId, int friendId){
            SQLiteDatabase db = getWritableDatabase();

            String delSQLString = "DELETE FROM "+ TABLE_CHATS + " where " + KEY_CHAT_USER_ID + "=" + friendId + " OR " + KEY_CHAT_FRIEND_ID_FK + "=" + friendId;

            updateLastMessageId(0,friendId);

            db.execSQL(delSQLString);
            db.close();
            return false;
    }
    public int updateLastMessageId(int messageId, int userId){
        int i=0;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_LAST_MESSAGE,messageId);
        i = db.update(TABLE_USERS,values,KEY_USER_ID + "=" + userId,null);
        return i;
    }

}
