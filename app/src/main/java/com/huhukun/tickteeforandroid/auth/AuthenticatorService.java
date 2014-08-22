package com.huhukun.tickteeforandroid.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by kun on 19/08/2014.
 */
public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
