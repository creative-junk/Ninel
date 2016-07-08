package com.crysoft.me.pichat.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Maxx on 6/14/2016.
 */
public class UserDetails implements Parcelable{
    //UserDetails ID & the Message ID
    private int userId, messageId;
    private String parseUserId;
    //The UserDetails Details we interested in
    private String name, image, phoneNumber,phoneCode,status,lastMessage,messageTime,phoneType;

    private boolean isSelected;

    //In case of Groups
    private boolean isAGroup;
    private int adminId;

    private int groupId;
    private boolean isAdmin, isLastMessage;

    public UserDetails(){

    }
    private boolean isLastMessage(){
        return isLastMessage;
    }

    public void setIsLastMessage(boolean isLastMessage) {
        this.isLastMessage = isLastMessage;
    }
    public static final Parcelable.Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel source) {
            return new UserDetails(source);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
    public UserDetails(Parcel in){
        userId = in.readInt();
        parseUserId = in.readString();
        messageId = in.readInt();
        name = in.readString();
        image = in.readString();
        phoneCode = in.readString();
        phoneNumber = in.readString();
        status = in.readString();
        lastMessage = in.readString();
        messageTime = in.readString();
        isSelected = in.readInt()== 1;
        isAGroup = in.readInt() == 1;
        adminId = in.readInt();
        groupId = in.readInt();
        isAdmin = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeInt(messageId);
        dest.writeString(parseUserId);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(phoneCode);
        dest.writeString(phoneNumber);
        dest.writeString(status);
        dest.writeString(lastMessage);
        dest.writeString(messageTime);
        dest.writeInt(isSelected ? 1 : 0);
        dest.writeInt(isAGroup ? 1 : 0);
        dest.writeInt(groupId);
        dest.writeInt(isAdmin ? 1:0 );
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getParseUserId() {
        return parseUserId;
    }

    public void setParseUserId(String parseUserId) {
        this.parseUserId = parseUserId;
    }
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isAGroup() {
        return isAGroup;
    }

    public void setIsAGroup(boolean isAGroup) {
        this.isAGroup = isAGroup;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }
    public String getPhoneType(){
        return phoneType;
    }
}
