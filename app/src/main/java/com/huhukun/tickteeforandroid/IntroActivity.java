package com.huhukun.tickteeforandroid;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class IntroActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }



    @Override
    protected void onResume(){
        super.onResume();

        if (TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null) != null)
        {
            finish();
        }
    }

    public void loginAction(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
