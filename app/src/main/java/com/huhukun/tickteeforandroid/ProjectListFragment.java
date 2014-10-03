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
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;

import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private int projects_type;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks <Project> mCallbacks ;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private static final String TAG = App_Constants.APP_TAG +"ProjectListFragment";

    private static final int GET_PROJECTS_BY_STATUS = 1;


    private ExecutorService executorPool;

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
            case GET_PROJECTS_BY_STATUS:
                Uri baseUri;

                switch (projects_type){
                    case Project.TOTAL_PROJECTS:
                        // total projects
                        baseUri = TickteeProvider.CONTENT_URI;
                        break;
                    default:
                        // get projects in status of in progress (2), overdue(3), complete(4)
                        baseUri =
                                Uri.withAppendedPath(TickteeProvider.CONTENT_URI_STATUS,
                                        projects_type+"");

                }

                cursorLoader = new CursorLoader(
                        getActivity(), baseUri, SqlOpenHelper.LOADER_COLUMNS, null, null, null);

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
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProjectListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.projects_type = getActivity().getIntent().getIntExtra(ProjectListActivity.PROJECT_STATUS, 1);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(GET_PROJECTS_BY_STATUS, null, this);
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
        else {
            mCallbacks = (Callbacks) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = null;
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
        if(mCallbacks != null) {
            try {
                mCallbacks.onItemSelected(new Project(cursor));
            } catch (ParseException e) {
                Log.e(TAG, e.toString());
            }
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

            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();

        }

    };
}
