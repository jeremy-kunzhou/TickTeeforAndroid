package com.huhukun.tickteeforandroid;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.UILibrary.SeekArc;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.tickteeforandroid.providers.WebApiConstants;
import com.huhukun.utils.FormatHelper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants;

/**
 * A fragment representing a single Project detail screen.
 * This fragment is either contained in a {@link ProjectListActivity}
 * in two-pane mode (on tablets) or a {@link ProjectDetailActivity}
 * on handsets.
 */
public class ProjectDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = App_Constants.APP_TAG + "ProjectDetailFragment";
    /**
     * The dummy content this fragment is presenting.
     */
    private Project mItem;
    private static final int GET_PROJECT_BY_ID = 1;

    private TextView tvProjectStartAt;
    private TextView tvProjectEndAt;
    private TextView tvProjectExpectedProgress;
    private TextView tvProjectCurrentProgress;
    private TextView tvProjectCreatedAt;
    private TextView tvProjectLastUpdateAt;
    private TextView tvProjectDescription;
    private TextView tvSeekArcPercentage;
    private SeekArc seekArc;

    private String sqlId;
    private int startPercent = 0;
    private ExecutorService executorPool;

    private static final String[] loaderColumns = new String[]{
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
            TableConstants.COL_TRANS_DATE};

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProjectDetailFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
//            sqlId = getArguments().getString(ARG_ITEM_ID);
//            loaderManager = getLoaderManager();
//            loaderManager.initLoader(GET_PROJECT_BY_ID, null, this);
//        }
        executorPool = Executors.newSingleThreadExecutor();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_project_detail, container, false);
        tvProjectStartAt = (TextView) rootView.findViewById(R.id.project_detail_start_at);
        tvProjectEndAt = (TextView) rootView.findViewById(R.id.project_detail_end_at);
        tvProjectExpectedProgress = (TextView) rootView.findViewById(R.id.project_detail_expected_progress);
        tvProjectCurrentProgress = (TextView) rootView.findViewById(R.id.project_detail_current_progress);
        tvProjectCreatedAt = (TextView) rootView.findViewById(R.id.project_detail_created_at);
        tvProjectLastUpdateAt = (TextView) rootView.findViewById(R.id.project_detail_last_update_at);
        tvProjectDescription = (TextView) rootView.findViewById(R.id.project_detail_description);
        tvSeekArcPercentage = (TextView) rootView.findViewById(R.id.seekArcProgress);
        seekArc = (SeekArc) rootView.findViewById(R.id.seekArc);

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                tvSeekArcPercentage.setText(startPercent + progress + "");
                tvProjectCurrentProgress.setText(startPercent + progress + "");
            }
        });


        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            if (sqlId == null) {
                sqlId = getArguments().getString(ARG_ITEM_ID);
            }
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GET_PROJECT_BY_ID, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Mark as pending so the SyncAdapter knows to request
        // new data from the REST API.
        QueryTransactionInfo.getInstance().markPending();
        CursorLoader cursorLoader;
        switch (id) {
            case GET_PROJECT_BY_ID:
                Uri baseUri =
                        Uri.withAppendedPath(TickteeProvider.CONTENT_URI,
                                Uri.encode(sqlId));


                cursorLoader = new CursorLoader(
                        getActivity(), baseUri, loaderColumns, null, null, null);

                break;
            default:
                throw new IllegalStateException(
                        "Cannot create Loader with id[" + id + "]");
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            try {
                mItem = new Project(cursor);
                showProjectDetail(mItem);
            } catch (ParseException e) {
                Log.e(TAG, e.toString());
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void showProjectDetail(Project project) {
        if (project != null) {
            getActivity().setTitle(mItem.getName());
            tvProjectStartAt.setText(FormatHelper.shortLocalDateFormatter.format(mItem.getStartDate()));
            tvProjectEndAt.setText(FormatHelper.shortLocalDateFormatter.format(mItem.getEndDate()));
            tvProjectExpectedProgress.setText(mItem.getExpectedProgress().toPlainString());
            tvProjectCurrentProgress.setText(mItem.getCurrentProgress().toPlainString());
            tvProjectCreatedAt.setText(FormatHelper.shortLocalDateTimeFormatter.format(mItem.getCreatedTime()));
            tvProjectLastUpdateAt.setText(FormatHelper.shortLocalDateTimeFormatter.format(mItem.getLastUpdateTime()));
            tvProjectDescription.setText(mItem.getDescription());
            startPercent = (int) mItem.getCurrentProgress().doubleValue();
            seekArc.setStartAngle((int) (mItem.getCurrentProgress().doubleValue() / 100 * 360));
            tvSeekArcPercentage.setText(startPercent + "");

        } else {
            tvProjectStartAt.setText("");
            tvProjectEndAt.setText("");
            tvProjectExpectedProgress.setText("");
            tvProjectCurrentProgress.setText("");
            tvProjectCreatedAt.setText("");
            tvProjectLastUpdateAt.setText("");
            tvProjectDescription.setText("");
        }
    }


    public void saveProgress() {
        if(mItem!=null)
        {
            mItem.setCurrentProgress(new BigDecimal(tvSeekArcPercentage.getText().toString()));
            WebApiConstants.UpdateTask task = new WebApiConstants.UpdateTask(mItem.getId(), mItem);
            executorPool.submit(task);
        }
    }

    public int removeProject() {
        if(mItem !=null)
        {

            WebApiConstants.DeleteTask task = new WebApiConstants.DeleteTask(mItem.getId());
            executorPool.submit(task);
            return 1;
        }
        return -1;
    }
}
