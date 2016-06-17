package com.crysoft.me.pichat.Network;

import android.os.Environment;

import com.crysoft.me.pichat.helpers.Constants;

import java.io.File;

/**
 * Created by Maxx on 6/15/2016.
 */
public class Urls {
    public  static final String BASE_URL = "http://crysoft.me/ninelV1/";
    public  static final String BASE_IMAGE = BASE_URL + "images/";
    public static final String BASE_STICKER = BASE_URL + "stickers/";

    public static final  String BASE_LOCAL_IMAGE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + Constants.ROOT_FOLDER_NAME
            + File.separator
            + Constants.FOLDER_IMAGE;

    public static final String REGISTER_USER = BASE_URL + "userregister.php";
    public static final String VERIFY_USER = BASE_URL + "verified_code.php";
    public static final String EDIT_PROFILE = BASE_URL + "edit_profile.php";
    public static final String UPDATE_CONTACTS = BASE_URL + "friendalllist.php";
    public static final String SEND_MESSAGE = BASE_URL + "sendmessage.php";
    public static final String UPLOAD_IMAGE = BASE_URL + "upload_image.php";
    public static final String UPLOAD_VIDEO = BASE_URL + "upload_video.php";
    public static final String GET_STICKER = BASE_URL + "getStickerList.php";
    public static final String LAST_SEEN_GET = BASE_URL + "last_seen_get";
    public static final String LAST_SEEN_SET = BASE_URL + "last_seen_set";
    public static final String CREATE_GROUP = BASE_URL + "create_group.php";
    public static final String GET_GROUP_MEMBER = BASE_URL + "group_member.php";
    public static final String GROUP_MESSAGE = BASE_URL + "message_group.php";
    public static final String GROUPLIST = BASE_URL + "group_list.php";
    public static final String GROUPINFO = BASE_URL + "group_time_info.php";
    public static final String EXIT_GROUP = BASE_URL + "join_unjoin_group_2.php";
    public static final String UNJOIN_GROUP = BASE_URL + "join_unjoin_group,php";
    public static final String DELETE_CHAT = BASE_URL + "group_member_delete";


}
