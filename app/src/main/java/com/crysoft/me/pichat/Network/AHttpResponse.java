package com.crysoft.me.pichat.Network;

import com.crysoft.me.pichat.helpers.helpers;
import com.crysoft.me.pichat.models.GroupDetailsModel;
import com.crysoft.me.pichat.models.MessageModel;
import com.crysoft.me.pichat.models.UserDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Maxx on 6/15/2016.
 */
public class AHttpResponse {
    private static final String STATUS = "success";
    private static final String MESSAGE = "message";

    public boolean isInternetConnected;
    public boolean isSuccess;
    public String message;

    private JSONObject rootObject;

    public AHttpResponse(String response, boolean isInternetConnected){
        try{
            rootObject = new JSONObject(response);
            isSuccess = rootObject.getBoolean(STATUS);
            message = rootObject.getString(MESSAGE);
        }catch (JSONException e){
            e.printStackTrace();
            isSuccess = false;
            message = "Server Connection Failed";
        }
    }

    public JSONObject getRootObject() {
        return rootObject;
    }

    private UserDetails userDetails;
    private int month;

    public UserDetails verifyDetails(){
        if (userDetails == null && isSuccess){
            userDetails = new UserDetails();
            if (rootObject != null){
                try{
                    userDetails.setUserId(Integer.parseInt(rootObject.getString("user_id")));
                    userDetails.setImage(rootObject.getString("pic"));
                    userDetails.setName(rootObject.getString("fname"));
                    userDetails.setPhoneCode(rootObject.getString("phonecode"));
                    userDetails.setPhoneNumber(rootObject.getString("phone"));
                    userDetails.setStatus(rootObject.getString("status"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        return userDetails;
    }
    public UserDetails editProfile(){
        if (userDetails == null && isSuccess){
            userDetails = new UserDetails();
            try{
                JSONObject object = rootObject.getJSONObject("details");
                userDetails.setUserId(Integer.parseInt(object.getString("id")));
                userDetails.setName(object.getString("name"));
                userDetails.setStatus(object.getString("status"));
                userDetails.setImage(object.getString("image"));
                userDetails.setPhoneCode(object.getString("phone_code"));
                userDetails.setPhoneNumber(object.getString("phone"));
            }catch(JSONException e){
                e.printStackTrace();
            }

        }
        return userDetails;
    }
    // Looks Like this
    // {
    // "details": {
    // "id": "902",
    // "time": "2016-06-15 17:11:17",
    // "type": "1",
    // "user_id": "71",
    // "friend_id": "70",
    // "who": "1",
    // "message": "Niaje",
    // "delivery_time": "0000-00-00 00:00:00",
    // "image": "k.jpg",
    // "user_status": "is how...!",
    // "phone": "720893982",
    // "phone_code": "+254",
    // "name": "maxx",
    // "status": "1"
    // },
    // "message": "Stored message for sending.",
    // "success": true
    // }
    public MessageModel getSentMessage(){
        MessageModel model = new MessageModel();
        try{
            if (rootObject.has("tmp_msg_id"))
                model.setId(rootObject.getInt("tmp_msg_id"));
            JSONObject object = rootObject.getJSONObject("details");
            model.setMessageId(object.getInt("id"));

            String milli_sec = helpers.convertDateIntoLocalMillis(object.getString("time"));

            Calendar calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH)+ 1;
            model.setTime(calendar.get(Calendar.YEAR) + "-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH)+ ""
                    +calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+ calendar.get(Calendar.SECOND));
            model.setMessageType(object.getInt("type"));
            model.setUserID(object.getInt("user_id"));
            model.setFriendId(object.getInt("friend_id"));
            model.setWhat(object.getInt("what"));

            if (object.has("name"))
                model.setDisplayName(object.getString("name"));
            model.setMessage(object.getString("message"));

        }catch (JSONException e){

        }
        return model;
    }
    public UserDetails getJoinGroupDetail(){
        try{
            UserDetails userDetails = new UserDetails();
            JSONObject object = rootObject.getJSONObject("details");
            userDetails.setAdminId(object.getInt("user_id"));
            userDetails.setName(object.getString("name"));
            userDetails.setImage(object.getString("image"));
            userDetails.setUserId(object.getInt("id"));
            return userDetails;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    public List<UserDetails> getGroupDetails(){
        GroupDetailsModel model = new GroupDetailsModel();

        if (isSuccess){
            try{
                JSONArray array = rootObject.getJSONArray("details");

                JSONObject jsonObject;
                for (int i=0; i< array.length(); i++){
                    jsonObject = array.getJSONObject(i);
                    userDetails.setGroupId(jsonObject.getInt("id"));
                    userDetails.setName(jsonObject.getString("name"));
                    userDetails.setImage(jsonObject.getString("image"));
                    userDetails.setAdminId(jsonObject.getInt("admin_id"));
                }
            }catch(JSONException e){

            }
        }
        return null;
    }
}
