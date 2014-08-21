package com.huhukun.tickteeforandroid.providers;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.utils.FormatHelper;

import java.util.ArrayList;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.*;

public class Processor {

    private static final String TAG = App_Constants.APP_TAG +"Processor";

    private static final Processor instance = new Processor();

    private Processor() {}

    private static final String[] columns =
            { SqlOpenHelper.TableConstants.COL_TRY_COUNT, SqlOpenHelper.TableConstants.COL_STATUS };

    public static Processor getInstance()
    {
        return instance;
    }

    /**
     * Physically delete the row form the local database.
     *
     * @param requestId The row that matches this value
     * of the _ID column will be deleted.
     */
    public int delete( long requestId )
    {
        final Context context = TickTeeAndroid.getAppContext();
        final ContentResolver cr = context.getContentResolver();
        int deleteCount;

        Uri uri;
        uri = ContentUris.withAppendedId(
                TickteeProvider.CONTENT_URI_PROJECTS_COMPLETED,
                requestId );

        deleteCount = cr.delete(uri, null, null);
        return deleteCount;
    }

    /**
     * Update the database content and set transactional
     * flags to a completed state. This method is used for
     * PUT and POST requests.
     *
     * @param detail The songs information that was parsed
     * from the http response.
     */
    public void update( Project detail )
    {
        final Context context = TickTeeAndroid.getAppContext();
        final ContentResolver cr = context.getContentResolver();
        ContentValues values;

        Uri uri;
        uri = ContentUris.withAppendedId(
                TickteeProvider.CONTENT_URI_PROJECTS_COMPLETED,
                detail.getRequestId() );

        values = new ContentValues();
        int updateCount;

        values.put( TableConstants.COL_PROJECT_ID,
                detail.getProjectId() );
        values.put( TableConstants.COL_NAME ,
                detail.getName() );
        values.put( TableConstants.COL_DESCRIPTION,
                detail.getDescription() );
        values.put( TableConstants.COL_TRANSACTING,
                App_Constants.TRANSACTION_COMPLETED );
        values.put( TableConstants.COL_RESULT ,
                detail.getHttpResult() );
        values.put( TableConstants.COL_TRY_COUNT,
                0 );

        updateCount = cr.update( uri, values, null, null );

    }

    /**
     * Looping through the list of responses a separate batch operation will be
     * defined for each of the three syncModes returned from the REST API. After
     * the list is exhausted, and all the rows have been loaded into an operation,
     * the three batches will be executed.
     *
     * syncMode U = Update
     * syncMode I = Insert
     * syncMode D = Delete
     *
     * @param details Array of song information that was parsed
     * from the http response.
     * @param result The HttpStatus code.
     */
    public void retrieve( Project[] details, long downloadDate, int result )
    {
        ContentResolver cr =   TickTeeAndroid.getAppContext().getContentResolver();
        ContentValues[] insertValues;
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        ArrayList< ContentProviderOperation > insertOps =
                new ArrayList< ContentProviderOperation >();
        ArrayList< ContentProviderOperation > updateOps =
                new ArrayList< ContentProviderOperation >();
        ArrayList< ContentProviderOperation > deleteOps =
                new ArrayList< ContentProviderOperation >();

        Uri pendingUri;


        prefs = TickTeeAndroid.getAppContext().getSharedPreferences(
                App_Constants.PREF_APP, 0 );
        editor = prefs.edit();

        // Put the date of this download in SharedPreferences
        editor.putLong(
                App_Constants.PREFS_DOWNLOAD_DATE,
               downloadDate);
        editor.commit();

        if ( details != null ) {
            // if success
            insertValues = new ContentValues[details.length];
            for ( int i = 0; i < details.length; i++ )
            {
                Project detail = details[i];


                switch ( detail.getSyncMode()) {
                    case U:

                        // build batch operation for updated rows

                        pendingUri = ContentUris.withAppendedId(
                                TickteeProvider.CONTENT_URI_PROJECTS_QUERY_COMPLETED,
                                detail.getProjectId() );

                        updateOps.add(
                                ContentProviderOperation.newUpdate(pendingUri)
                                        .withValue(TableConstants.COL_NAME,
                                                detail.getName())
                                        .withValue(TableConstants.COL_DESCRIPTION,
                                                detail.getDescription())
                                        .withValue(TableConstants.COL_START_AT,
                                                FormatHelper.serverDateFormatter.format(detail.getStartDate()))
                                        .withValue(TableConstants.COL_END_AT,
                                                FormatHelper.serverDateFormatter.format(detail.getEndDate()))
                                        .withValue(TableConstants.COL_EXPECTED_PROGRESS,
                                                detail.getExpectedProgress().toString())
                                        .withValue(TableConstants.COL_CURRENT_PROGRESS,
                                                detail.getCurrentProgress().toString())
                                        .withValue(TableConstants.COL_CREATED_AT,
                                                FormatHelper.serverDateTimeFormatter.format(detail.getCreatedTime()))
                                        .withValue(TableConstants.COL_UPDATED_AT,
                                                FormatHelper.serverDateTimeFormatter.format(detail.getLastUpdateTime()))
                                        .withValue(TableConstants.COL_TRANS_DATE,
                                                detail.getTransDate())
                                        .withValue(TableConstants.COL_RESULT,
                                                result)
                                        .withValue(TableConstants.COL_TRY_COUNT,
                                                0)
                                        .withValue(TableConstants.COL_TRANSACTING,
                                                App_Constants.TRANSACTION_COMPLETED)
                                        .withValue(TableConstants.COL_STATUS,
                                                MethodEnum.PUT.toString())
                                        .withYieldAllowed(true)
                                        .build());

                        break;
                    case I:

                        // build batch operation for inserted rows

                        insertOps.add(
                                ContentProviderOperation.newInsert(TickteeProvider.CONTENT_URI)
                                        .withValue( TableConstants.COL_PROJECT_ID,
                                                detail.getProjectId() )
                                        .withValue( TableConstants.COL_NAME,
                                                detail.getName() )
                                        .withValue( TableConstants.COL_DESCRIPTION,
                                                detail.getDescription() )
                                        .withValue(TableConstants.COL_START_AT,
                                                FormatHelper.serverDateFormatter.format(detail.getStartDate()))
                                        .withValue(TableConstants.COL_END_AT,
                                                FormatHelper.serverDateFormatter.format(detail.getEndDate()))
                                        .withValue(TableConstants.COL_EXPECTED_PROGRESS,
                                                detail.getExpectedProgress().toString())
                                        .withValue(TableConstants.COL_CURRENT_PROGRESS,
                                                detail.getCurrentProgress().toString())
                                        .withValue(TableConstants.COL_CREATED_AT,
                                                FormatHelper.serverDateTimeFormatter.format(detail.getCreatedTime()))
                                        .withValue(TableConstants.COL_UPDATED_AT,
                                                FormatHelper.serverDateTimeFormatter.format(detail.getLastUpdateTime()))
                                        .withValue(TableConstants.COL_TRANS_DATE,
                                                detail.getTransDate())
                                        .withValue( TableConstants.COL_RESULT,
                                                result )
                                        .withValue( TableConstants.COL_TRY_COUNT,
                                                0 )
                                        .withValue( TableConstants.COL_TRANSACTING,
                                                App_Constants.TRANSACTION_COMPLETED )
                                        .withValue( TableConstants.COL_STATUS,
                                                MethodEnum.NEW.toString() )
                                        .withYieldAllowed(true)
                                        .build());

                        break;
                    case D:

                        // build batch operation for deleted rows

                        pendingUri = ContentUris.withAppendedId(
                                TickteeProvider.CONTENT_URI_PROJECTS_QUERY_COMPLETED,
                                detail.getProjectId() );

                        deleteOps.add(
                                ContentProviderOperation.newDelete(pendingUri)
                                        .withYieldAllowed(true)
                                        .build());

                        break;

                    default:
                        Log.e( TAG, "Cannot sync queried data: syncMode[" +
                                detail.getSyncMode() + "]" );
                }

            }

            try {
                ContentProviderResult[] insertResults;
                insertResults = cr.applyBatch(App_Constants.AUTHORITY, insertOps );

                if ( !validateResult( insertOps, insertResults ) ) {
                    Log.e( TAG, "Insert ops results not matching." );
                }
            } catch (OperationApplicationException e) {
                Log.e( TAG, "cannot apply insert batch",  e );
            } catch (RemoteException e) {
                Log.e( TAG, "cannot apply insert batch",  e );
            }

            try {
                ContentProviderResult[] updateResults;
                updateResults = cr.applyBatch(App_Constants.AUTHORITY, updateOps );

                if ( !validateResult( updateOps, updateResults ) ) {
                    Log.e( TAG, "Update ops results not matching." );
                }
            } catch (OperationApplicationException e) {
                Log.e( TAG, "cannot apply update batch",  e );
            } catch (RemoteException e) {
                Log.e( TAG, "cannot apply update batch",  e );
            }

            try {
                ContentProviderResult[] deleteResults;
                deleteResults = cr.applyBatch(App_Constants.AUTHORITY, deleteOps );

                if ( !validateResult( deleteOps, deleteResults ) ) {
                    Log.e( TAG, "Delete ops results not matching." );
                }
            } catch (OperationApplicationException e) {
                Log.e( TAG, "cannot apply delete batch",  e );
            } catch (RemoteException e) {
                Log.e( TAG, "cannot apply delete batch",  e );
            }

        }

        QueryTransactionInfo.getInstance().markCompleted( result );
    }

    /**
     * If number of operations does not equal number of results
     * the there has been failure with at least one of the
     * operstions.
     *
     * @param ops The list of attempted operations.
     * @param results The list of results.
     * @return True if operation count equals result count, false otherwise.
     */
    private boolean validateResult(
            ArrayList<ContentProviderOperation> ops,
            ContentProviderResult[] results )
    {
        if ( ops == null && results == null ) {
            return true;
        } else if ( ops == null ) {
            return false;
        } else if ( results == null ) {
            return false;
        } else if ( ops.size() == results.length ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check try_count from database against MAX_REQUEST_ATTEMPTS.
     * If try_count < MAX_REQUEST_ATTEMPTS then increment tryCount.
     * If try_count >= MAX_REQUEST_ATTEMPTS then mark transaction
     * as completed and show notification.
     *
     * @param requestId The _ID column from the table to update.
     * @param result The HttpStatus code.
     * @param allowRetry If true and less then MAX_REQUEST_ATTEMPTS
     * increment retry counter and try again later.  If false then
     * mark transaction as completed.
     */
    public boolean requestFailure( long requestId, int result, boolean allowRetry )
    {
        final Context context = TickTeeAndroid.getAppContext();
        final ContentResolver cr = context.getContentResolver();
        ContentValues values;
        MethodEnum methodEnum;
        int failedUpdateCount;
        boolean tryAgain;

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.e( TAG, "processing request failure: httpResult[" + result + "]" );
        }

        Uri uri;
        uri = ContentUris.withAppendedId(
                TickteeProvider.CONTENT_URI_PROJECTS_COMPLETED,
                requestId );

        Uri failedUri;
        Cursor cursor;

        failedUri = ContentUris.withAppendedId(
                TickteeProvider.CONTENT_URI,
                requestId );

        cursor = cr.query( failedUri, columns, null, null, null );

        final int colTryCount = cursor.getColumnIndex(TableConstants.COL_TRY_COUNT);
        final int colStatus = cursor.getColumnIndex(TableConstants.COL_STATUS);
        int tryCount;
        String status;

        if ( cursor.moveToFirst() ) {
            tryCount = cursor.getInt( colTryCount );
            status = cursor.getString( colStatus );
            methodEnum = MethodEnum.valueOf( status );
        } else {
            if ( Log.isLoggable( TAG, Log.INFO ) ) {
                Log.i( TAG, "row has been removed by another thread" );
            }
            return false;
        }

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "processing request failure: tryCount[" + tryCount + "]" );
        }

        values = new ContentValues();
        values.put( TableConstants.COL_RESULT , result );

        if ( allowRetry && tryCount < App_Constants.MAX_REQUEST_ATTEMPTS ) {

            tryCount++;

            if ( Log.isLoggable( TAG, Log.INFO ) ) {
                Log.i( TAG,
                        "processing request failure: set retry tryCount[" +
                                tryCount + "]" );
            }

            values.put( TableConstants.COL_TRANSACTING,
                    App_Constants.TRANSACTION_RETRY );
            values.put( TableConstants.COL_TRY_COUNT,
                    tryCount );
            failedUpdateCount = cr.update( uri, values, null, null );

            tryAgain = true;
        } else {

            if ( Log.isLoggable( TAG, Log.INFO ) ) {
                Log.i( TAG,
                        "processing request failure: set max tryCount[" +
                                tryCount + "]" );
            }

            values.put( TableConstants.COL_TRANSACTING,
                    App_Constants.TRANSACTION_COMPLETED );
            values.put( TableConstants.COL_TRY_COUNT,
                    0 );

            failedUpdateCount = cr.update( uri, values, null, null );

//            NotificationUtil.errorNotify( methodEnum );

            tryAgain = false;
        }

        return tryAgain;
    }

    /**
     * Checks QueryTransactionInfo to determine if query request
     * should be retried.  If request will not be retried
     * show notification.
     *
     * @param result The HttpStatus code.
     * @param allowRetry If true and less then MAX_REQUEST_ATTEMPTS
     * increment retry counter and try again later.  If false then
     * mark transaction as completed.
     */
    public boolean retrieveFailure( int result, boolean allowRetry )
    {
        boolean tryAgain;

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "processing retrieve failure: httpResult[" + result + "]" );
        }

        if ( allowRetry ) {
            tryAgain = QueryTransactionInfo.getInstance().markRetry( result );
            if ( !tryAgain ) {
                QueryTransactionInfo.getInstance().markCompleted( result );
            }
        } else {
            QueryTransactionInfo.getInstance().markCompleted( result );
//            NotificationUtil.errorNotify( MethodEnum.GET );
            tryAgain = false;
        }

        return tryAgain;
    }

}