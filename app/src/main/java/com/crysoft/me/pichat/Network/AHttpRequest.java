package com.crysoft.me.pichat.Network;

import android.app.Activity;
import android.os.Handler;

import com.crysoft.me.pichat.database.DatabaseAdapter;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.helpers.Utilities;
import com.crysoft.me.pichat.models.MessageModel;
import com.crysoft.me.pichat.models.UserDetails;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Maxx on 6/15/2016.
 */
public class AHttpRequest {
    private AHttpResponse response;
    private RequestCallback callback;
    private Activity context;
    private Handler mHandler = new Handler();

    public AHttpRequest() {

    }

    public AHttpRequest(Activity context, boolean showProgress) {
        this(context);
    }

    public AHttpRequest(Activity context) {
        this.context = context;
    }

    public AHttpRequest(Activity context, RequestCallback callback) {
        this(context);
        this.callback = callback;
    }

    public void registerUser(final String code, final String mobile, final String regId, final String timezone) {
        if (Utilities.isOnline(context)) {
            Utilities.showNoInternetConnection(context);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();

                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("code", code));
                params.add(new BasicNameValuePair("mobile", mobile));
                params.add(new BasicNameValuePair("regId", regId));
                params.add(new BasicNameValuePair("timezone", timezone));
                params.add(new BasicNameValuePair("device_type", "0")); //0 because we are on Android

                HttpRequest httpRequest = new HttpRequest();
                try {
                    String responseString = httpRequest.postData(Urls.REGISTER_USER, params);
                    response = new AHttpResponse(responseString, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void verifyUser(final String mobile, final String verificationCode, final String regId) {
        if (Utilities.isOnline(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("mobile", mobile));
                    list.add(new BasicNameValuePair("verify_code", verificationCode));
                    list.add(new BasicNameValuePair("regId", regId));

                    HttpRequest httpRequest = new HttpRequest();
                    try {
                        String responseString = httpRequest.postData(Urls.VERIFY_USER, list);
                        if (context != null && responseString != null){
                            AHttpResponse response = new AHttpResponse(responseString,true);
                            if (response.isSuccess){
                                MyPreferences myPreferences = new MyPreferences(context);
                                UserDetails userDetails = response.verifyDetails();
                                myPreferences.SaveUserDetails(userDetails);
                            }else{

                            }

                            if (callback != null){
                                callback.onRequestComplete(response);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            }).start();
        }else{
            if (context != null){
                Utilities.showNoInternetConnection(context);
            }
        }
    }

    public void editProfile(final String id, final String name,
                            final String status, final String image, final String code,
                            final String mobile) {
        if (Utilities.isOnline(context)) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("id", id));
                    if (name != null)
                        list.add(new BasicNameValuePair("name", name));
                    if (status != null)
                        list.add(new BasicNameValuePair("status", status));
                    if (image != null)
                        list.add(new BasicNameValuePair("image", image));
                    if (code != null)
                        list.add(new BasicNameValuePair("code", code));
                    if (mobile != null)
                        list.add(new BasicNameValuePair("mobile", mobile));
                    try {
                        String responseString = new HttpRequest().postData(
                                Urls.EDIT_PROFILE, list);
                        final AHttpResponse response = new AHttpResponse(
                                responseString, true);
                        UserDetails userDetails = response.editProfile();

                        MyPreferences myPreferences = new MyPreferences(context);
                        myPreferences.SaveUserDetails(userDetails);

                        if (context != null && callback != null) {
                            mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    callback.onRequestComplete(response);
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        } else {
            Utilities.showNoInternetConnection(context);
        }
    }

    public void sendMessage(final String id, final String friend_id,
                            final String message, final String type,
                            final boolean isGroupMessage) {


        if (Utilities.isOnline(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("id", id));
                    list.add(new BasicNameValuePair("friend_id", friend_id));
                    list.add(new BasicNameValuePair("message", message));
                    list.add(new BasicNameValuePair("type", type));

                    try {
                        String responseString;

                        if (!isGroupMessage) {
                            responseString = new HttpRequest().postData(
                                    Urls.SEND_MESSAGE, list);

                        } else {
                            responseString = new HttpRequest().postData(
                                    Urls.GROUP_MESSAGE, list);

                        }
                        AHttpResponse response = new AHttpResponse(
                                responseString, true);
                        MessageModel message = response.getSentMessage();
                        if (context != null && callback != null) {
                            callback.onRequestComplete(response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Utilities.showNoInternetConnection(context);
        }
    }

    public void getLastSeen(final String id, final String myid) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                if (!Utilities.isOnline(context)) {

                    return;
                }

                ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("id", id));
                list.add(new BasicNameValuePair("user_id", myid));

                try {
                    String responseString = new HttpRequest().postData(
                            Urls.LAST_SEEN_GET, list);
                    AHttpResponse response = new AHttpResponse(responseString,
                            true);

                    // response.getLastSeen();
                    if (context != null && callback != null) {
                        callback.onRequestComplete(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void removeAndExitGroup(final String userId, final String groupId) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpRequest httpRequest = new HttpRequest();
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("id", groupId));
                list.add(new BasicNameValuePair("member_ids", userId));

                try {
                    String responseString = httpRequest.postData(
                            Urls.EXIT_GROUP, list);
                    if (responseString != null) {
                        final AHttpResponse response = new AHttpResponse(
                                responseString, true);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onRequestComplete(response);
                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (callback != null) {
                    callback.onRequestComplete(null);
                }

            }
        }).start();

    }

    public void setLastSeen(final String id, final String userStatus) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpRequest httpRequest = new HttpRequest();
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("id", id));
                list.add(new BasicNameValuePair("status", userStatus));
                try {
                    String response = httpRequest.postData(Urls.LAST_SEEN_SET,
                            list);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateAllGroupsDetail(final String userId) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("id", userId));
                HttpRequest request = new HttpRequest();
                try {
                    String responseString = request.postData(Urls.GROUPLIST,
                            list);
                    AHttpResponse response = new AHttpResponse(responseString,
                            true);
                    if (response.isSuccess) {
                        List<UserDetails> details = response.getGroupDetails();
                        DatabaseAdapter adapter = new DatabaseAdapter(context);
                        adapter.openForReading();
                        for (int i = 0; i < details.size(); i++) {
                            adapter.insertOrUpdateGroup(details.get(i));
                        }

                        adapter.close();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getGroupInfo(final String userId, final String groupId) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("user_id", userId));
                list.add(new BasicNameValuePair("id", groupId));

                HttpRequest request = new HttpRequest();
                try {
                    String responseString = request.postData(Urls.GROUPINFO,
                            list);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deleteChat(final String user_id, final String groupId,
                           final String Who) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("user_id", user_id));
                list.add(new BasicNameValuePair("id", groupId));
                list.add(new BasicNameValuePair("who", Who));
                HttpRequest request = new HttpRequest();

                try {
                    String responseString = request.postData(Urls.DELETE_CHAT,
                            list);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }).start();

    }

}
