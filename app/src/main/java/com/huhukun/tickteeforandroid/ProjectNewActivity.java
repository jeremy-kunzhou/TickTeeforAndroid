package com.huhukun.tickteeforandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.ParseException;


public class ProjectNewActivity extends ActionBarActivity {

    private static final String TAG = App_Constants.APP_TAG + "ProjectNewActivity";
    ProjectEditFragment fragment;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_project_new);
        getSupportActionBar().setIcon(R.drawable.artwork);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (bundle == null) {
            // if ProjectDetialFragment is Fragment
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            fragment = new ProjectEditFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.project_new_container, fragment)
                    .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_new, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.project_new_done:
                addProject();
                return true;
            case R.id.project_new_discard:
                discardProject();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void discardProject() {
        finish();
    }

    private void addProject() {
        if(fragment !=null)
        {
            try {
                long result = fragment.addProject();
                if (result != -1)
                {
                    Toast.makeText(this, R.string.project_added, Toast.LENGTH_LONG);
                    Intent intent = new Intent(this, ProjectListActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (ParseException e) {
                Log.e(TAG, e.toString());
                Toast.makeText(this, R.string.project_added_fail, Toast.LENGTH_LONG);
            }

        }
    }
}
