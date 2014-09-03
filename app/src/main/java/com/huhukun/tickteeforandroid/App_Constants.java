package com.huhukun.tickteeforandroid;

import android.accounts.Account;

/**
 * Created by kun on 19/08/2014.
 */
public class App_Constants {
    public static final String PREF_APP = "TICKTEE";
    public static final String APP_TAG = "Ticktee_";
    public static final String ACCOUNT_TYPE = "com.huhukun.ticktee";
    public static final String AUTH_TOKEN_TYPE = "com.huhukun.ticktee";
    public static final String NEW_ACCOUNT = "new_account";
    public  static  final String PREF_EMAIL = "email";
    public  static  final String PREF_TOKEN = "auth_token";
    public  static  final String PREF_PASSWORD = "password";
    public static final String PREFS_DOWNLOAD_DATE = "download_date";

    public static final String AUTHORITY =
            "com.huhukun.ticktee";

    public static final int MAX_REQUEST_ATTEMPTS = 5;
    public static final int NON_HTTP_FAILURE = -1;



    public static final int TRANSACTION_PENDING = 0;
    public static final int TRANSACTION_RETRY = 1;
    public static final int TRANSACTION_IN_PROGRESS = 2;
    public static final int TRANSACTION_COMPLETED = 3;

    public static final String POST_TEXT = "POST";
    public static final String PUT_TEXT = "PUT";
    public static final String GET_TEXT = "GET";
    public static final String DELETE_TEXT = "DELETE";
    public static final String NEW_TEXT = "NEW";

    public static final String KEY_ERROR_MSG = "errorMsg";

    public static final String ERROR_MSG_RECEIVER = "com.huhukun.ticktee.ERROR_MSG_RECEIVER";

    public static Account currentAccount;
}
