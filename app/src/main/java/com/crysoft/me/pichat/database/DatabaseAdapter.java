package com.crysoft.me.pichat.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crysoft.me.pichat.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxx on 6/14/2016.
 */
public class DatabaseAdapter {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NinelV1Database";

    //User Table
    public static final String KEY_ID = "_id";

    public static final String KEY_NAME = "display_name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_STATUS = "status";
    public static final String KEY_LAST_MESSAGE = "last_message";
    public static final String KEY_PHONE_CODE = "phone_code";
    public static final String KEY_PHONE_NO = "phone_no";

    public static final String TABLE_USERS = "n_users";

    public static final String CREATE_USER_MASTER = "CREATE IF NOT EXISTS "
            + TABLE_USERS
            + " ( "
            + KEY_ID
            + " INTEGER NOT NULL,"
            + KEY_NAME
            + " text not null , "
            + KEY_IMAGE
            + " text not null , "
            + KEY_PHONE_CODE
            + " text not null , "
            + KEY_PHONE_NO
            + " text not null , "
            + KEY_STATUS
            + " text not null , "
            + KEY_LAST_MESSAGE
            + " INTEGER " + ");";

    public static final String KEY_FRIEND_ID = "friend_id";
    public static final String KEY_MESSAGE_ID = "message_id";
    //Is it a group or personal chat
    public static final String KEY_WHAT_ID = "what";
    public static final String KEY_MESSAGE_TYPE = "message_type";
    public static final String KEY_MESSAGE_STATUS = "message_status";
    public static final String KEY_DELIVERY_TIME = "delivery_time";
    public static final String KEY_DISPLAY_NAME = "display_name";
    public static final String KEY_MESSAGE = "message";


    //These two would make sense when reading the names but brings confusion as the user will not always be the one sending the messages. Let's keep them here in case we need them during the update
    public static final String KEY_TO_USER_ID = "to_user_id";
    public static final String KEY_FROM_USER_ID = "from_user_id";

    public static final String KEY_TIME = "time";
    public static final String KEY_MESSAGE_DATE = "message_date";

    //This field should be tracking if the message is from a group but we need to consider if we'll put the group messages in the same table as other messages or the same group.
    public static final String KEY_IS_FROM_GROUP ="is_from_group";

    public static final String TABLE_MESSAGE = "n_messages";
    public static final String KEY_USER_ID  = "user_id";

    public static final String CREATE_MESSAGE_MASTER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_MESSAGE
            + " ( "
            + KEY_ID
            + " INTEGER PRIMARY KEY, "
            + KEY_MESSAGE_ID
            + " INTEGER, "
            + KEY_MESSAGE
            + " text not null, "
            + KEY_MESSAGE_TYPE
            + " INTEGER NOT NULL, "
            + KEY_USER_ID
            + " INTEGER NOT NULL, "
            + KEY_FRIEND_ID
            + " INTEGER NOT NULL"
            + KEY_WHAT_ID
            + " INTEGER NOT NULL, "
            + KEY_TIME
            + " TEXT NOT NULL, "
            + KEY_DELIVERY_TIME
            + " TEXT NOT NULL, "
            + KEY_MESSAGE_STATUS
            + " INTEGER NOT NULL, "
            + KEY_DISPLAY_NAME
            + " text, "
            + KEY_MESSAGE_DATE
            + "text ); ";

    public static final String KEY_GROUP_ID = "group_id";
    public static final String KEY_GROUP_ADMIN_ID ="admin_id";
    public static final String KEY_GROUP_IMAGE = "group_image";
    public static final String KEY_GROUP_NAME = "group_name";
    public static final String KEY_CREATED_TIME = "created_time";

    public static final String TABLE_GROUPS = "n_groups";

    public static final String CREATE_GROUP_MASTER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_GROUPS
            + " ( "
            + KEY_GROUP_ID
            + "INTEGER PRIMARY KEY, "
            + KEY_GROUP_NAME
            + " text not null, "
            + KEY_GROUP_IMAGE
            + " text not null, "
            + KEY_GROUP_ADMIN_ID
            + " INTEGER NOT NULL, "
            + KEY_CREATED_TIME
            + "text NOT NULL, "
            + KEY_STATUS
            + " text NOT NULL"
            + KEY_LAST_MESSAGE
            + "INTEGER);";

    public int insertOrUpdateGroup(UserDetails userDetails){
        int returnValue = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_GROUP_NAME, userDetails.getName());
        contentValues.put(KEY_GROUP_IMAGE, userDetails.getImage());
        contentValues.put(KEY_GROUP_ADMIN_ID, userDetails.getAdminId());
        contentValues.put(KEY_STATUS,"Tap to View Group details");

        if (getGroupDetail(userDetails.getUserId())== null){
            contentValues.put(KEY_GROUP_ID,userDetails.getUserId());
            contentValues.put(KEY_CREATED_TIME, System.currentTimeMillis());
            returnValue = (int) db.insert(TABLE_GROUPS,null,contentValues);
        }else{
            returnValue = db.update(TABLE_GROUPS, contentValues, KEY_GROUP_ID + "=" + userDetails.getUserId(),null);
        }
        return returnValue;
    }
    public UserDetails getGroupDetail(int groupId){
        if (db==null || !db.isOpen()){
            openForReading();
        }
        Cursor cursor = db.query(TABLE_GROUPS, new String[]{
                KEY_GROUP_ID,KEY_GROUP_NAME,KEY_GROUP_IMAGE,KEY_GROUP_ADMIN_ID,
                KEY_LAST_MESSAGE }, KEY_GROUP_ID + "=" + groupId, null, null,null,null);
        if (cursor != null && cursor.moveToFirst()){
            UserDetails userDetails = new UserDetails();
            userDetails.setUserId(cursor.getInt(0));
            userDetails.setName(cursor.getString(1));
            userDetails.setImage(cursor.getString(2));
            userDetails.setAdminId(cursor.getInt(3));
            userDetails.setLastMessage(cursor.getString(4));
            userDetails.setIsAGroup(true);
            cursor.close();
            return userDetails;

        }
        return null;
    }
    public void addAllGroups(List<UserDetails> list){
        Cursor cursor = db.query(TABLE_GROUPS, new String[]{
                KEY_GROUP_ID,KEY_GROUP_NAME,KEY_GROUP_IMAGE,KEY_GROUP_ADMIN_ID,
                KEY_LAST_MESSAGE },null, null, null,null,null);
        if (cursor != null & cursor.moveToFirst()){
            do {
                UserDetails userDetails = new UserDetails();
                userDetails.setUserId(cursor.getInt(0));
                userDetails.setName(cursor.getString(1));
                userDetails.setImage(cursor.getString(2));
                userDetails.setAdminId(cursor.getInt(3));
                userDetails.setLastMessage(cursor.getString(4));
                userDetails.setIsAGroup(true);
                if (userDetails.getMessageId() != 0){
                    setUserMessage(userDetails);
                }
                list.add(userDetails);
            }while (cursor.moveToNext());
        }
    }

    public static final String TABLE_GROUP_MEMBERS = "group_members";

    public static  final String CREATE_GROUP_USERS = "CREATE IF NOT EXISTS "
            + TABLE_GROUP_MEMBERS
            + " ( "
            + KEY_ID
            + "INTEGER PRIMARY KEY, "
            + KEY_GROUP_ID
            + "INTEGER NOT NULL, "
            + KEY_USER_ID
            + " INTEGER NOT NULL, "
            + KEY_NAME
            + " TEXT NOT NULL, "
            + KEY_PHONE_CODE
            + "TEXT NOT NULL, "
            + KEY_PHONE_NO
            + "TEXT NOT NULL, "
            + KEY_IMAGE
            + "TEXT NOT NULL, "
            + KEY_STATUS
            + "TEXT_NOT NULL, "
            + " );";
    public ArrayList<UserDetails> getGroupMembers(int groupId){
        ArrayList<UserDetails> list = new ArrayList<UserDetails>();
        if (db==null || !db.isOpen()){
            openForReading();
        }
        Cursor cursor = db.query(TABLE_GROUP_MEMBERS, new String[]{
                KEY_GROUP_ID, KEY_USER_ID,KEY_NAME,KEY_PHONE_CODE,KEY_PHONE_NO,
                KEY_IMAGE,KEY_STATUS }, KEY_GROUP_ID + "=" + groupId,null,null,null,null);
        if (cursor == null || !cursor.moveToFirst()){
            return list;
        }
        UserDetails userDetails;
        do {
            userDetails = new UserDetails();
            userDetails.setIsAGroup(false);
            userDetails.setUserId(cursor.getInt(1));
            userDetails.setName(cursor.getString(2));
            userDetails.setPhoneCode(cursor.getString(3));
            userDetails.setPhoneNumber(cursor.getString(4));
            userDetails.setImage(cursor.getString(5));
            userDetails.setStatus(cursor.getString(6));
            list.add(userDetails);
        }while (cursor.moveToNext());

        return list;

    }

    public class DatabaseHelper {

    }
}
