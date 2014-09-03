package com.huhukun.tickteeforandroid;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kun on 19/08/2014.
 */
public class TickTeeAndroid extends Application {
    private static Context context;
    public static SharedPreferences appSetting;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        appSetting = getSharedPreferences(App_Constants.PREF_APP, 0);
        Account[] s = AccountManager.get(this).getAccountsByType(App_Constants.ACCOUNT_TYPE);
        if(s != null && s.length > 0)
        {
            App_Constants.currentAccount = s[0];
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
