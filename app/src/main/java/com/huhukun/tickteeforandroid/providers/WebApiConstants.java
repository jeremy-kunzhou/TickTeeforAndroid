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

/**
 * Created by kun on 19/08/2014.
 */
public class WebApiConstants {
    public static final String PARAM_PROJECT = "project";
    public static final String PARAM_PROJECTS_ID = "id";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_START_AT = "start_at";
    public static final String PARAM_END_AT = "end_at";
    public static final String PARAM_EXPECTED_PROGRESS = "expected_progress";
    public static final String PARAM_CURRENT_PROGRESS = "current_progress";
    public static final String PARAM_CREATED_AT = "created_at";
    public static final String PARAM_UPDATED_AT = "updated_at";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_DATE_UPDATED = "trans_date";
    public static final String PARAM_DOWNLOAD_DATE = "downloadDate";
    public static final String PARAM_NEXT_DOWNLOAD_DATE = "";
    public static final String PARAM_SYNC_MODE = "";
    public static final String PARAM_SONGS_LIST = "";
    public static final String LOGIN_URL ;
    public static final String PROJECTS_URL;
    public static final String PROJECT_URL;
    public static final String BASE_URL = "http://192.168.2.3:3000/";

    public static final String HEADER_ACCESS_EMAIL_PARAM = "X_API_EMAIL";
    public static final String HEADER_ACCESS_TOKEN_PARM = "X_API_TOKEN";
    static {
        LOGIN_URL = BASE_URL + "users/sign_in.json";
        PROJECTS_URL = BASE_URL + "api/v1/projects.json";
        PROJECT_URL = BASE_URL + "api/v1/projects/%d.json";
    }

    public static class DeleteTask  implements Callable<Boolean> {

        private static final String TAG = App_Constants.APP_TAG +"DeleteTask";

        private long mDeleteId;

        public DeleteTask( long deleteId )
        {
            mDeleteId = deleteId;
        }

        /**
         * Set the status to "DELETE" and transacting flag to "pending".
         */
        @Override
        public Boolean call()
        {
            ContentResolver cr = TickTeeAndroid.getAppContext().getContentResolver();
            Uri uri;
            int deleteCount;

            uri = ContentUris.withAppendedId(
                    TickteeProvider.CONTENT_URI_PROJECTS_PENDING,
                    mDeleteId);

            deleteCount = cr.delete( uri, null, null );

            if ( deleteCount == 0 ) {
                Log.e(TAG, "Error setting delete request to PENDING status.");


                return false;
            }

            return true;
        }

    }

    public static class InsertTask implements Callable<Boolean>{

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

    public static class UpdateTask implements Callable<Boolean>{

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
                    mUpdateId );

            values.put( SqlOpenHelper.TableConstants.COL_NAME, project.getName() );
            values.put( SqlOpenHelper.TableConstants.COL_DESCRIPTION, project.getDescription() );
            values.put( SqlOpenHelper.TableConstants.COL_START_AT,  FormatHelper.serverDateFormatter.format(project.getStartDate()));
            values.put( SqlOpenHelper.TableConstants.COL_END_AT, FormatHelper.serverDateFormatter.format(project.getEndDate()));
            values.put( SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS, project.getExpectedProgress().toString() );
            values.put( SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS, project.getCurrentProgress().toString() );
            values.put( SqlOpenHelper.TableConstants.COL_CREATED_AT,  FormatHelper.serverDateTimeFormatter.format(project.getCreatedTime()));
            values.put( SqlOpenHelper.TableConstants.COL_UPDATED_AT,  FormatHelper.serverDateTimeFormatter.format(project.getLastUpdateTime()));

            updateCount = cr.update( uri, values, null, null );

            if ( updateCount == 0 ) {
                Log.e( TAG, "Error setting update request to PENDING status." );


                return false;
            }

            return true;
        }

    }
}
