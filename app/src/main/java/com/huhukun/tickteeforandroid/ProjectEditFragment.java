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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huhukun.tickteeforandroid.UILibrary.DatePickerFragment;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.*;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.InsertTask;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.tickteeforandroid.providers.UpdateTask;
import com.huhukun.tickteeforandroid.providers.WebApiConstants;
import com.huhukun.utils.FormatHelper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kun on 22/08/2014.
 */
public class ProjectEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener,
        DatePickerFragment.IDatePickerFragmentCallBack{

    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = App_Constants.APP_TAG + "ProjectEditFragment";
    private String sqlId;
    private static final int GET_PROJECT_BY_ID = 1;
    private Project mItem;
    private EditText etName;
    private TextView tvStartAt;
    private TextView tvEndAt;
    private EditText etDescription;
    private ExecutorService executorPool;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            if(sqlId == null) {
                sqlId = getArguments().getString(ARG_ITEM_ID);
            }
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GET_PROJECT_BY_ID, null, this);
        }
        executorPool = Executors.newSingleThreadExecutor();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_project_edit, container, false);
        tvStartAt = (TextView) rootView.findViewById(R.id.project_edit_start_at);
        tvStartAt.setOnClickListener(this);
        tvEndAt = (TextView) rootView.findViewById(R.id.project_edit_end_at);
        tvEndAt.setOnClickListener(this);
        etName = (EditText) rootView.findViewById(R.id.project_edit_name);
        etDescription = (EditText) rootView.findViewById(R.id.project_edit_description);
        Calendar cal = Calendar.getInstance();
        tvStartAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        tvEndAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));
        return rootView;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Mark as pending so the SyncAdapter knows to request
        // new data from the REST API.
        QueryTransactionInfo.getInstance().markPending();
        CursorLoader cursorLoader = null;
        switch (id) {
            case GET_PROJECT_BY_ID:
                Uri baseUri =
                        Uri.withAppendedPath(TickteeProvider.CONTENT_URI,
                                Uri.encode(sqlId));


                cursorLoader = new CursorLoader(
                        getActivity(), baseUri, SqlOpenHelper.LOADER_COLUMNS, null, null, null);

                break;
            default:
                throw new IllegalStateException(
                        "Cannot create Loader with id[" + id + "]");
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            try {
                mItem = new Project(cursor);
                showProjectDetail(mItem);
            } catch (ParseException e) {
                Log.e(TAG, e.toString());
            }

        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mItem = null;
    }

    private void showProjectDetail(Project project) {
        if (project != null) {
            etName.setText(project.getName());
            if(mItem.getStartDate() == null )
            {
                tvStartAt.setText("");
                tvEndAt.setText("");
            }
            else {
                tvStartAt.setText(FormatHelper.shortLocalDateFormatter.format(project.getStartDate()));
                tvEndAt.setText(FormatHelper.shortLocalDateFormatter.format(project.getEndDate()));
            }
            etDescription.setText(mItem.getDescription());


        } else {
            etName.setText("");
            tvStartAt.setText("");
            tvEndAt.setText("");
            etDescription.setText("");

        }

    }

    public void saveProject() throws ParseException {
        Log.d(TAG, "save project");
        mItem.setName(etName.getText().toString().trim());
        Log.d(TAG, "save project id "+mItem.getProjectId());
        mItem.setDescription(etDescription.getText().toString().trim());
        mItem.setStartDate(FormatHelper.shortLocalDateFormatter.parse(tvStartAt.getText().toString()));
        mItem.setEndDate(FormatHelper.shortLocalDateFormatter.parse(tvEndAt.getText().toString()));
        UpdateTask task = new UpdateTask(mItem.getId(), mItem);
        executorPool.submit(task);
    }

    @Override
    public void onClick(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setCallBack(this);
        datePickerFragment.setTargetId(view.getId());
        datePickerFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void setDate(long targetId, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        if (targetId == tvStartAt.getId()) {
            tvStartAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));
        } else if (targetId == tvEndAt.getId()) {
            tvEndAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));

        }
    }

    public long addProject() throws ParseException {
        if(validInput())
        {
            Project project = new Project();
            project.setName(etName.getText().toString().trim());
            project.setDescription(etDescription.getText().toString().trim());
            project.setStartDate(FormatHelper.shortLocalDateFormatter.parse(tvStartAt.getText().toString()));
            project.setEndDate(FormatHelper.shortLocalDateFormatter.parse(tvEndAt.getText().toString()));
            project.setExpectedProgress(BigDecimal.ZERO);
            project.setCurrentProgress(BigDecimal.ZERO);
            project.setTarget(new BigDecimal("300"));
            project.setUnit("DSD");
            project.setAlertType(Project.AlertType.OFF);
            project.setConsumed(false);
            project.setDecimalUnit(false);
            project.setInitProgress(BigDecimal.ZERO);
            Date date = new Date();
            project.setCreatedTime(date);
            project.setLastUpdateTime(date);
            InsertTask task = new InsertTask(project);
            executorPool.submit(task);
            return 1;
        }
        else {
            Toast.makeText(this.getActivity(), R.string.invalid_input, Toast.LENGTH_LONG).show();
        }
        return -1;
    }

    private boolean validInput() {
        boolean result = true;
        if(etName.getText().toString().trim().isEmpty()) result = false;
        if (result && tvStartAt.getText().toString().isEmpty()) result = false;
        if (result && tvEndAt.getText().toString().isEmpty()) result = false;
        return result;
    }
}
