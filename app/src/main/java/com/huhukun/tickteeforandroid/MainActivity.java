package com.huhukun.tickteeforandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {




    private static final String TAG = App_Constants.APP_TAG +"MainActivity";
    private static final String[] loaderColumns = new String[] {
            SqlOpenHelper.TableConstants._ID,
            SqlOpenHelper.TableConstants.COL_PROJECT_ID,
            SqlOpenHelper.TableConstants.COL_NAME,
            SqlOpenHelper.TableConstants.COL_DESCRIPTION,
            SqlOpenHelper.TableConstants.COL_START_AT,
            SqlOpenHelper.TableConstants.COL_END_AT,
            SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS,
            SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS,
            SqlOpenHelper.TableConstants.COL_CREATED_AT,
            SqlOpenHelper.TableConstants.COL_UPDATED_AT,
            SqlOpenHelper.TableConstants.COL_TRANSACTING,
            SqlOpenHelper.TableConstants.COL_STATUS,
            SqlOpenHelper.TableConstants.COL_RESULT,
            SqlOpenHelper.TableConstants.COL_TRANS_DATE };

    private TextView tvTotal;
    private TextView tvInProgress;
    private TextView tvOverdue;
    private TextView tvComplete;
    private View mProgressView;

    int total_count = 0;
    int in_progress_count = 0;
    int overdue_count = 0;
    int complete_count = 0;
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
                        this, baseUri, loaderColumns, null, null, null);



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
            if(cursor.getInt(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS))== 100)
            {
                complete_count ++;
            }
            else if (cursor.getInt(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS))< cursor.getInt(cursor.getColumnIndex(SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS)))
            {
                overdue_count ++;
                in_progress_count ++;
            }
            else{
                in_progress_count ++;
            }
            cursor.moveToNext();
        }
        tvTotal.setText(total_count+"");
        tvInProgress.setText(in_progress_count+"");
        tvOverdue.setText(overdue_count+"");
        tvComplete.setText(complete_count+"");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }




}
