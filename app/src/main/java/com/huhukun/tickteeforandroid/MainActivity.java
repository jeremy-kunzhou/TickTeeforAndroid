package com.huhukun.tickteeforandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.utils.BooleanUtils;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.NumberUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {




    private static final String TAG = App_Constants.APP_TAG +"MainActivity";


    private TextView tvTotal;
    private TextView tvInProgress;
    private TextView tvOverdue;
    private TextView tvComplete;
    private View mProgressView;

    int total_count = 0;
    int in_progress_count = 0;
    int overdue_count = 0;
    int complete_count = 0;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, Locale.getDefault().toString());

        setContentView(R.layout.activity_main);
        mProgressView = findViewById(R.id.loading_progress);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvInProgress = (TextView) findViewById(R.id.tvInProgress);
        tvOverdue = (TextView) findViewById(R.id.tvOverdue);
        tvComplete = (TextView) findViewById(R.id.tvComplete);

        tvTotal.setText("0");
        tvInProgress.setText("0");
        tvComplete.setText("0");
        tvOverdue.setText("0");

        if(App_Constants.currentAccount != null){
            Bundle params = new Bundle();
            params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(App_Constants.currentAccount,App_Constants.AUTHORITY,params);
        }

        Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, App_Constants.ALERT_ID, myIntent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
        {
            Log.d(TAG, "Alarm is already active");
        }else {
            Log.d(TAG, "Alarm is created");
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, App_Constants.ALERT_ID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Log.d(TAG, "Set alarm time " + calendar.getTimeInMillis() + " " + calendar.getTime());
            TickTeeAndroid.alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            TickTeeAndroid.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null) == null)
        {
            showIntroActivity();
        }
        else
        {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(0, null, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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

    private void showWarning(String warning)
    {
        // Create an alert dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set alert title
        builder.setTitle(R.string.notice);

        // Set the value for the positive reaction from the user
        // You can also set a listener to call when it is pressed
        builder.setPositiveButton(R.string.ok, null);

        // The message
        builder.setMessage(warning);

        // Create the alert dialog and display it
        AlertDialog theAlertDialog = builder.create();
        theAlertDialog.show();
    }

    private void showIntroActivity(){

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private void prepareMenu(Menu menu) {
        if (TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null) == null)
        {
            menu.findItem(R.id.menu_login).setVisible(true);
            menu.findItem(R.id.menu_logout).setVisible(false);
        }else
        {
            menu.findItem(R.id.menu_login).setVisible(false);
            menu.findItem(R.id.menu_logout).setVisible(true);
        }
    }

    private void settings(){
        Log.d(TAG, "call settings");
    }

    public void newProjectAction(View view)
    {
        Intent intent = new Intent(this, ProjectNewActivity.class);
        startActivity(intent);
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private  void logout() {
        SharedPreferences.Editor editor = TickTeeAndroid.appSetting.edit();
        editor.putString(App_Constants.PREF_TOKEN, null);
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void viewProjects(View view){
        int view_projects_type = 0;
        switch (view.getId()){
            case R.id.total_layout:
                view_projects_type = Project.TOTAL_PROJECTS;
                break;
            case R.id.in_progress_layout:
                view_projects_type = Project.IN_PROGRESS_PROJECTS;
                break;
            case R.id.overdue_layout:
                view_projects_type = Project.OVERDUE_PROJECTS;
                break;
            case R.id.complete_layout:
                view_projects_type = Project.COMPLETE_PROJECTS;
                break;
        }
        Intent intent = new Intent(this, ProjectListActivity.class);
        intent.putExtra(ProjectListActivity.PROJECT_STATUS, view_projects_type);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);



            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri  baseUri = TickteeProvider.CONTENT_URI;


        CursorLoader cursorLoader = new CursorLoader(
                        this, baseUri, SqlOpenHelper.LOADER_COLUMNS, null, null, null);



        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        total_count = 0;
        in_progress_count = 0;
        overdue_count = 0;
        complete_count = 0;
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            total_count ++;
            BigDecimal current = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS)));
            BigDecimal target = new BigDecimal(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_TARGET)));

            if(current.compareTo(target) == 0)
            {
                complete_count ++;
            }
            else {
                String start_date_string = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_START_AT));
                String end_date_string = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_END_AT));
                String name = cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_NAME));
                boolean isConsumed = BooleanUtils.parse(cursor.getString(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_IS_CONSUMED)));
                if (start_date_string != null && end_date_string != null && !TextUtils.isEmpty(start_date_string) && !TextUtils.isEmpty(end_date_string))
                {
                    try {
                        Date start = FormatHelper.toLocalDateFromUTCString(start_date_string);
                        Date end = FormatHelper.toLocalDateFromUTCString(end_date_string);
                        Date now = new Date();
                        Log.d(TAG, name + ": "+NumberUtils.getPercentage(current, target) +" "+ NumberUtils.getPercentage(start, end, now)
                                +" "+(end.getTime()-start.getTime())+ " "+(now.getTime()-start.getTime()));
                        if(!isConsumed && NumberUtils.getPercentage(current, target) < NumberUtils.getPercentage(start, end, now))
                        {
                            overdue_count++;
                        }else if (isConsumed && NumberUtils.getPercentage(current, target) > NumberUtils.getPercentage(start, end, now))
                        {
                            overdue_count++;
                        }
                    }catch (ParseException e) {
                        Log.d(TAG, e.toString());
                    }
                }

                in_progress_count++;

            }
            cursor.moveToNext();
        }
        tvTotal.setText(total_count + "");
        tvInProgress.setText(in_progress_count+"");
        tvOverdue.setText(overdue_count+"");
        tvComplete.setText(complete_count+"");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }




}
