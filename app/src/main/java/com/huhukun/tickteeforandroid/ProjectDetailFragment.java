package com.huhukun.tickteeforandroid;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huhukun.tickteeforandroid.UILibrary.ClearableEditText;
import com.huhukun.tickteeforandroid.UILibrary.SeekArc;
import com.huhukun.tickteeforandroid.UILibrary.TextWatcherAdapter;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.DeleteTask;
import com.huhukun.tickteeforandroid.providers.NetworkUtils;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.tickteeforandroid.providers.UpdateTask;
import com.huhukun.tickteeforandroid.providers.WebApiConstants;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.NumberUtils;

//import org.droidparts.adapter.widget.TextWatcherAdapter;
//import org.droidparts.widget.ClearableEditText;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

/**
 * A fragment representing a single Project detail screen.
 * This fragment is either contained in a {@link ProjectListActivity}
 * in two-pane mode (on tablets) or a {@link ProjectDetailActivity}
 * on handsets.
 */
public class ProjectDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SeekArc.OnSeekArcChangeListener {
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

    private LinearLayout layoutStartEndLabels;
    private LinearLayout layoutStartEndValues;
    private TextView tvProjectStartAt;
    private TextView tvProjectEndAt;
    private TextView tvProjectExpectedProgress;
    private TextView tvProjectCurrentProgress;
    private TextView tvProjectCreatedAt;
    private TextView tvProjectLastUpdateAt;
    private TextView tvProjectDescription;
    private TextView tvSeekArcPercentage;
    private TextView tvSeekArcPercentageUnit;
    private TextView tvProjectPast;
    private TextView tvProjectPastDaily;
    private TextView tvProjectFuture;
    private TextView tvProjectFutureDaily;
    private TextView tvProjectProgress;
    private ClearableEditText etNewProgress;
    private SeekArc seekArc;

    private String sqlId;
    private ExecutorService executorPool;


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
        layoutStartEndLabels = (LinearLayout) rootView.findViewById(R.id.project_detail_start_end_date_labels);
        layoutStartEndValues = (LinearLayout) rootView.findViewById(R.id.project_detail_start_end_date_values);
        tvProjectStartAt = (TextView) rootView.findViewById(R.id.project_detail_start_at);
        tvProjectEndAt = (TextView) rootView.findViewById(R.id.project_detail_end_at);
        tvProjectExpectedProgress = (TextView) rootView.findViewById(R.id.project_detail_expected_progress);
        tvProjectCurrentProgress = (TextView) rootView.findViewById(R.id.project_detail_current_progress);
        tvProjectCreatedAt = (TextView) rootView.findViewById(R.id.project_detail_created_at);
        tvProjectLastUpdateAt = (TextView) rootView.findViewById(R.id.project_detail_last_update_at);
        tvProjectDescription = (TextView) rootView.findViewById(R.id.project_detail_description);
        tvProjectPast = (TextView) rootView.findViewById(R.id.project_detail_past);
        tvProjectPastDaily = (TextView) rootView.findViewById(R.id.project_detail_past_daily);
        tvProjectFuture = (TextView) rootView.findViewById(R.id.project_detail_future);
        tvProjectFutureDaily = (TextView) rootView.findViewById(R.id.project_detail_future_daily);
        tvSeekArcPercentage = (TextView) rootView.findViewById(R.id.seekArcProgress);
        tvSeekArcPercentageUnit = (TextView) rootView.findViewById(R.id.seekArcProgressUnit);
        tvProjectProgress = (TextView) rootView.findViewById(R.id.project_detail_percentage);
        etNewProgress = (ClearableEditText) rootView.findViewById(R.id.project_detail_new_progress);
        etNewProgress.setListener(new ClearableEditText.Listener() {
            @Override
            public void didClearText() {
                updateProgress(null);
            }
        });
        etNewProgress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateProgress(etNewProgress.getText().toString().trim());
//                    handled = true;
                }
                return handled;
            }
        });
        seekArc = (SeekArc) rootView.findViewById(R.id.seekArc);
        seekArc.setOnSeekArcChangeListener(this);
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
                        getActivity(), baseUri, SqlOpenHelper.LOADER_COLUMNS, null, null, null);

                break;
            default:
                throw new IllegalStateException(
                        "Cannot create Loader with id[" + id + "]");
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NotNull Loader<Cursor> loader, Cursor cursor) {

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
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    private void showProjectDetail(Project project) {
        if (project != null) {
            getActivity().setTitle(mItem.getName());
            if(mItem.getStartDate() == null )
            {
                layoutStartEndLabels.setVisibility(View.GONE);
                layoutStartEndValues.setVisibility(View.GONE);
            }
            else {
                tvProjectStartAt.setText(FormatHelper.toLocalDateString(mItem.getStartDate()));
                tvProjectEndAt.setText(FormatHelper.toLocalDateString(mItem.getEndDate()));
            }
            tvProjectExpectedProgress.setText(mItem.getExpectedProgress().toPlainString());
            tvProjectCurrentProgress.setText(mItem.getCurrentProgress().toPlainString());
            tvProjectCreatedAt.setText(FormatHelper.toLocalDateTimeString(mItem.getCreatedTime()));
            tvProjectLastUpdateAt.setText(FormatHelper.toLocalDateTimeString(mItem.getLastUpdateTime()));
            tvProjectDescription.setText(mItem.getDescription());
            tvProjectPastDaily.setText("Daily: "+NumberUtils.decimalToString(mItem.getPastDaily(), mItem.isDecimalUnit())+" "+mItem.getUnit());
            tvProjectFutureDaily.setText("Daily: "+NumberUtils.decimalToString(mItem.getFutureDaily(), mItem.isDecimalUnit())+" "+mItem.getUnit());
            tvProjectProgress.setText(String.format("%d%%",mItem.currentPercentage()));
            int startAngle = NumberUtils.getAngle(mItem.getCurrentProgress(), mItem.getTarget());
            int expectedAngle = NumberUtils.getAngle(mItem.getExpectedPercentage());
            if(mItem.isConsumed()){
                tvSeekArcPercentage.setText(NumberUtils.decimalToString(mItem.getTarget().subtract(mItem.getCurrentProgress()), mItem.isDecimalUnit()));
                seekArc.setStartAngle(startAngle);
                if(expectedAngle == 0) expectedAngle = 360;
                seekArc.setExpectedAngle(expectedAngle);
                seekArc.setReverse();
                tvProjectPast.setText(mItem.currentPercentage()+"% gone so far");
                tvProjectFuture.setText(mItem.restPercentage()+"% left in next "+mItem.getRestDay()+" days on schedule");
            }
            else
            {
                tvProjectPast.setText(mItem.currentPercentage()+"% finished so far");
                tvProjectFuture.setText(mItem.restPercentage()+"% to be done in next "+mItem.getRestDay()+" days on schedule");
                seekArc.setStartAngle(startAngle);
                seekArc.setExpectedAngle(expectedAngle);
                tvSeekArcPercentage.setText(NumberUtils.decimalToString(mItem.getCurrentProgress(), mItem.isDecimalUnit()));
            }
            tvSeekArcPercentageUnit.setText(mItem.getUnit());
            if (mItem.getUnit()!=null && !mItem.getUnit().trim().isEmpty()){
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tvSeekArcPercentageUnit.getLayoutParams();
                params.setMargins(5, 0, 0, 0); //substitute parameters for left, top, right, bottom
                tvSeekArcPercentageUnit.setLayoutParams(params);
            }

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
            if (mItem.isConsumed()){
                mItem.setCurrentProgress(mItem.getTarget().subtract(new BigDecimal(tvSeekArcPercentage.getText().toString())));
            }
            else {
                mItem.setCurrentProgress(new BigDecimal(tvSeekArcPercentage.getText().toString()));
            }
            UpdateTask task = new UpdateTask(mItem.getId(), mItem);
            executorPool.submit(task);
        }
    }

    public int removeProject() {
        if(mItem !=null)
        {

            DeleteTask task = new DeleteTask(mItem.getId());
            executorPool.submit(task);
            return 1;
        }
        return -1;
    }


    @Override
    public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
        if(fromUser){
            tvProjectProgress.setText(String.format("%d%%", progress+mItem.currentPercentage()));
            BigDecimal newProgress =  NumberUtils.getNumberFromPercentage(progress, mItem.getTarget());
            if(mItem.isConsumed()){
                tvSeekArcPercentage.setText(NumberUtils.decimalToString(mItem.getTarget().subtract(mItem.getCurrentProgress().add(newProgress)), mItem.isDecimalUnit()));
            }else {
                this.tvSeekArcPercentage.setText(NumberUtils.decimalToString(mItem.getCurrentProgress().add(newProgress), mItem.isDecimalUnit()));
            }
//            this.etNewProgress.setText(NumberUtils.decimalToString(newProgress, mItem.isDecimalUnit()));

        }
    }

    @Override
    public void onStartTrackingTouch(SeekArc seekArc) {

    }

    @Override
    public void onStopTrackingTouch(SeekArc seekArc) {

    }


    private void updateProgress(String progress) {
        if(progress == null || progress.isEmpty()) progress = "0";
        if(new BigDecimal(progress).compareTo(mItem.getTarget().subtract(mItem.getCurrentProgress())) > 0)
            progress = NumberUtils.decimalToString(mItem.getTarget().subtract(mItem.getCurrentProgress()), mItem.isDecimalUnit());
        BigDecimal currentProgress = new BigDecimal(progress);
        int percentage = NumberUtils.getPercentage(currentProgress, mItem.getTarget());
        seekArc.setProgress(percentage);
        tvProjectProgress.setText(String.format("%d%%",mItem.currentPercentage()+percentage));
        if(mItem.isConsumed()){
            tvSeekArcPercentage.setText(NumberUtils.decimalToString(mItem.getTarget().subtract(mItem.getCurrentProgress().add(currentProgress)), mItem.isDecimalUnit()));
        }
        else{
            tvSeekArcPercentage.setText(NumberUtils.decimalToString(mItem.getCurrentProgress().add(currentProgress), mItem.isDecimalUnit()));
        }
//        etNewProgress.setText(progress);
    }

    public void sharing(){
        saveProgress();
        String sharing = String.format("I have finished %s of my progress %s. That's awesome!", tvProjectProgress.getText(), mItem.getName());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharing);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent,getString(R.string.sharing_with)));
    }
}
