package com.huhukun.tickteeforandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public static final String APP_TAG = "TICKTEE";

    private TextView tvTotal;
    private TextView tvOnProgress;
    private TextView tvOverdue;
    private TextView tvComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(APP_TAG, 0);
        if (settings.getString(LoginActivity.PREF_TOKEN, null) == null)
        {
            // Create an alert dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // Set alert title
            builder.setTitle(R.string.notice);

            // Set the value for the positive reaction from the user
            // You can also set a listener to call when it is pressed
            builder.setPositiveButton(R.string.ok, null);

            // The message
            builder.setMessage(R.string.login_notice);

            // Create the alert dialog and display it
            AlertDialog theAlertDialog = builder.create();
            theAlertDialog.show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }


        setContentView(R.layout.activity_main);

        tvTotal = (TextView)findViewById(R.id.tvTotal);
        tvOnProgress = (TextView)findViewById(R.id.tvOnProgress);
        tvOverdue = (TextView)findViewById(R.id.tvOverdue);
        tvComplete = (TextView)findViewById(R.id.tvComplete);

        tvTotal.setText("10");
        tvOnProgress.setText("3");
        tvComplete.setText("32");
        tvOverdue.setText("3");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.prepareMenu(menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.prepareMenu(menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_settings:
                settings();
                return true;
            case R.id.menu_login:
                login();
                return true;
            case  R.id.menu_logout:
                logout();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareMenu(Menu menu) {
        SharedPreferences settings = getSharedPreferences(APP_TAG, 0);
        if (settings.getString(LoginActivity.PREF_TOKEN, null) == null)
        {
            Log.d(APP_TAG, "show log in");
            menu.findItem(R.id.menu_login).setVisible(true);
            menu.findItem(R.id.menu_logout).setVisible(false);
        }else
        {
            Log.d(APP_TAG, "show log out");
            menu.findItem(R.id.menu_login).setVisible(false);
            menu.findItem(R.id.menu_logout).setVisible(true);
        }
    }

    private void settings(){

    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private  void logout() {
        SharedPreferences settings = getSharedPreferences(APP_TAG, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LoginActivity.PREF_TOKEN, null);
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
