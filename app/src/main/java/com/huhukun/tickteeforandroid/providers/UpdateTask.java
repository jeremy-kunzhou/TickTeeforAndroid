package com.huhukun.tickteeforandroid.providers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.utils.FormatHelper;

import java.util.concurrent.Callable;

public class UpdateTask implements Callable<Boolean> {

    private static final String TAG = App_Constants.APP_TAG +"UpdateTask";

    private long mUpdateId;
    private Project project;

    public UpdateTask( long updateId, Project project)
    {
        mUpdateId = updateId;
        this.project = project;
    }

    /**
     * Update the table with data entered by the user.
     * Set the status to "PUT" and transacting flag to "pending".
     */
    @Override
    public Boolean call()
    {
        ContentResolver cr = TickTeeAndroid.getAppContext().getContentResolver();
        ContentValues values = new ContentValues();
        Uri uri;
        int updateCount;

        uri = ContentUris.withAppendedId(
                TickteeProvider.CONTENT_URI_PROJECTS_PENDING,
                mUpdateId);

        values.put( SqlOpenHelper.TableConstants.COL_NAME, project.getName() );
        values.put( SqlOpenHelper.TableConstants.COL_DESCRIPTION, project.getDescription() );
        values.put( SqlOpenHelper.TableConstants.COL_START_AT,  FormatHelper.UseDateFormatter(FormatHelper.serverDateFormatter, project.getStartDate()));
        values.put( SqlOpenHelper.TableConstants.COL_END_AT, FormatHelper.UseDateFormatter(FormatHelper.serverDateFormatter, project.getEndDate()));
        values.put( SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS, project.getExpectedProgress().toString() );
        values.put( SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS, project.getCurrentProgress().toString() );
        values.put( SqlOpenHelper.TableConstants.COL_CREATED_AT,  FormatHelper.serverDateTimeFormatter.format(project.getCreatedTime()));
        values.put( SqlOpenHelper.TableConstants.COL_UPDATED_AT,  FormatHelper.serverDateTimeFormatter.format(project.getLastUpdateTime()));
        values.put( SqlOpenHelper.TableConstants.COL_TARGET, project.getTarget().toString());
        values.put( SqlOpenHelper.TableConstants.COL_UNIT, project.getUnit());
        values.put( SqlOpenHelper.TableConstants.COL_ALERT_TYPE, project.getAlertType().toString());
        values.put( SqlOpenHelper.TableConstants.COL_IS_CONSUMED, project.isConsumed());
        values.put( SqlOpenHelper.TableConstants.COL_IS_DECIMAL, project.isDecimalUnit());
        values.put( SqlOpenHelper.TableConstants.COL_INIT_PROGRESS, project.getInitProgress().toString());

        updateCount = cr.update( uri, values, null, null );

        if ( updateCount == 0 ) {
            Log.e(TAG, "Error setting update request to PENDING status.");


            return false;
        }

        return true;
    }

}
