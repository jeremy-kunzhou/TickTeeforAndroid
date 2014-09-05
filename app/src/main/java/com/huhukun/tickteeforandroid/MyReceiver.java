package com.huhukun.tickteeforandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

/**
 * Created by kun on 5/09/2014.
 */
public class MyReceiver extends BroadcastReceiver{

    public MyReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service1 = new Intent(context,  MyAlarmService.class);
        context.startService(service1);
    }
}
