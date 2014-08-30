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
import com.huhukun.tickteeforandroid.exception.AuthenticationFailureException;
import com.huhukun.tickteeforandroid.exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.exception.WebServiceConnectionException;
import com.huhukun.tickteeforandroid.exception.WebServiceFailedException;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;

import org.json.JSONException;

import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_ALERT_TYPE;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_CREATED_AT;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_CURRENT_PROGRESS;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_END_AT;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_EXPECTED_PROGRESS;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_INIT_PROGRESS;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_IS_CONSUMED;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_IS_DECIMAL;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_START_AT;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_TARGET;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_UNIT;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.TableConstants.COL_UPDATED_AT;

/**
 * Created by kun on 20/08/2014.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = App_Constants.APP_TAG + "SyncAdapter";

    private final Context mContext;
    private final AccountManager mAccountManager;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        ContentResolver cr = getContext().getContentResolver();

        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "Performing sync operation.");
        }

        RESTCommand restCommand;
        MethodEnum methodEnum;
        Uri pendingUri;
        int count;

        Cursor cursor;
        cursor = cr.query(
                TickteeProvider.CONTENT_URI_PROJECTS_PENDING, SqlOpenHelper.LOADER_COLUMNS, null, null, null);

        final int colId = cursor.getColumnIndex(TableConstants._ID);
        final int colProjectsId = cursor.getColumnIndex(TableConstants.COL_PROJECT_ID);
        final int colName = cursor.getColumnIndex(TableConstants.COL_NAME);
        final int colDescription = cursor.getColumnIndex(TableConstants.COL_DESCRIPTION);
        final int colStartAt = cursor.getColumnIndex(COL_START_AT);
        final int colEndAt = cursor.getColumnIndex(COL_END_AT);
        final int colExpectedProgress = cursor.getColumnIndex(COL_EXPECTED_PROGRESS);
        final int colCurrentProgress = cursor.getColumnIndex(COL_CURRENT_PROGRESS);
        final int colCreatedAt = cursor.getColumnIndex(COL_CREATED_AT);
        final int colUpdatedAt = cursor.getColumnIndex(COL_UPDATED_AT);
        final int colTarget = cursor.getColumnIndex(COL_TARGET);
        final int colUnit = cursor.getColumnIndex(COL_UNIT);
        final int colAlert = cursor.getColumnIndex(COL_ALERT_TYPE);
        final int colIsDecimal = cursor.getColumnIndex(COL_IS_DECIMAL);
        final int colInitProgress = cursor.getColumnIndex(COL_INIT_PROGRESS);
        final int colIsConsumed = cursor.getColumnIndex(COL_IS_CONSUMED);

        final int colStatus = cursor.getColumnIndex(TableConstants.COL_STATUS);

        long id;
        long projectsId;
        String name;
        String description;
        String startAt;
        String endAt;
        String expectedProgress;
        String currentProgress;
        String createdAt;
        String updatedAt;
        String target;
        String unit;
        String alert;
        String isDecimal;
        String initProgress;
        String isConsumed;
        String status;

        while (cursor.moveToNext()) {

            id = cursor.getLong(colId);
            projectsId = cursor.getLong(colProjectsId);
            name = cursor.getString(colName);
            Log.d(TAG, "Prepare for sync project id "+projectsId+" "+name);

            description = cursor.getString(colDescription);
            startAt = cursor.getString(colStartAt);
            endAt = cursor.getString(colEndAt);
            expectedProgress = cursor.getString(colExpectedProgress);
            currentProgress = cursor.getString(colCurrentProgress);
            createdAt = cursor.getString(colCreatedAt);
            updatedAt = cursor.getString(colUpdatedAt);
            target = cursor.getString(colTarget);
            unit = cursor.getString(colUnit);
            alert = cursor.getString(colAlert);
            isDecimal = cursor.getString(colIsDecimal);
            initProgress = cursor.getString(colInitProgress);
            isConsumed = cursor.getString(colIsConsumed);
            status = cursor.getString(colStatus);

            try {
                methodEnum = MethodEnum.valueOf(status);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Cannot create MethodEnum: status[" + status + "]", e);
                syncResult.databaseError = true;
                clearTransacting(id);

                continue;
            }

            // If the row still exists and is still pending it will be
            // updated to transacting in-progress.
            pendingUri = ContentUris.withAppendedId(
                    TickteeProvider.CONTENT_URI_PROJECTS_IN_PROGRESS, id);
            count = cr.update(pendingUri, new ContentValues(), null, null);

            if (count > 0) {
                try {
                    switch (methodEnum) {
                        case POST:
                            restCommand = new InsertCommand(id, name, description, startAt,
                                    endAt, expectedProgress, currentProgress, createdAt, updatedAt, target, unit, alert, isDecimal, initProgress, isConsumed);
                            break;
                        case PUT:

                                restCommand = new UpdateCommand(id, projectsId, name, description, startAt,
                                        endAt, expectedProgress, currentProgress, createdAt, updatedAt, target, unit, alert, isDecimal, initProgress, isConsumed);

                            break;
                        case DELETE:
                            restCommand = new DeleteCommand(id, projectsId);
                            break;
                        default:
                            Log.e(TAG, "Invalid REST method: methodEnum[" + methodEnum + "]");
                            syncResult.databaseError = true;
                            clearTransacting(id);
                            continue;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON_EXCEPTION, Invalid REST method: methodEnum[" + methodEnum + "]");
                    syncResult.databaseError = true;
                    clearTransacting(id);
                    continue;
                }

                if (!handleSync(restCommand, syncResult)) {
                    break;
                }
            }

        }

        cursor.close();

        QueryTransactionInfo queryTransInfo = QueryTransactionInfo.getInstance();
        if (queryTransInfo.isRefreshOutstanding(true)) {
            queryTransInfo.markInProgress();
            restCommand = new RetrieveCommand();
            handleSync(restCommand, syncResult);
        }

        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "leaving onPerformSync");
        }
    }


    /**
     * Sync the local database with the web service by executing the RESTCommand which
     * will call the REST API.  Hard errors will cancel the sync operation.
     * Soft errors will result in exponential back-off.
     *
     * @param restCommand The command object to be executed.
     * @param syncResult  SyncAdapter-specific parameters. Here we use it to set
     *                    soft and hard errors.
     * @return True if the call to the REST API was successful.
     * True if the call fails and request should be retried.
     * False if the call fails and request should NOT be retried.
     */
    private boolean handleSync(RESTCommand restCommand, SyncResult syncResult) {
        try {
            RESTMethod.getInstance().handleRequest(restCommand);
        } catch (WebServiceConnectionException e) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "Web service not available.");
            }

            if (e.isRetry()) {
                // soft error
                syncResult.stats.numIoExceptions = 1;
                return true;
            } else {
                // hard error
                syncResult.databaseError = true;
                return false;
            }

        } catch (WebServiceFailedException e) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "Error returned from web service.");
            }
            return true;
        } catch (DeviceConnectionException e) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "Device cannot connect to the network.");
            }
            if (e.isRetry()) {
                // soft error
                syncResult.stats.numIoExceptions = 1;
                return true;
            } else {
                // hard error
                syncResult.databaseError = true;
                return false;
            }
        } catch (NetworkSystemException e) {
            Log.e(TAG, "Error configuring http request.", e);
            // hard error
            syncResult.databaseError = true;
            return false;
        } catch (AuthenticationFailureException e) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "Authentication failure.", e);
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
     *                  update key for the request that failed.
     */
    private void clearTransacting(long requestId) {
        final ContentResolver cr =
                TickTeeAndroid.getAppContext().getContentResolver();
        ContentValues values;
        Uri uri;
        Cursor cursor;

        uri = ContentUris.withAppendedId(
                TickteeProvider.CONTENT_URI,
                requestId);

        values = new ContentValues();
        values.put(TableConstants.COL_RESULT, App_Constants.NON_HTTP_FAILURE);
        values.put(TableConstants.COL_TRANSACTING,
                App_Constants.TRANSACTION_COMPLETED);
        values.put(TableConstants.COL_TRY_COUNT,
                0);

        cr.update(uri, values, null, null);
    }
}
