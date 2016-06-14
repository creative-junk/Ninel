package com.crysoft.me.pichat.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Maxx on 6/14/2016.
 */
public class MessageModel implements Parcelable {

    public static final int MESSAGE_TYPE_MESSAGE = 1;
    public static final int MESSAGE_TYPE_IMAGE = 2;
    public static final int MESSAGE_TYPE_VIDEO = 3;
    public static final int MESSAGE_TYPE_LOCATION = 4;
    public static final int MESSAGE_TYPE_STICKER = 5;
    public static final int MESSAGE_TYPE_CONTACT = 6;

    //Message Status
    public static final int STATUS_SENDING = 1;
    public static final int STATUS_SENT = 2;
    public static final int STATUS_DELIVERED = 3;
    public static final int STATUS_DOWNLOADING = 4;
    public static final int STATUS_UPLOADING = 5;

    //Membership states
    //1 -   Personal Chat
    //2 -   Group Chat
    //3 -   Message Delivered
    //4 -   Join a group
    //5 -   Leave a group

    public static final int CHAT_PERSONAL = 1;
    public static final int CHAT_GROUP = 2;
    public static final int CHAT_MESSAGE_DELIVERED = 3;
    public static final int CHAT_JOIN_GROUP = 4;
    public static final int CHAT_LEAVE_GROUP = 5;

    private int id, messageId, messageType, userID, friendId, messageStatus, what;

    private String message, time;

    private String displayName;

    public boolean isPersonalMessage = true;

    public MessageModel() {

    }

    public MessageModel(Parcel in) {
        id = in.readInt();
        messageId = in.readInt();
        messageType = in.readInt();
        userID = in.readInt();
        friendId = in.readInt();
        messageStatus = in.readInt();
        what = in.readInt();
        message = in.readString();
        time = in.readString();
        displayName = in.readString();
        isPersonalMessage = in.readInt() == 1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(messageType);
        dest.writeInt(userID);
        dest.writeInt(friendId);
        dest.writeInt(messageStatus);
        dest.writeInt(what);
        dest.writeString(message);
        dest.writeString(time);
        dest.writeString(displayName);
        dest.writeInt(isPersonalMessage ? 1 : 0);
    }

    public static final Parcelable.Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel source) {
            return new MessageModel(source);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "id:" + id + ", messageId : " + messageId + ", messageType: "
                + messageType + ", usedId: " + userID + ", friendId: " + friendId + ", messageStatus: "
                + messageStatus + ", who: " + what + ", message: " + message + ", time: " + time;
    }
}
