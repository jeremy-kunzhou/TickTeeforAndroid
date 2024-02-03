package com.huhukun.tickteeforandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.huhukun.utils.FormatHelper;

import java.util.List;


/**
 * An activity representing a single Project detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ProjectListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ProjectDetailFragment}.
 */
public class ProjectDetailActivity extends AppCompatActivity {

    private String item_id;
    private ProjectDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        // Show the Up button in the action bar.
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
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            item_id = getIntent().getStringExtra(ProjectDetailFragment.ARG_ITEM_ID);
            arguments.putString(ProjectDetailFragment.ARG_ITEM_ID, item_id);

            fragment = new ProjectDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.project_detail_container, fragment)
                    .commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.project_detail_edit:
                editProject();
                break;
            case R.id.project_detail_share:
                shareProject();
                break;
            case R.id.project_detail_done:
                saveProgress();
                break;
            case R.id.project_detail_remove:
                removeProject();
                break;
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, ProjectListActivity.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void removeProject() {
        if (fragment != null) {
            // Create an alert dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set alert title
            builder.setTitle(R.string.notice);

            // Set the value for the positive reaction from the user
            // You can also set a listener to call when it is pressed
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (fragment.removeProject() > -1) {
                        Toast.makeText(ProjectDetailActivity.this, R.string.progress_removed, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
            builder.setNegativeButton(R.string.no, null);
            // The message
            builder.setMessage(R.string.confirm_delete_project);

            // Create the alert dialog and display it
            AlertDialog theAlertDialog = builder.create();
            theAlertDialog.show();

        }
    }

    private void saveProgress() {
        if (fragment != null) {
            fragment.saveProgress();
            Toast.makeText(this, R.string.progress_saved, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void shareProject() {
        if(fragment != null ){
            fragment.sharing();
        }

    }

    private void editProject() {
        Intent intent = new Intent(this, ProjectEditActivity.class);
        intent.putExtra(ProjectEditActivity.ARG_ITEM_ID, item_id);
        startActivity(intent);
    }

    public void findTwitterClient(Intent tweetIntent) {
        final String[] twitterApps = {
                // package // name - nb installs (thousands)
                "com.twitter.android", // official - 10 000
                "com.twidroid", // twidroyd - 5 000
                "com.handmark.tweetcaster", // Tweecaster - 5 000
                "com.thedeck.android" // TweetDeck - 5 000
        };
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (int i = 0; i < twitterApps.length; i++) {
            for (ResolveInfo resolveInfo : list) {
                String p = resolveInfo.activityInfo.packageName;
                if (p != null && p.startsWith(twitterApps[i])) {
                    tweetIntent.setPackage(p);
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
