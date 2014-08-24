package com.huhukun.tickteeforandroid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;

import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants;

/**
 * A list fragment representing a list of Projects. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ProjectDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ProjectListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks <Project> mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private static final String TAG = App_Constants.APP_TAG +"ProjectListFragment";

    private static final int GET_FILTERED_SONGS = 1;

    private static final String[] loaderColumns = new String[] {
            TableConstants._ID,
            TableConstants.COL_PROJECT_ID,
            TableConstants.COL_NAME,
            TableConstants.COL_DESCRIPTION,
            TableConstants.COL_START_AT,
            TableConstants.COL_END_AT,
            TableConstants.COL_EXPECTED_PROGRESS,
            TableConstants.COL_CURRENT_PROGRESS,
            TableConstants.COL_CREATED_AT,
            TableConstants.COL_UPDATED_AT,
            TableConstants.COL_TRANSACTING,
            TableConstants.COL_STATUS,
            TableConstants.COL_RESULT,
            TableConstants.COL_TRANS_DATE };
    private static final String[] adapterColumns = new String[] {
            TableConstants.COL_NAME,
            TableConstants.COL_DESCRIPTION,
            TableConstants.COL_TRANSACTING };
    private static final int[] to = new int[] {
            R.id.row_project_title,
            R.id.row_project_category,
            R.id.row_project_progress };


    private ExecutorService executorPool;

    private long mUpdateId = 0;

    CursorAdapter cursorAdapter;
    LoaderManager loaderManager;
    CursorLoader cursorLoader;

    /**
     * Create the CursorLoader and provide it with the
     * filter data.
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {

        // Mark as pending so the SyncAdapter knows to request
        // new data from the REST API.
        QueryTransactionInfo.getInstance().markPending();

        switch (id) {
            case GET_FILTERED_SONGS:
                Uri baseUri;
                String searchTitle = null;

//                searchTitle = titleText.getText().toString();

                if ( searchTitle == null || searchTitle.trim().length() == 0 ) {
                    baseUri = TickteeProvider.CONTENT_URI;
                } else {
                    baseUri =
                            Uri.withAppendedPath(TickteeProvider.CONTENT_URI_FILTERED,
                                    Uri.encode(searchTitle) );
                }

                cursorLoader = new CursorLoader(
                        getActivity(), baseUri, loaderColumns, null, null, null);

                break;
            default:
                throw new IllegalStateException(
                        "Cannot create Loader with id[" + id + "]" );
        }

        return cursorLoader;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        if(cursor!=null) {


            cursorAdapter =
                    new ProjectCursorAdapter(getActivity(),cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            setListAdapter(cursorAdapter);
            cursorAdapter.swapCursor(cursor); //swap the new cursor in.
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        if(cursorAdapter!=null) {
            cursorAdapter.swapCursor(null);
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks <T>{
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(T t);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks<Project>() {
        @Override
        public void onItemSelected(Project id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProjectListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(GET_FILTERED_SONGS, null, this);
//
//        ListAdapter projectsAdapter = new ProjectListAdapter(getActivity(),
//                R.layout.row_project_list, ProjectsManagementImpl.getInstance().getAllProjects());
//        setListAdapter(projectsAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        TickTeeAndroid.getAppContext().registerReceiver(errorMsgReceiver,
                new IntentFilter(App_Constants.ERROR_MSG_RECEIVER));

        executorPool = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        executorPool.shutdown();

        TickTeeAndroid.getAppContext().unregisterReceiver(errorMsgReceiver);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Log.d(TAG, "Select item");
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        final Cursor cursor;

        cursor = (Cursor)listView.getItemAtPosition( position );
        try {
            mCallbacks.onItemSelected(new Project(cursor));
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }


    /**
     * Receives and displays an error message if the REST request fails.
     */
    public BroadcastReceiver errorMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String errorMsg =
                    intent.getExtras().getString( App_Constants.KEY_ERROR_MSG );

            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT);

        }

    };
}
