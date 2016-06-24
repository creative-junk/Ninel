package com.crysoft.me.pichat.helpers;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxx on 6/22/2016.
 */
public class ContactsManager {
    private static final String MIMETYPE = "vnd.android.cursor.item/com.crysoft.me";
    public static void addContact(Context context, MyContact contact, MyContact phone){
        ContentResolver contentResolver = context.getContentResolver();

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.RawContacts.CONTENT_URI, true))
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, SyncStateContract.Constants.ACCOUNT_NAME)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, SyncStateContract.Constants.ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.AGGREGATION_MODE,ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT)
                .build());

        ops.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI,true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.name)
                .build());
        ops.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI,true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE, MIMETYPE)
                .withValue(ContactsContract.Data.DATA1, 12345)
                .withValue(ContactsContract.Data.DATA2, "user")
                .withValue(ContactsContract.Data.DATA3, phone)
                .build());
        try{
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static Uri addCallerIsSyncAdapterParameter(Uri contentUri, boolean isSyncOperation) {
        if (isSyncOperation){
            return contentUri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,"true").build();
        }
        return contentUri;
    }

    private static class MyContact {
        public String name;
        public String number;
        public static List<String> mNumbersList = new ArrayList<>();
        public String Id;

        public MyContact( String Id,String number){
//        this.name = name;
            this.Id = Id;
            this.number = number;
            mNumbersList.add(number);
        }
    }
}
