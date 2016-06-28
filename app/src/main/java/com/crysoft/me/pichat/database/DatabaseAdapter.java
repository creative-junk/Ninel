package com.crysoft.me.pichat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.models.MessageModel;
import com.crysoft.me.pichat.models.StickerItem;
import com.crysoft.me.pichat.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static com.crysoft.me.pichat.models.MessageModel.MESSAGE_TYPE_CONTACT;
import static com.crysoft.me.pichat.models.MessageModel.MESSAGE_TYPE_IMAGE;
import static com.crysoft.me.pichat.models.MessageModel.MESSAGE_TYPE_LOCATION;
import static com.crysoft.me.pichat.models.MessageModel.MESSAGE_TYPE_MESSAGE;
import static com.crysoft.me.pichat.models.MessageModel.MESSAGE_TYPE_STICKER;
import static com.crysoft.me.pichat.models.MessageModel.MESSAGE_TYPE_VIDEO;

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

    public static final String CREATE_TABLE_USERS = "CREATE IF NOT EXISTS "
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

    public static final String KEY_TIME = "time";
    public static final String KEY_MESSAGE_DATE = "message_date";

    public static final String TABLE_MESSAGES = "n_messages";
    public static final String KEY_USER_ID = "user_id";

    public static final String CREATE_TABLE_MESSAGES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_MESSAGES
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
    public static final String KEY_GROUP_ADMIN_ID = "admin_id";
    public static final String KEY_GROUP_IMAGE = "group_image";
    public static final String KEY_GROUP_NAME = "group_name";
    public static final String KEY_CREATED_TIME = "created_time";

    public static final String TABLE_GROUPS = "n_groups";

    public static final String CREATE_TABLE_GROUPS = "CREATE TABLE IF NOT EXISTS "
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

    public int insertOrUpdateGroup(UserDetails userDetails) {
        int returnValue = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_GROUP_NAME, userDetails.getName());
        contentValues.put(KEY_GROUP_IMAGE, userDetails.getImage());
        contentValues.put(KEY_GROUP_ADMIN_ID, userDetails.getAdminId());
        contentValues.put(KEY_STATUS, "Tap to View Group details");

        if (getGroupDetail(userDetails.getUserId()) == null) {
            contentValues.put(KEY_GROUP_ID, userDetails.getUserId());
            contentValues.put(KEY_CREATED_TIME, System.currentTimeMillis());
            returnValue = (int) db.insert(TABLE_GROUPS, null, contentValues);
        } else {
            returnValue = db.update(TABLE_GROUPS, contentValues, KEY_GROUP_ID + "=" + userDetails.getUserId(), null);
        }
        return returnValue;
    }

    public UserDetails getGroupDetail(int groupId) {
        if (db == null || !db.isOpen()) {
            openForReading();
        }
        Cursor cursor = db.query(TABLE_GROUPS, new String[]{
                KEY_GROUP_ID, KEY_GROUP_NAME, KEY_GROUP_IMAGE, KEY_GROUP_ADMIN_ID,
                KEY_LAST_MESSAGE}, KEY_GROUP_ID + "=" + groupId, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
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


    public void addAllGroups(List<UserDetails> list) {
        Cursor cursor = db.query(TABLE_GROUPS, new String[]{
                KEY_GROUP_ID, KEY_GROUP_NAME, KEY_GROUP_IMAGE, KEY_GROUP_ADMIN_ID,
                KEY_LAST_MESSAGE}, null, null, null, null, null);
        if (cursor != null & cursor.moveToFirst()) {
            do {
                UserDetails userDetails = new UserDetails();
                userDetails.setUserId(cursor.getInt(0));
                userDetails.setName(cursor.getString(1));
                userDetails.setImage(cursor.getString(2));
                userDetails.setAdminId(cursor.getInt(3));
                userDetails.setLastMessage(cursor.getString(4));
                userDetails.setIsAGroup(true);
                if (userDetails.getMessageId() != 0) {
                    setUserMessage(userDetails);
                }
                list.add(userDetails);
            } while (cursor.moveToNext());
        }
    }


    public static final String TABLE_GROUP_MEMBERS = "n_group_members";

    public static final String CREATE_TABLE_GROUP_MEMBERS = "CREATE IF NOT EXISTS "
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

    public ArrayList<UserDetails> getGroupMembers(int groupId) {
        ArrayList<UserDetails> list = new ArrayList<UserDetails>();
        if (db == null || !db.isOpen()) {
            openForReading();
        }
        Cursor cursor = db.query(TABLE_GROUP_MEMBERS, new String[]{
                KEY_GROUP_ID, KEY_USER_ID, KEY_NAME, KEY_PHONE_CODE, KEY_PHONE_NO,
                KEY_IMAGE, KEY_STATUS}, KEY_GROUP_ID + "=" + groupId, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
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
        } while (cursor.moveToNext());

        return list;

    }

    public void deleteUserFromGroup(int userId, int groupId) {
        int i = db.delete(TABLE_USERS, KEY_GROUP_ID + "=" + groupId + " and " + KEY_USER_ID + "=" + userId, null);
    }

    public void deleteAllGroupUsers(int groupId) {
        if (db == null || !db.isOpen()) {
            openForReading();
        }
        int i = db.delete(TABLE_GROUP_MEMBERS, KEY_GROUP_ID + "=" + groupId, null);
    }

    public void deleteGroupAndMembers(int groupId) {
        db.delete(TABLE_GROUPS, KEY_GROUP_ID + "=" + groupId, null);
        deleteAllGroupUsers(groupId);
        int i = db.delete(TABLE_MESSAGES, "((" + KEY_FRIEND_ID + "=" + groupId + ")"
                + " OR " + "(" + KEY_USER_ID + "=" + groupId + ")) AND "
                + KEY_WHAT_ID + "=" + MessageModel.CHAT_GROUP, null);
    }

    public int insertUpdateGroupMember(UserDetails userDetails) {
        int returnValues = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_ID, userDetails.getGroupId());
        values.put(KEY_USER_ID, userDetails.getUserId());
        values.put(KEY_NAME, userDetails.getName());
        values.put(KEY_PHONE_CODE, userDetails.getPhoneCode());
        values.put(KEY_PHONE_NO, userDetails.getPhoneNumber());
        values.put(KEY_IMAGE, userDetails.getImage());
        values.put(KEY_STATUS, userDetails.getStatus());

        if (userIsPresent(userDetails)) {
            returnValues = db.update(TABLE_GROUP_MEMBERS, values, KEY_USER_ID
                    + "=" + userDetails.getUserId() + " and " + KEY_GROUP_ID + "="
                    + userDetails.getGroupId(), null);
        } else {
            returnValues = (int) db.insert(TABLE_GROUP_MEMBERS, null, values);
        }

        return returnValues;

    }

    private boolean userIsPresent(UserDetails userDetails) {
        if (db == null || !db.isOpen()) {
            openForReading();
        }
        Cursor cursor = db.query(TABLE_GROUP_MEMBERS,
                new String[]{KEY_ID}, KEY_USER_ID + "=" + userDetails.getUserId()
                        + " and " + KEY_GROUP_ID + "=" + userDetails.getGroupId(),
                null, null, null, null);
        if (cursor == null && cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public static final String TABLE_STICKERS = "n_stickers";
    public static final String IMAGE = "image";
    public static final String CATEGORY = "category";
    public static final String EXT = "ext";
    public static final String TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_STICKERS = "CREATE TABLE IF NOT EXISTS"
            + TABLE_STICKERS + "(" + KEY_ID + " INTEGER, " + IMAGE + " TEXT, "
            + EXT + " TEXT, " + CATEGORY + " TEXT, " + TIMESTAMP + " TEXT"
            + " );";
    private static final String KEY_STATUS_TEXT = "status_text";
    private static final String KEY_STATUS_ID = "_id";
    private static final String TABLE_STATUS = "n_status";

    private static final String CREATE_TABLE_STATUS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STATUS
            + "("
            + KEY_STATUS_ID
            + " INTEGER PRIMARY KEY, "
            + KEY_STATUS
            + " TEXT UNIQUE);";

    private Context context;

    public DatabaseAdapter(Context ctx) {
        databaseHelper = new DatabaseHelper(ctx);
        context = ctx;
    }


    public class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_STATUS);
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_MESSAGES);
            db.execSQL(CREATE_TABLE_GROUPS);
            db.execSQL(CREATE_TABLE_GROUP_MEMBERS);
            db.execSQL(CREATE_TABLE_STICKERS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS titles ");
            onCreate(db);
        }
    }


    public DatabaseAdapter openForReading() {
        try {
            db = databaseHelper.getWritableDatabase();
        } catch (SQLiteException sql) {
            sql.printStackTrace();
        }
        return this;
    }

    public DatabaseAdapter openForWriting() {
        try {
            db = databaseHelper.getWritableDatabase();
        } catch (SQLiteException sql) {
            sql.printStackTrace();
        }
        return this;
    }

    public boolean isCreated() {
        if (db != null) {
            return db.isOpen();
        }
        return false;
    }

    public boolean isOpen() {
        if (db != null) {
            return db.isOpen();
        }
        return false;
    }

    public void close() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        if (db != null) {
            db.close();
        }
    }

    public List<UserDetails> getPastChats() {
        List<UserDetails> list = new ArrayList<UserDetails>();
        if (!isOpen()) {
            openForReading();
        }
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                KEY_NAME, KEY_IMAGE, KEY_PHONE_CODE, KEY_PHONE_NO, KEY_STATUS,
                KEY_LAST_MESSAGE}, KEY_LAST_MESSAGE + "!=0", null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();
            UserDetails userDetails;
            for (int i = 0; i < count; i++) {
                userDetails = new UserDetails();
                userDetails.setUserId(cursor.getInt(0));
                userDetails.setName(cursor.getString(1));
                userDetails.setImage(cursor.getString(2));
                userDetails.setPhoneCode(cursor.getString(3));
                userDetails.setPhoneNumber(cursor.getString(4));
                userDetails.setStatus(cursor.getString(5));
                userDetails.setMessageId(cursor.getInt(6));
                setUserMessage(userDetails);
                list.add(userDetails);
                if (!cursor.moveToNext()) {
                    break;
                }
            }
            cursor.close();
        }
        return list;
    }

    private void setUserMessage(UserDetails userDetails) {
        if (!isOpen()) {
            openForWriting();
        }

        Cursor cursor = db.query(TABLE_MESSAGES, new String[]{
                KEY_MESSAGE, KEY_TIME, KEY_USER_ID, KEY_MESSAGE_TYPE,
                KEY_FRIEND_ID}, KEY_ID
                + "="
                + userDetails.getMessageId()
                + " and "
                + KEY_WHAT_ID
                + "="
                + (userDetails.isAGroup() ? MessageModel.CHAT_GROUP : MessageModel.CHAT_PERSONAL), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int type = cursor.getInt(3);
            switch (type) {
                case MESSAGE_TYPE_MESSAGE:
                    userDetails.setLastMessage(cursor.getString(0));
                    break;
                case MESSAGE_TYPE_IMAGE:
                    userDetails.setLastMessage("image");
                    break;
                case MESSAGE_TYPE_VIDEO:
                    userDetails.setLastMessage("video");
                    break;
                case MESSAGE_TYPE_LOCATION:
                    userDetails.setLastMessage("location");
                    break;
                case MESSAGE_TYPE_STICKER:
                    userDetails.setLastMessage("sticker");
                    break;
                case MESSAGE_TYPE_CONTACT:
                    userDetails.setLastMessage("contact");
                    break;
            }

            userDetails.setMessageTime(cursor.getString(1));

            UserDetails myDetails = new MyPreferences(context).getUserDetails();

            int id = cursor.getInt(2);

            if (myDetails.getUserId() == id) {
                userDetails.setUserId(cursor.getInt(4));
            } else {
                userDetails.setUserId(id);
            }
            cursor.close();
            close();
        }
    }

    public List<UserDetails> getAllUsers() {
        List<UserDetails> list = new ArrayList<UserDetails>();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                KEY_NAME, KEY_IMAGE, KEY_PHONE_CODE, KEY_PHONE_NO, KEY_STATUS,
                KEY_LAST_MESSAGE}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            UserDetails userDetails;
            do {
                userDetails = new UserDetails();
                userDetails.setUserId(cursor.getInt(0));
                userDetails.setName(cursor.getString(1));
                userDetails.setImage(cursor.getString(2));
                userDetails.setPhoneCode(cursor.getString(3));
                userDetails.setPhoneNumber(cursor.getString(4));
                userDetails.setStatus(cursor.getString(5));
                userDetails.setMessageId(cursor.getInt(6));

                list.add(userDetails);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public UserDetails getUserDetails(int userId) {
        return null;
    }

    public ArrayList<MessageModel> getMessages(int userID, int friendId, int what) {

        ArrayList<MessageModel> list = new ArrayList<MessageModel>();
        Cursor mCursor;

        String[] columns = new String[]{KEY_ID, KEY_MESSAGE, KEY_MESSAGE_TYPE,
                KEY_USER_ID, KEY_FRIEND_ID, KEY_WHAT_ID, KEY_TIME,
                KEY_MESSAGE_STATUS, KEY_DELIVERY_TIME, KEY_DISPLAY_NAME};
        if (what == MessageModel.CHAT_GROUP) {
            mCursor = db.query(TABLE_MESSAGES, columns, "(("
                    + KEY_FRIEND_ID + "=" + friendId + ")" + " OR " + "("
                    + KEY_USER_ID + "=" + friendId + ")) AND " + KEY_WHAT_ID
                    + "=" + what, null, null, null, null);
        } else {
            mCursor = db.query(TABLE_MESSAGES, columns, "((("
                    + KEY_FRIEND_ID + "=" + friendId + ")" + " OR " + "("
                    + KEY_USER_ID + "=" + friendId + " )) AND " + KEY_WHAT_ID
                    + "=" + what + ")", null, null, null, null);
        }

        String date;

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                MessageModel model;
                do {
                    model = new MessageModel();
                    model.setMessageId(mCursor.getInt(0));
                    model.setMessage(mCursor.getString(1));
                    model.setMessageType(mCursor.getInt(2));
                    model.setUserID(mCursor.getInt(3));
                    model.setFriendId(mCursor.getInt(4));
                    model.setWhat(mCursor.getInt(5));
                    model.setTime(mCursor.getString(6));
                    model.setMessageStatus(mCursor.getInt(7));
                    model.setDisplayName(mCursor.getString(9));
                    list.add(model);

                } while (mCursor.moveToNext());

            }
            mCursor.close();
        }
        return list;
    }

    public Cursor getMessagesCursor(int userID, int friendId, int what) {
        Cursor mCursor;
        if (what == MessageModel.CHAT_GROUP) {
            mCursor = db.query(TABLE_MESSAGES, new String[]{}, "(("
                    + KEY_FRIEND_ID + "=" + friendId + ")" + "OR" + "("
                    + KEY_USER_ID + "=" + friendId + ")) AND " + KEY_WHAT_ID
                    + "=" + what, null, null, null, null);
        } else {
            mCursor = db.query(TABLE_MESSAGES, new String[]{}, "(("
                    + KEY_USER_ID + "=" + userID + " AND " + KEY_FRIEND_ID
                    + "=" + friendId + ")" + " OR " + "(" + KEY_USER_ID + "="
                    + friendId + " AND " + KEY_FRIEND_ID + "=" + userID
                    + " )) AND " + KEY_WHAT_ID + "=" + what, null, null, null, null);
        }
        return mCursor;
    }

    public int insertOrUpdateMessage(MessageModel model) {
        int i = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, model.getMessage());
        values.put(KEY_MESSAGE_ID, model.getMessageId());
        values.put(KEY_MESSAGE_TYPE, model.getMessageType());
        values.put(KEY_USER_ID, model.getUserID());
        values.put(KEY_FRIEND_ID, model.getFriendId());
        values.put(KEY_TIME, model.getTime());
        values.put(KEY_MESSAGE_STATUS, model.getMessageStatus());
        values.put(KEY_DELIVERY_TIME, model.getTime());
        values.put(KEY_WHAT_ID, model.getWhat());
        values.put(KEY_DISPLAY_NAME, model.getDisplayName());

        i = (int) db.insert(TABLE_MESSAGES, null, values);
        return i;
    }

    public int updateLastMessage(int messageId, int userId) {
        int i = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_LAST_MESSAGE, messageId);
        i = db.update(TABLE_USERS, values, KEY_ID + "=" + userId, null);
        return i;
    }

    public int insertOrUpdateUser(UserDetails userDetails) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, userDetails.getUserId());
        values.put(KEY_NAME, userDetails.getName());
        values.put(KEY_IMAGE, userDetails.getImage());
        values.put(KEY_PHONE_CODE, userDetails.getPhoneCode());
        values.put(KEY_PHONE_NO, userDetails.getPhoneNumber());
        values.put(KEY_STATUS, userDetails.getStatus());

        int i = 0;

        i = db.update(TABLE_USERS, values, KEY_ID + "=" + userDetails.getUserId(), null);

        if (i == 0) {
            i = (int) db.insert(TABLE_USERS, null, values);
        }

        return i;
    }

    public int insertStatus(String status) {
        int i = 0;
        openForReading();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS_TEXT, status);
        try {
            i = (int) db.insert(TABLE_STATUS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
        return i;
    }

    public int deleteStatus(String status) {
        int i = 0;
        openForReading();
        try {
            i = (int) db.delete(TABLE_STATUS, KEY_STATUS_TEXT + "='" + status + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public ArrayList<String> getStatusList() {
        ArrayList<String> list = new ArrayList<String>();

        openForReading();

        try {
            Cursor cursor = db.query(TABLE_STATUS, new String[]{KEY_STATUS_TEXT}, null, null, null, null, KEY_ID);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
        return list;

    }

    public List<StickerItem> getAllDetails() {
        List<StickerItem> list = new ArrayList<StickerItem>();

        openForReading();
        Cursor cursor = db.query(TABLE_STICKERS, new String[]{}, null, null, null, null, TIMESTAMP + " DESC ");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                StickerItem details = new StickerItem();
                details.setId(cursor.getString(0));
                details.setImage(cursor.getString(1));
                details.setExtension(cursor.getString(2));
                details.setCategory(cursor.getString(3));
                details.setTime(cursor.getString(4));

                list.add(details);

            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        close();
        return list;
    }

    public void insertSticker(StickerItem details) {
        openForReading();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, details.getId());
        values.put(IMAGE, details.getImage());
        values.put(CATEGORY, details.getCategory());
        values.put(EXT, details.getExtension());
        values.put(TIMESTAMP, details.getTime());

        try {
            db.insert(TABLE_STICKERS, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public StickerItem getStickerDetail(String id) {
        openForReading();
        Cursor cursor = db.query(TABLE_STICKERS, new String[]{KEY_ID, IMAGE, EXT,
                CATEGORY, TIMESTAMP}, IMAGE + "=" + id.split("\\.")[0], null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            StickerItem item = new StickerItem();
            item.setId(cursor.getInt(0) + "");
            item.setImage(cursor.getString(1));
            item.setExtension(cursor.getString(2));
            return item;
        }

        return null;
    }

    public boolean isImagePresent(String id) {
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_STICKERS + " WHERE " + KEY_ID + " = " + id + "";
        openForReading();

        Cursor cursor = null;

        if (db.rawQuery(selectQuery, null) != null) {
            cursor = db.rawQuery(selectQuery, null);
        }

        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        }

        db.close();

        return false;
    }


}
