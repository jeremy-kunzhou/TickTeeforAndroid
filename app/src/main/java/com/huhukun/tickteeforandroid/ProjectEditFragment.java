package com.huhukun.tickteeforandroid;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huhukun.tickteeforandroid.UILibrary.DatePickerFragment;

import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.InsertTask;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.tickteeforandroid.providers.UpdateTask;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.NumberUtils;

import org.w3c.dom.Text;

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
    private EditText etTarget;
    private EditText etInitProgress;
    private AutoCompleteTextView tvUnit;
    private Spinner spinnerAlertType;
    private CheckBox checkBoxUseStartEndDate;
    private LinearLayout layoutStartEndLabels;
    private LinearLayout layoutStartEndValues;
    private TextView tvStartAt;
    private TextView tvEndAt;
    private CheckBox checkBoxIsConsumed;
    private CheckBox checkBoxIsDecimalUnit;



    private EditText etDescription;

    private ExecutorService executorPool;

    private View.OnClickListener checkBoxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkBoxClicked(v);
        }
    };

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
        spinnerAlertType = (Spinner) rootView.findViewById(R.id.project_edit_alert_spinner);
        ArrayAdapter<CharSequence> adapterAlert = ArrayAdapter.createFromResource(getActivity(), R.array.alert_array, android.R.layout.simple_spinner_item);
        adapterAlert.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlertType.setAdapter(adapterAlert);
        tvUnit = (AutoCompleteTextView) rootView.findViewById(R.id.project_edit_unit);
        String[] units = getResources().getStringArray(R.array.unit_array);
        ArrayAdapter<String> adapterUnit = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1, units);
        tvUnit.setAdapter(adapterUnit);
        etTarget = (EditText) rootView.findViewById(R.id.project_edit_target);
        etInitProgress = (EditText) rootView.findViewById(R.id.project_edit_init_progress);
        layoutStartEndLabels = (LinearLayout) rootView.findViewById(R.id.project_edit_start_end_labels);
        layoutStartEndValues = (LinearLayout) rootView.findViewById(R.id.project_edit_start_end_values);
        checkBoxUseStartEndDate = (CheckBox) rootView.findViewById(R.id.project_edit_checkBoxUseStartEndDate);
        checkBoxUseStartEndDate.setOnClickListener(this.checkBoxListener);
        checkBoxIsConsumed = (CheckBox) rootView.findViewById(R.id.project_edit_consumed);
        checkBoxIsConsumed.setOnClickListener(this.checkBoxListener);
        checkBoxIsDecimalUnit = (CheckBox) rootView.findViewById(R.id.project_edit_decimal_unit);
        checkBoxIsDecimalUnit.setOnClickListener(this.checkBoxListener);
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mItem = null;
    }

    private void showProjectDetail(Project project) {
        etName.setText(project.getName());
        etTarget.setText(NumberUtils.decimalToString(project.getTarget(), project.isDecimalUnit()));
        etInitProgress.setText(NumberUtils.decimalToString(project.getInitProgress(), project.isDecimalUnit()));
        tvUnit.setText(project.getUnit());
        spinnerAlertType.setSelection(project.getAlertType().ordinal());
        if(mItem.getStartDate() == null )
        {
            checkBoxUseStartEndDate.setChecked(false);
            tvStartAt.setText("");
            tvEndAt.setText("");
        }
        else {
            checkBoxUseStartEndDate.setChecked(true);
            layoutStartEndLabels.setVisibility(View.VISIBLE);
            layoutStartEndValues.setVisibility(View.VISIBLE);
            tvStartAt.setText(FormatHelper.toLocalDateString(project.getStartDate()));
            tvEndAt.setText(FormatHelper.toLocalDateString(project.getEndDate()));
        }
        checkBoxIsConsumed.setChecked(project.isConsumed());
        checkBoxIsDecimalUnit.setChecked(project.isDecimalUnit());
        etDescription.setText(mItem.getDescription());
        etInitProgress.setEnabled(false);
        checkBoxIsConsumed.setEnabled(false);


    }

    public void saveProject() throws ParseException {
        if(validInput())
        {
            mItem.setName(etName.getText().toString().trim());
            mItem.setDescription(etDescription.getText().toString().trim());
            if(checkBoxUseStartEndDate.isChecked()) {
                mItem.setStartDate(FormatHelper.fromLocalDateStringToUTC(tvStartAt.getText().toString()+" 00:00:00"));
                mItem.setEndDate(FormatHelper.fromLocalDateStringToUTC(tvEndAt.getText().toString() + " 23:59:59"));
            }
            else {
                mItem.setStartDate(null);
                mItem.setEndDate(null);
            }
            mItem.setTarget(new BigDecimal(etTarget.getText().toString()));
            mItem.setUnit(tvUnit.getText().toString());
            mItem.setAlertType(Project.AlertType.parse(spinnerAlertType.getSelectedItem().toString()));
            mItem.setDecimalUnit(checkBoxIsDecimalUnit.isChecked());
            Date date = new Date();
            mItem.setLastUpdateTime(date);
            UpdateTask task = new UpdateTask(mItem.getId(), mItem);
            executorPool.submit(task);
        }
        else {
            Toast.makeText(this.getActivity(), R.string.invalid_input, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setCallBack(this);
        datePickerFragment.setTargetId(view.getId());
        datePickerFragment.show(getFragmentManager(), "timePicker");
    }

    public void checkBoxClicked(View view){
        switch (view.getId()) {
            case R.id.project_edit_checkBoxUseStartEndDate:
                if (this.checkBoxUseStartEndDate.isChecked()) {
                    this.layoutStartEndValues.setVisibility(View.VISIBLE);
                    this.layoutStartEndLabels.setVisibility(View.VISIBLE);
                    Calendar cal = Calendar.getInstance();
                    tvStartAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    tvEndAt.setText(FormatHelper.shortLocalDateFormatter.format(cal.getTime()));
                } else {
                    this.layoutStartEndValues.setVisibility(View.GONE);
                    this.layoutStartEndLabels.setVisibility(View.GONE);
                    this.tvEndAt.setText("");
                    this.tvStartAt.setText("");
                }
                break;
            case R.id.project_edit_consumed:
                break;
            case R.id.project_edit_decimal_unit:
                break;
        }
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
            if(checkBoxUseStartEndDate.isChecked()) {
                if (!tvStartAt.getText().toString().isEmpty()) {
                    project.setStartDate(FormatHelper.fromLocalDateStringToUTC(tvStartAt.getText().toString()+" 00:00:00"));
                }
                if (!tvEndAt.getText().toString().isEmpty()) {
                    project.setEndDate(FormatHelper.fromLocalDateStringToUTC(tvEndAt.getText().toString()+" 23:59:59"));
                }
            }
            project.setExpectedProgress(BigDecimal.ZERO);
            project.setCurrentProgress(BigDecimal.ZERO);
            project.setTarget(new BigDecimal(etTarget.getText().toString()));
            project.setUnit(tvUnit.getText().toString());
            project.setAlertType(Project.AlertType.parse(spinnerAlertType.getSelectedItem().toString()));
            project.setConsumed(checkBoxIsConsumed.isChecked());
            project.setDecimalUnit(checkBoxIsDecimalUnit.isChecked());
            if (TextUtils.isEmpty(etInitProgress.getText())){
                project.setInitProgress(BigDecimal.ZERO);
            }
            else {
                project.setInitProgress(new BigDecimal(etInitProgress.getText().toString()));
            }
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
        if(result && TextUtils.isEmpty(etName.getText())){
            etName.setError(getString(R.string.emptyName));
            result = false;
        }
        if (result && TextUtils.isEmpty(etTarget.getText())) {
            etTarget.setError(getString(R.string.emptyTarget));
            result = false;
        }

        if (result && TextUtils.isEmpty(tvUnit.getText())) {
            tvUnit.setError(getString(R.string.emptyUnit));
            result = false;
        }
        return result;
    }

}
