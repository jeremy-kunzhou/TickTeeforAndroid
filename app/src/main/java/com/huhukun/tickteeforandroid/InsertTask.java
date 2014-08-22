package com.huhukun.tickteeforandroid;

import java.util.concurrent.Callable;

import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;
import com.huhukun.utils.FormatHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

public class InsertTask implements Callable<Boolean>{

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
        values.put( SqlOpenHelper.TableConstants.COL_START_AT,  FormatHelper.serverDateFormatter.format(project.getStartDate()));
        values.put( SqlOpenHelper.TableConstants.COL_END_AT, FormatHelper.serverDateFormatter.format(project.getEndDate()));
        values.put( SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS, project.getExpectedProgress().toString() );
        values.put( SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS, project.getCurrentProgress().toString() );
        values.put( SqlOpenHelper.TableConstants.COL_CREATED_AT,  FormatHelper.serverDateTimeFormatter.format(project.getCreatedTime()));
        values.put( SqlOpenHelper.TableConstants.COL_UPDATED_AT,  FormatHelper.serverDateTimeFormatter.format(project.getLastUpdateTime()));


        uri = cr.insert( TickteeProvider.CONTENT_URI_PROJECTS_PENDING, values );
        Log.d(TAG, uri.toString());
        if ( uri == null ) {
            Log.e( TAG, "Error setting insert request to PENDING status." );


            return false;
        }

        return true;
    }

}