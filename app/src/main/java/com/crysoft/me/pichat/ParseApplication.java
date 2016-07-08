package com.crysoft.me.pichat;

import android.app.Application;

import com.crysoft.me.pichat.Message;
import com.crysoft.me.pichat.User;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * Created by Maxx on 6/23/2016.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Message.class);

        //Enable local database
        Parse.enableLocalDatastore(this);

        //Initialization Parameters
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("khratos")
                .clientKey("r153ofth3n00b5")
                .server("http://khaotic.herokuapp.com/parse/")
                .build()
        );
        // Need to register GCM token
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
