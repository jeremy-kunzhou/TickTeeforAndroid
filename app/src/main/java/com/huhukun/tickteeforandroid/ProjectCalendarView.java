package com.huhukun.tickteeforandroid;

import android.database.Cursor;
import android.net.Uri;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
//import android.support.v4.widget.CursorAdapter;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.huhukun.tickteeforandroid.CalendarView.Day;
import com.huhukun.tickteeforandroid.CalendarView.ExtendedCalendarView;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.QueryTransactionInfo;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.utils.FormatHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class ProjectCalendarView extends AppCompatActivity implements ExtendedCalendarView.OnDayClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = App_Constants.APP_TAG + "ProjectCalendarView";

    private static final String START_OF_DAY = "startOfDay";
    private static final String END_OF_DAY = "endOfDay";
    private static final String DAY_OF_WEEK = "dayOfWeek";
    CursorAdapter cursorAdapter;
    LoaderManager loaderManager;
    CursorLoader cursorLoader;

    private ExtendedCalendarView calendarView;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_calendar_view);
        getSupportActionBar().setTitle("Calendar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        calendarView = (ExtendedCalendarView)findViewById(R.id.project_calendar_view_calendar);
        calendarView.setOnDayClickListener(this);

        listView = (ListView)findViewById(R.id.project_calendar_listView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_calendar_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
        Log.d(TAG, "Click on some day");
        loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        String dateString = FormatHelper.shortLocalDateFormatter.format(day.getCal().getTime());
        Log.d(TAG, "select date "+dateString);
        try {
            bundle.putString(START_OF_DAY, FormatHelper.fromLocalDateTimeStringToUTCString(dateString + " 12:00 AM"));
            bundle.putString(END_OF_DAY,FormatHelper.fromLocalDateTimeStringToUTCString(dateString + " 11:59 PM"));
            switch (day.getDayOfWeek())
            {
                case Calendar.MONDAY:
                    bundle.putString(DAY_OF_WEEK,"1");
                    break;
                case Calendar.TUESDAY:
                    bundle.putString(DAY_OF_WEEK,"2");
                    break;
                case Calendar.WEDNESDAY:
                    bundle.putString(DAY_OF_WEEK,"4");
                    break;
                case Calendar.THURSDAY:
                    bundle.putString(DAY_OF_WEEK,"8");
                    break;
                case Calendar.FRIDAY:
                    bundle.putString(DAY_OF_WEEK,"16");
                    break;
                case Calendar.SATURDAY:
                    bundle.putString(DAY_OF_WEEK,"32");
                    break;
                case Calendar.SUNDAY:
                    bundle.putString(DAY_OF_WEEK,"64");
                    break;
            }

            loaderManager.restartLoader(0, bundle, this);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Mark as pending so the SyncAdapter knows to request
        // new data from the REST API.
        QueryTransactionInfo.getInstance().markPending();
        Uri baseUri =  Uri.withAppendedPath(TickteeProvider.CONTENT_URI_ON_DAY, bundle.getString(START_OF_DAY)+"/"+bundle.getString(END_OF_DAY)+"/"+ bundle.getString(DAY_OF_WEEK));
        cursorLoader = new CursorLoader(
                this, baseUri, SqlOpenHelper.LOADER_COLUMNS, null, null, null);
        return cursorLoader;
    }

//    @Override
//    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//
//    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor!=null) {
            cursorAdapter =
                    new ProjectCursorAdapter(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            listView.setAdapter(cursorAdapter);
            cursorAdapter.swapCursor(cursor); //swap the new cursor in.
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if(cursorAdapter!=null) {
            cursorAdapter.swapCursor(null);
        }
    }
}
