package com.huhukun.tickteeforandroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;

/**
 * Created by kun on 8/09/2014.
 */
public class ProjectSharingActivity extends ActionBarActivity{

    private static final String TAG = App_Constants.APP_TAG + "ProjectSharingActivity";
    ProjectSharingFragment fragment;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_project_sharing);
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
            Bundle arguments = new Bundle();
            arguments.putString(ProjectSharingFragment.ARG_SHARING_ID,
                    getIntent().getStringExtra(ProjectSharingFragment.ARG_SHARING_ID));
            fragment = new ProjectSharingFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.project_sharing_container, fragment)
                    .commit();

        }
    }
}
