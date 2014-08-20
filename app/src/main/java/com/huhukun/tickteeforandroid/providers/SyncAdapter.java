package com.huhukun.tickteeforandroid.providers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.Exception.AuthenticationFailureException;
import com.huhukun.tickteeforandroid.Exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.Exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.Exception.WebServiceConnectionException;
import com.huhukun.tickteeforandroid.Exception.WebServiceFailedException;
import com.huhukun.tickteeforandroid.TickTeeAndroid;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.*;
/**
 * Created by kun on 20/08/2014.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";

    private final Context mContext;
    private final AccountManager mAccountManager;


    private static final String[] columns = {
            TableConstants._ID,
            TableConstants.COL_PROJECT_ID,
            TableConstants.COL_NAME,
            TableConstants.COL_DESCRIPTION,
            TableConstants.COL_STATUS };

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        ContentResolver cr = getContext().getContentResolver();

        if ( Log.isLoggable( TAG, Log.INFO )) {
            Log.e( TAG, "Performing sync operation." );
        }

        RESTCommand restCommand;
        MethodEnum methodEnum;
        Uri pendingUri;
        int count;

        Cursor cursor;
        cursor = cr.query(
                TickteeProvider.CONTENT_URI_PROJECTS_PENDING, columns, null, null, null );

        final int colId = cursor.getColumnIndex(TableConstants._ID);
        final int colProjectsId = cursor.getColumnIndex(TableConstants.COL_PROJECT_ID);
        final int colName = cursor.getColumnIndex(TableConstants.COL_NAME);
        final int colDescription = cursor.getColumnIndex(TableConstants.COL_DESCRIPTION);
        final int colStatus = cursor.getColumnIndex(TableConstants.COL_STATUS);

        long id;
        long projectsId;
        String name;
        String description;
        String status;

        while (cursor.moveToNext()) {

            id = cursor.getLong( colId );
            projectsId = cursor.getLong( colProjectsId );
            name = cursor.getString( colName );
            description = cursor.getString( colDescription );
            status = cursor.getString( colStatus );

            try {
                methodEnum = MethodEnum.valueOf( status );
            } catch ( IllegalArgumentException e ) {
                Log.e( TAG, "Cannot create MethodEnum: status[" + status + "]", e );
                syncResult.databaseError = true;
                clearTransacting(id);

                continue;
            }

            // If the row still exists and is still pending it will be
            // updated to transacting in-progress.
            pendingUri = ContentUris.withAppendedId(
                    TickteeProvider.CONTENT_URI_PROJECTS_IN_PROGRESS, id );
            count = cr.update( pendingUri, new ContentValues(), null, null );

            if ( count > 0 ) {
                switch ( methodEnum ) {
                    case POST:
                        restCommand = new InsertCommand( id, name, description );
                        break;
                    case PUT:
                        restCommand = new UpdateCommand( id, projectsId, name, description );
                        break;
                    case DELETE:
                        restCommand = new DeleteCommand( id, projectsId );
                        break;
                    default:
                        Log.e( TAG, "Invalid REST method: methodEnum[" + methodEnum + "]" );
                        syncResult.databaseError = true;
                        clearTransacting(id);
                        continue;
                }

                if ( !handleSync( restCommand, syncResult ) ) {
                    break;
                }
            }

        }

        cursor.close();

        QueryTransactionInfo queryTransInfo = QueryTransactionInfo.getInstance();
        if ( queryTransInfo.isRefreshOutstanding( true ) ) {
            queryTransInfo.markInProgress();
            restCommand = new RetrieveCommand();
            handleSync( restCommand, syncResult );
        }

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.e( TAG, "leaving onPerformSync" );
        }
    }


    /**
     * Sync the local database with the web service by executing the RESTCommand which
     * will call the REST API.  Hard errors will cancel the sync operation.
     * Soft errors will result in exponential back-off.
     *
     * @param restCommand The command object to be executed.
     * @param syncResult SyncAdapter-specific parameters. Here we use it to set
     * soft and hard errors.
     * @return True if the call to the REST API was successful.
     *         True if the call fails and request should be retried.
     *         False if the call fails and request should NOT be retried.
     */
    private boolean handleSync( RESTCommand restCommand, SyncResult syncResult )
    {
        try {
            RESTMethod.getInstance().handleRequest( restCommand );
        } catch ( WebServiceConnectionException e ) {
            if( Log.isLoggable(TAG, Log.INFO)) {
                Log.i( TAG, "Web service not available." );
            }

            if ( e.isRetry() ) {
                // soft error
                syncResult.stats.numIoExceptions = 1;
                return true;
            } else {
                // hard error
                syncResult.databaseError = true;
                return false;
            }

        } catch ( WebServiceFailedException e ) {
            if( Log.isLoggable( TAG, Log.INFO )) {
                Log.i( TAG, "Error returned from web service." );
            }
            return true;
        } catch ( DeviceConnectionException e ) {
            if( Log.isLoggable( TAG, Log.INFO )) {
                Log.i( TAG, "Device cannot connect to the network." );
            }
            if ( e.isRetry() ) {
                // soft error
                syncResult.stats.numIoExceptions = 1;
                return true;
            } else {
                // hard error
                syncResult.databaseError = true;
                return false;
            }
        } catch ( NetworkSystemException e ) {
            Log.e( TAG, "Error configuring http request.", e );
            // hard error
            syncResult.databaseError = true;
            return false;
        } catch ( AuthenticationFailureException e ) {
            if( Log.isLoggable( TAG, Log.INFO )) {
                Log.i( TAG, "Authentication failure.", e );
            }
            // hard error
            syncResult.stats.numAuthExceptions = 1;
            return false;
        }
        return true;
    }


    /**
     * Used to clear the transacting flags if the request fails before the
     * call to the REST API.  We will hopefully never get here, but reason
     * for this most likely bad data in the status column.
     *
     * @param requestId A value for the _ID column which will be used as the
     * update key for the request that failed.
     */
    private void clearTransacting( long requestId )
    {
        final ContentResolver cr =
                TickTeeAndroid.getAppContext().getContentResolver();
        ContentValues values;
        Uri uri;
        Cursor cursor;

        uri = ContentUris.withAppendedId(
                TickteeProvider.CONTENT_URI,
                requestId);

        values = new ContentValues();
        values.put( TableConstants.COL_RESULT , App_Constants.NON_HTTP_FALURE );
        values.put( TableConstants.COL_TRANSACTING,
                App_Constants.TRANSACTION_COMPLETED );
        values.put( TableConstants.COL_TRY_COUNT,
                0 );

        cr.update( uri, values, null, null );
    }
}
