package com.huhukun.tickteeforandroid;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.huhukun.tickteeforandroid.UILibrary.DatePickerFragment;
import com.huhukun.tickteeforandroid.UILibrary.SeekArc;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.ProjectsManagementImpl;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.utils.FormatHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants;

/**
 * A fragment representing a single Project detail screen.
 * This fragment is either contained in a {@link ProjectListActivity}
 * in two-pane mode (on tablets) or a {@link ProjectDetailActivity}
 * on handsets.
 */
public class ProjectDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerFragment.IDatePickerFragmentCallBack, View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = App_Constants.APP_TAG +"ProjectDetailFragment";
    /**
     * The dummy content this fragment is presenting.
     */
    private Project mItem;

    private static final int GET_PROJECT_BY_ID = 1;

    private TextView tvProjectName ;
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
    private LoaderManager loaderManager;
    private CursorLoader cursorLoader;
    private int startPercent = 0;
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
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProjectDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = ProjectsManagementImpl.getInstance().getProjectById(Long.parseLong(getArguments().getString(ARG_ITEM_ID)));
            sqlId = getArguments().getString(ARG_ITEM_ID);
            loaderManager = getLoaderManager();
            loaderManager.initLoader(GET_PROJECT_BY_ID, null, this);
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_project_detail, container, false);
        tvProjectName = (TextView) rootView.findViewById(R.id.project_detail_name);
        tvProjectStartAt = (TextView) rootView.findViewById(R.id.project_detail_start_at);
        tvProjectStartAt.setOnClickListener(this);
        tvProjectEndAt = (TextView) rootView.findViewById(R.id.project_detail_end_at);
        tvProjectEndAt.setOnClickListener(this);
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
                tvSeekArcPercentage.setText(startPercent+progress+"");
                tvProjectCurrentProgress.setText(startPercent+progress+"");
            }
        });



        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Mark as pending so the SyncAdapter knows to request
        // new data from the REST API.
        QueryTransactionInfo.getInstance().markPending();

        switch (id) {
            case GET_PROJECT_BY_ID:
                Uri baseUri =
                            Uri.withAppendedPath(TickteeProvider.CONTENT_URI,
                                    Uri.encode(sqlId) );


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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
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
        mItem = null;
        showProjectDetail(null);
        Toast.makeText(getActivity(), R.string.no_record_found, Toast.LENGTH_LONG);
    }



    private void showProjectDetail(Project project)
    {
        if (project != null)
        {

            tvProjectName.setText(mItem.getName());
            tvProjectStartAt.setText(FormatHelper.shortLocalDateFormatter.format(mItem.getStartDate()));
            tvProjectEndAt.setText(FormatHelper.shortLocalDateFormatter.format(mItem.getEndDate()));
            tvProjectExpectedProgress.setText(mItem.getExpectedProgress().toPlainString());
            tvProjectCurrentProgress.setText(mItem.getCurrentProgress().toPlainString());
            tvProjectCreatedAt.setText(FormatHelper.shortLocalDateTimeFormatter.format(mItem.getCreatedTime()));
            tvProjectLastUpdateAt.setText(FormatHelper.shortLocalDateTimeFormatter.format(mItem.getLastUpdateTime()));
            tvProjectDescription.setText(mItem.getDescription());
            startPercent =  (int)mItem.getExpectedProgress().doubleValue();
            seekArc.setStartAngle((int)(mItem.getExpectedProgress().doubleValue() / 100 * 360));
            tvSeekArcPercentage.setText(startPercent+"");

        }
        else {
            tvProjectName.setText("");
            tvProjectStartAt.setText("");
            tvProjectEndAt.setText("");
            tvProjectExpectedProgress.setText("");
            tvProjectCurrentProgress.setText("");
            tvProjectCreatedAt.setText("");
            tvProjectLastUpdateAt.setText("");
            tvProjectDescription.setText("");
        }
    }

    @Override
    public void setDate(long targetId, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year,month,day);
        if(targetId == tvProjectStartAt.getId())
        {
            tvProjectStartAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));
        }
        else if (targetId == tvProjectEndAt.getId())
        {
            tvProjectEndAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));

        }
    }

    @Override
    public void onClick(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setCallBack(this);
        datePickerFragment.setTargetId(view.getId());
        datePickerFragment.show(getFragmentManager(), "timePicker");
    }
}
