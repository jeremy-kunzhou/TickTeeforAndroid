package com.huhukun.tickteeforandroid.providers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.utils.FormatHelper;

import java.util.concurrent.Callable;

public class InsertTask implements Callable<Boolean> {

    private static final String TAG = App_Constants.APP_TAG +"InsertTask";

    private Project project;

    public InsertTask(Project project )
    {
        this.project = project;
    }

    /**
     * Insert a row into the table with data entered by the user.
     * Set the status to "POST" and transacting flag to "pending".
     */
    @Override
    public Boolean call()
    {
        ContentResolver cr = TickTeeAndroid.getAppContext().getContentResolver();
        ContentValues values = new ContentValues();
        Uri uri;

        values.put( SqlOpenHelper.TableConstants.COL_NAME, project.getName() );
        values.put( SqlOpenHelper.TableConstants.COL_DESCRIPTION, project.getDescription() );
        values.put( SqlOpenHelper.TableConstants.COL_START_AT,  FormatHelper.toUTCString(project.getStartDate()));
        values.put( SqlOpenHelper.TableConstants.COL_END_AT, FormatHelper.toUTCString( project.getEndDate()));
        values.put( SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS, project.getExpectedProgress().toString() );
        values.put( SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS, project.getCurrentProgress().toString() );
        values.put( SqlOpenHelper.TableConstants.COL_CREATED_AT,  FormatHelper.toUTCString(project.getCreatedTime()));
        values.put( SqlOpenHelper.TableConstants.COL_UPDATED_AT,  FormatHelper.toUTCString(project.getLastUpdateTime()));
        values.put( SqlOpenHelper.TableConstants.COL_TARGET, project.getTarget().toString());
        values.put( SqlOpenHelper.TableConstants.COL_UNIT, project.getUnit());
        values.put( SqlOpenHelper.TableConstants.COL_ALERT_TYPE, project.getAlertType().toString());
        values.put( SqlOpenHelper.TableConstants.COL_IS_CONSUMED, project.isConsumed()? 1:0);
        values.put( SqlOpenHelper.TableConstants.COL_SCHEDULE, project.getSchedule());
        values.put( SqlOpenHelper.TableConstants.COL_IS_DECIMAL, project.isDecimalUnit()? 1:0);
        values.put( SqlOpenHelper.TableConstants.COL_INIT_PROGRESS, project.getInitProgress().toString());

        Log.d(TAG, values.toString());
        uri = cr.insert( TickteeProvider.CONTENT_URI_PROJECTS_PENDING, values );
        Log.d(TAG, uri.toString());
        if ( uri == null ) {
            Log.e( TAG, "Error setting insert request to PENDING status." );


            return false;
        }

        return true;
    }

}