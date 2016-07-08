package com.crysoft.me.pichat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by Maxx on 6/23/2016.
 */
@ParseClassName("Messages")
public class Message extends ParseObject {
    public Message() {
        /*Required Empty Constructor*/

    }

    public String getUserFromId() {
        return getString("user_from_id");
    }

    public void setUserFromId(String userFromId) {
        put("user_from_id", userFromId);
    }
    public String getUserToId() {
        return getString("user_to_id");
    }

    public void setUserToId(String userToId) {
        put("user_to_id", userToId);
    }
    public Date getMessageDate(){
        return getDate("message_date");
    }
    public void setMessageDate(Date sentOn){
        put("message_date",sentOn);
    }
    public boolean getSeparator(){
        return getBoolean("separator");
    }
    public void setSeparator(Boolean state){
        put("separator",state);
    }
    public int getStatus() {
        return getInt("status");
    }

    public void setStatus(int status) {
        put("status", status);
    }

    public String getMessage() {
        return getString("message");
    }

    public void setMessage(String message) {
        put("message", message);
    }

    public String getSentOn() {
        return getString("sent_on");
    }

    public void setSentOn(String sentOn) {
        put("sent_on", sentOn);
    }

    public String getReceivedOn() {
        return getString("received_on");
    }

    public void setReceivedOn(String receivedOn) {
        put("received_on", receivedOn);
    }

    public String getMediaType() {
        return getString("media_type");
    }

    public void setMediaType(String mediaType) {
        put("media_type", mediaType);
    }

    public boolean getNeedsDelivery() {
        return getBoolean("needs_delivery");
    }

    public void setNeedsDelivery(Boolean needsDelivery) {
        put("needs_deliver", needsDelivery);
    }

    public String getMediaSize() {
        return getString("media_size");
    }

    public void setMediaSize(String mediaSize) {
        put("media_size", mediaSize);
    }

    public String getMediaURL() {
        return getString("media_url");
    }

    public void setMediaURL(String mediaURL) {
        put("media_url", mediaURL);
    }
}
