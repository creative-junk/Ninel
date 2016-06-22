package com.crysoft.me.pichat.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.crysoft.me.pichat.Network.HttpRequest;
import com.crysoft.me.pichat.Network.Urls;
import com.crysoft.me.pichat.database.DatabaseAdapter;
import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.MyPreferences;
import com.crysoft.me.pichat.helpers.Utilities;
import com.crysoft.me.pichat.models.StickerItem;
import com.crysoft.me.pichat.models.UserDetails;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxx on 6/20/2016.
 */
public class StickerManageServices extends Service {
    private static final String TAG ="StickerManagerService";

    private static final String BASE_PATH = Environment.getExternalStorageDirectory()
            + File.separator
            + Constants.ROOT_FOLDER_NAME
            + File.separator
            + Constants.STICKER_DIR
            + File.separator;

    private DatabaseAdapter databaseAdapter;
    private UserDetails userDetails;

    private Thread thread;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseAdapter = new DatabaseAdapter(this);
        userDetails = new MyPreferences(getApplicationContext()).getUserDetails();
        manageStickers();
        return START_STICKY;
    }

    private void manageStickers() {
        createStickersDir();
        if (thread == null || !thread.isAlive()){
            requestForStickers();
        }
    }

    private void requestForStickers() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id", userDetails.getUserId() + ""));
                if (Utilities.isOnline(getApplicationContext())){
                    try{
                        String response = new HttpRequest().postData(Urls.GET_STICKER,nameValuePairs);
                        if (response != null){
                            parseTheSticker(response);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else{

                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                });
            }
        });
        thread.start();
    }

    private void parseTheSticker(String response) {
        try{
            JSONObject object = new JSONObject(response);
            if (object.getBoolean("success")){
                JSONArray array = object.getJSONArray("details");
                StickerItem item;

                for (int i=0; i < array.length(); i++){
                    item = new StickerItem();
                    object = array.getJSONObject(i);

                    item.setId(object.getString("id"));
                    item.setImage(object.getString("image"));
                    item.setExtension(object.getString("ext"));
                    item.setCategory(object.getString("category"));
                    item.setTime(System.currentTimeMillis() + "");

                    File file = new File(BASE_PATH + item.getImage() + item.getExtension());

                    if (!file.exists()){
                        createFileFromUrl(Urls.BASE_STICKER + item.getImage() + item.getExtension(), item);
                    }
                    if (databaseAdapter.getStickerDetail(item.getImage() + item.getExtension()) == null)
                        databaseAdapter.insertSticker(item);
                }
            }
        } catch (JSONException e){

        }
        stopSelf();
    }

    private void createFileFromUrl(String stickerURL, StickerItem item) {
        try{
            int count = 0;
            URL url = new URL(stickerURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();

            int lengthOfFile = urlConnection.getContentLength();

            InputStream inputStream = new BufferedInputStream(url.openStream());

            File file = new File(BASE_PATH + item.getImage() + item.getExtension());

            if (!file.exists()){
                file.getParentFile().mkdirs();
                OutputStream outputStream = new FileOutputStream(file);

                byte data[] = new byte[1024];
                while ((count = inputStream.read(data))!= -1){
                    outputStream.write(data,0,count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createStickersDir() {
        File file = new File( BASE_PATH);
        if (!file.exists()){
            file.mkdir();
        }
    }
}
