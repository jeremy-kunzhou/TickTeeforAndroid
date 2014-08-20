package com.huhukun.tickteeforandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.model.ProjectsManagementImpl;

import org.json.JSONException;

import java.text.ParseException;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {


    private TextView tvTotal;
    private TextView tvOnProgress;
    private TextView tvOverdue;
    private TextView tvComplete;
    private View mProgressView;
    private ProjectsLoadingTask mProjectsLoadingTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("ddd", Locale.getDefault().toString());

        setContentView(R.layout.activity_main);
        mProgressView = findViewById(R.id.loading_progress);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvOnProgress = (TextView) findViewById(R.id.tvOnProgress);
        tvOverdue = (TextView) findViewById(R.id.tvOverdue);
        tvComplete = (TextView) findViewById(R.id.tvComplete);

        tvTotal.setText("0");
        tvOnProgress.setText("0");
        tvComplete.setText("0");
        tvOverdue.setText("0");

        if (TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null) == null)
        {
            showIntroActivity();
        }
        else
        {
            syncProject();
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
            syncProject();
        }
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
            Log.d(App_Constants.APP_TAG, "show log in");
            menu.findItem(R.id.menu_login).setVisible(true);
            menu.findItem(R.id.menu_logout).setVisible(false);
        }else
        {
            Log.d(App_Constants.APP_TAG, "show log out");
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
        SharedPreferences.Editor editor = TickTeeAndroid.appSetting.edit();
        editor.putString(App_Constants.PREF_TOKEN, null);
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void llTotalClick(View view){
        Intent intent = new Intent(this, ProjectListActivity.class);
        startActivity(intent);
    }

    private void syncProject(){
        showProgress(true);
        showProgress(true);
        mProjectsLoadingTask = new ProjectsLoadingTask();
        mProjectsLoadingTask.execute((Void) null);
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



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ProjectsLoadingTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {


            boolean result = false;
            try {
                ProjectsManagementImpl.getInstance().SyncProjects();
                result = true;
            } catch (JSONException e)
            {
                Log.d("JSON", e.toString());
            } catch (ParseException e) {
                Log.d("JSON", e.toString());
            }


            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {

            } else {
                showWarning("Sync Fails");
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


}
