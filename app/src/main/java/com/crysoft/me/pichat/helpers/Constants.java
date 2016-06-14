package com.crysoft.me.pichat.helpers;

/**
 * Created by Maxx on 6/14/2016.
 * This Class handles our Constants
 */
public class Constants {

    //GCM Constants
    public static final String SENDER_ID = "808457187436";

    public static final String ROOT_FOLDER_NAME ="PiChat";
    public static final String FOLDER_IMAGE ="PiChat Images";
    public static final String PROFILE_PICTURES ="Pichat Profiles Photos";

    public static final String STICKER_DIR = "Pichat Stickers";

    public static final class Extra{
        public static final String CODES ="codes";
        public static final String FRIEND_DETAILS ="friend_details";
        public static final String IMAGE = "image";
        public static final String NAME = "Name";
        public static final String BITMAP ="bitmap";
        public static final String LOCAL_PATH ="local_image_path";
        public static final String IMAGE_URL ="image_url";
        public static final String USER_LIST ="user_list";
    }
    public class Pref{
        public static final String REGISTER_STEP_ONE = "register_one";
        public static final String REGISTER_STEP_TWO =  "register_two";
        public static final String REGISTER_STEP_THREE = "register_three";

        //Storage for Current UserDetails Details
        public static final String USER_ID   =      "user_id";
        public static final String USER_NAME =      "user_name";
        public static final String USER_IMAGE =     "user_image";
        public static final String USER_PHONE_CODE =  "phone_code";
        public static final String USER_PHONE_NO =  "phone_no";
        public static final String USER_STATUS =    "user_status";

        public static final String USER_LAST_MSG_STATUS =   "readornot";

    }
    public static final class Params{
        public static final String phone="phone";
    }
    public static final class MyActions{
        public static final String CONTACT_LIST_UPDATE = "contact_list_updated";
        public static final String MESSAGE_UPDATE = "message_update";
        public static final String SEND_MESSAGE = "send_message";
    }
}
