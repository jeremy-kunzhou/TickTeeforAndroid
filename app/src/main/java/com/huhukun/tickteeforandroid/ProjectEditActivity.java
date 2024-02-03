package com.huhukun.tickteeforandroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.app.NavUtils;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.huhukun.tickteeforandroid.UILibrary.DatePickerFragment;
import com.huhukun.tickteeforandroid.UILibrary.SeekArc;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.*;

import androidx.appcompat.app.AppCompatActivity;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;

import java.text.ParseException;

/**
 * Created by kun on 22/08/2014.
 */
public class ProjectEditActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = App_Constants.APP_TAG + "ProjectEditActivity";
    ProjectEditFragment fragment;
    private String item_id;
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_project_edit);
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
            arguments.putString(ProjectEditFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ProjectEditFragment.ARG_ITEM_ID));
            item_id = getIntent().getStringExtra(ProjectEditFragment.ARG_ITEM_ID);
            fragment = new ProjectEditFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.project_edit_container, fragment)
                    .commit();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.project_edit_done:
                doneProject();
                return true;
            case R.id.project_edit_discard:
                discardProject();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void discardProject() {
        Toast.makeText(this, R.string.change_discard, Toast.LENGTH_LONG).show();

        finish();

    }

    private void doneProject() {
        try {
            fragment.saveProject();
            Toast.makeText(this, R.string.change_saved, Toast.LENGTH_LONG).show();
            finish();

        } catch (ParseException e) {
            Log.d(TAG, e.toString());
            Toast.makeText(this, R.string.save_failure, Toast.LENGTH_LONG).show();

        }


    }
}
