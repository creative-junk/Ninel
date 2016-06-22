package com.crysoft.me.pichat;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.support.v7.app.AppCompatActivity;


import com.crysoft.me.pichat.helpers.Constants;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    AccountManager accountManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Constants.ACCOUNT_NAME);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, Constants.ACCOUNT_TOKEN);

        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        accountManager = AccountManager.get(this);
        accountManager.addAccountExplicitly(account,null,null);
        //      mAccountManager.setAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS, Constants.ACCOUNT_TOKEN);
        ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY,true);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK,intent);
        finish();


    }

}
