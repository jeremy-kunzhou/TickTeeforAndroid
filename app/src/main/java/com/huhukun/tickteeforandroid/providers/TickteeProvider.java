package com.huhukun.tickteeforandroid.providers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.utils.MyDateUtils;

import static com.huhukun.tickteeforandroid.App_Constants.*;
import static com.huhukun.tickteeforandroid.model.SqlOpenHelper.*;

/**
 * Created by kun on 20/08/2014.
 */
public class TickteeProvider extends ContentProvider {

    private static final String TAG = "RestfulProvider";

    /** The MIME type of a directory of songs */
    private static final String CONTENT_TYPE
            = "vnd.android.cursor.dir/vnd.ticktee.projects";

    /** The MIME type of a single song */
    private static final String CONTENT_ITEM_TYPE
            = "vnd.android.cursor.item/vnd.ticktee.projects";

    private UriMatcher uriMatcher;
    private SqlOpenHelper dbHelper;

    private static final int PROJECTS = 1;
    private static final int PROJECTS_ID = 2;
    private static final int PROJECTS_PENDING = 3;
    private static final int PROJECTS_PENDING_ID = 4;
    private static final int PROJECTS_IN_PROGRESS_ID = 5;
    private static final int PROJECTS_COMPLETED_ID = 6;
    private static final int PROJECTS_QUERY_COMPLETED_ID = 7;
    private static final int PROJECTS_FILTERED = 8;

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/projects");
    public static final Uri CONTENT_URI_PROJECTS_PENDING = Uri.parse("content://"
            + AUTHORITY + "/projects/pending");
    public static final Uri CONTENT_URI_PROJECTS_IN_PROGRESS = Uri.parse("content://"
            + AUTHORITY + "/projects/in-progress");
    public static final Uri CONTENT_URI_PROJECTS_COMPLETED = Uri.parse("content://"
            + AUTHORITY + "/projects/completed");
    public static final Uri CONTENT_URI_PROJECTS_QUERY_COMPLETED = Uri.parse("content://"
            + AUTHORITY + "/projects/query-completed");
    public static final Uri CONTENT_URI_FILTERED = Uri.parse("content://"
            + AUTHORITY + "/projects/filtered");

    @Override
    public boolean onCreate() {

        this.dbHelper = new SqlOpenHelper(getContext());

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,
                "projects", PROJECTS);
        uriMatcher.addURI(AUTHORITY,
                "projects/#", PROJECTS_ID);
        uriMatcher.addURI(AUTHORITY,
                "projects/pending", PROJECTS_PENDING);
        uriMatcher.addURI(AUTHORITY,
                "projects/pending/#", PROJECTS_PENDING_ID);
        uriMatcher.addURI(AUTHORITY,
                "projects/in-progress/#", PROJECTS_IN_PROGRESS_ID);
        uriMatcher.addURI(AUTHORITY,
                "projects/completed/#", PROJECTS_COMPLETED_ID);
        uriMatcher.addURI(AUTHORITY,
                "projects/query-completed/#", PROJECTS_QUERY_COMPLETED_ID);
        uriMatcher.addURI(AUTHORITY,
                "projects/filtered/*", PROJECTS_FILTERED);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SqlOpenHelper.TableConstants.TABLE_NAME);
        QueryTransactionInfo queryInfo;
        String title;

        if ( Log.isLoggable(TAG, Log.INFO) ) {
            Log.i( TAG, "query uri[" + uri + "]" );
        }

        switch (uriMatcher.match(uri)) {
            case PROJECTS:

                // Triggered by RestfulActivity to start a
                // GET request for all the songs in the
                // database.

                qb.appendWhere( SqlOpenHelper.TableConstants.COL_STATUS );
                qb.appendWhere( "!=" );
                qb.appendWhere( "'" );
                qb.appendWhere( MethodEnum.DELETE.toString() );
                qb.appendWhere( "'" );

                queryInfo = QueryTransactionInfo.getInstance();

                // If a GET request is pending, notify the SyncAdapter.
                if ( queryInfo.isRefreshOutstanding( false ) ) {
                    ContentResolver.requestSync(
                            getAccount(getContext()),
                            AUTHORITY,
                            new Bundle());
                }

                break;
            case PROJECTS_ID:

                // Retrieve a specific song by ID.

                qb.appendWhere( SqlOpenHelper.TableConstants._ID );
                qb.appendWhere( "=" );
                qb.appendWhereEscapeString( uri.getPathSegments().get(1) );

                break;

            case PROJECTS_PENDING:

                // Triggered by SyncAdapter to get all
                // pending requests from the database

                qb.appendWhere( SqlOpenHelper.TableConstants.COL_TRANSACTING );
                qb.appendWhere( " IN (" );
                qb.appendWhere( String.valueOf( TRANSACTION_PENDING ));
                qb.appendWhere( ", " );
                qb.appendWhere( String.valueOf( TRANSACTION_RETRY ));
                qb.appendWhere( " )" );

                sortOrder = SqlOpenHelper.TableConstants.COL_TRANS_DATE + " ASC";

                break;
            case PROJECTS_FILTERED:

                // Triggered by RestfulActivity to start a
                // GET request for songs filtered by
                // song title.

                title = uri.getPathSegments().get(2);

                qb.appendWhere( SqlOpenHelper.TableConstants.COL_STATUS );
                qb.appendWhere( "!=" );
                qb.appendWhere( "'" );
                qb.appendWhere( MethodEnum.DELETE.toString() );
                qb.appendWhere( "'" );

                if ( !TextUtils.isEmpty(title) ) {
                    qb.appendWhere( " AND " );
                    qb.appendWhere( SqlOpenHelper.TableConstants.COL_NAME );
                    qb.appendWhere( " LIKE " );
                    qb.appendWhereEscapeString( title.trim() + "%" );
                }

                queryInfo = QueryTransactionInfo.getInstance();

                // If a GET request is pending, notify the SyncAdapter.
                if ( queryInfo.isRefreshOutstanding( false ) ) {
                    ContentResolver.requestSync(
                            getAccount( getContext() ),
                            AUTHORITY,
                            new Bundle() );
                }

                break;
            default:
                Log.e( TAG, "Unknown uri[" + uri + "]" );
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default.
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = SqlOpenHelper.TableConstants.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = qb.query(
                db, projection, selection, selectionArgs, null, null, orderBy);

        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case PROJECTS:
            case PROJECTS_PENDING:
            case PROJECTS_FILTERED:
                return CONTENT_TYPE;
            case PROJECTS_ID:
            case PROJECTS_PENDING_ID:
            case PROJECTS_IN_PROGRESS_ID:
            case PROJECTS_COMPLETED_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long requestId;
        Uri newUri;
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "insert uri[" + uri + "]" );
        }

        switch (uriMatcher.match(uri)) {
            case PROJECTS:

                // Triggered by the Processor to insert new songs that have been
                // retrieved from the REST API during a GET request.

                try {
                    requestId = db.insertOrThrow(
                            SqlOpenHelper.TableConstants.TABLE_NAME, null, values);
                    newUri = ContentUris.withAppendedId(
                            TickteeProvider.CONTENT_URI, requestId);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                } catch ( SQLException e ) {
                    Log.e( TAG, "Cannot insert song." );
                    return null;
                }
            case PROJECTS_PENDING:

                // Triggered by RestfulActivity to start a POST request.

                values.put( SqlOpenHelper.TableConstants.COL_STATUS,
                        MethodEnum.POST.toString() );
                values.put( SqlOpenHelper.TableConstants.COL_TRANSACTING,
                        App_Constants.TRANSACTION_PENDING );
                values.put( SqlOpenHelper.TableConstants.COL_RESULT,
                        0 );
                values.put( SqlOpenHelper.TableConstants.COL_TRANS_DATE,
                        MyDateUtils.currentDateMillis() );
                values.put( SqlOpenHelper.TableConstants.COL_TRY_COUNT,
                        0 );

                // Insert into database
                try {
                    requestId = db.insertOrThrow(SqlOpenHelper.TableConstants.TABLE_NAME, null, values);
                } catch ( SQLException e ) {
                    Log.e( TAG, "Cannot insert song in PENDING status." );
                    return null;
                }

                ContentResolver.requestSync(
                        getAccount( getContext() ), App_Constants.AUTHORITY, new Bundle() );

                // Notify any watchers of the change
                newUri = ContentUris.withAppendedId(TickteeProvider.CONTENT_URI, requestId);
                getContext().getContentResolver().notifyChange(newUri, null);

                return newUri;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        StringBuilder where;
        ContentValues values;
        StringBuilder keyWithSelection;
        String segment;
        Uri newUri;
        int count;

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "delete uri[" + uri + "]" );
        }

        switch (uriMatcher.match(uri)) {
            case PROJECTS_ID:

                // Delete a song by songs_id

                segment = uri.getPathSegments().get(2);

                where = new StringBuilder();
                where.append( TableConstants._ID );
                where.append( "=" );
                where.append( segment );

                count = db.delete(TableConstants.TABLE_NAME, where.toString(), null );

                return count;
            case PROJECTS_PENDING_ID:

                // Triggered by RestfulActivity
                // set status to DELETE.
                // Logical delete.

                segment = uri.getPathSegments().get(2);

                values = new ContentValues();

                values.put( TableConstants.COL_STATUS,
                        MethodEnum.DELETE.toString() );
                values.put( TableConstants.COL_TRANSACTING,
                        App_Constants.TRANSACTION_PENDING );
                values.put( TableConstants.COL_RESULT,
                        0 );
                values.put( TableConstants.COL_TRANS_DATE,
                        MyDateUtils.currentDateMillis() );
                values.put( TableConstants.COL_TRY_COUNT,
                        0 );

                keyWithSelection = new StringBuilder();
                keyWithSelection.append( TableConstants._ID );
                keyWithSelection.append( "=" );
                keyWithSelection.append( segment );
                keyWithSelection.append( " AND " );
                keyWithSelection.append( TableConstants.COL_TRANSACTING );
                keyWithSelection.append( " != " );
                keyWithSelection.append( App_Constants.TRANSACTION_PENDING );

                if ( (!TextUtils.isEmpty(selection) )) {
                    keyWithSelection.append( " AND (" );
                    keyWithSelection.append( selection );
                    keyWithSelection.append( ")" );
                }

                // Virgil Dobjanschi: "Delete breaks the contract."
                count = db.update(TableConstants.TABLE_NAME, values,
                        keyWithSelection.toString(), selectionArgs);

                ContentResolver.requestSync(
                        getAccount( getContext() ), App_Constants.AUTHORITY, new Bundle() );

                newUri = ContentUris.withAppendedId(
                        TickteeProvider.CONTENT_URI, Long.valueOf(segment));
                getContext().getContentResolver().notifyChange(newUri, null);

                return count;
            case PROJECTS_COMPLETED_ID:

                // Triggered by the Processor
                // to physically delete the row
                // for a DELETE operation initiated
                // from the local device
                // Select by _ID column.

                segment = uri.getPathSegments().get(2);

                where = new StringBuilder();
                where.append( TableConstants._ID );
                where.append( "=" );
                where.append( segment );

                count = db.delete(TableConstants.TABLE_NAME, where.toString(), null );

                newUri = ContentUris.withAppendedId(
                        TickteeProvider.CONTENT_URI_PROJECTS_COMPLETED, Long.valueOf(segment));
                getContext().getContentResolver().notifyChange(newUri, null);

                return count;
            case PROJECTS_QUERY_COMPLETED_ID:

                // Triggered by the Processor
                // to physically delete the row
                // for a GET operation initiated
                // from the local device where
                // the row has been deleted
                // by the REST API.
                // Select by songs_id column.

                segment = uri.getPathSegments().get(2);

                where = new StringBuilder();
                where.append( TableConstants.COL_PROJECT_ID );
                where.append( "=" );
                where.append( segment );

                count = db.delete(TableConstants.TABLE_NAME, where.toString(), null );

                newUri = ContentUris.withAppendedId(
                        TickteeProvider.CONTENT_URI_PROJECTS_QUERY_COMPLETED,
                        Long.valueOf(segment));
                getContext().getContentResolver().notifyChange(newUri, null);

                return count;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String segment;
        StringBuilder keyWithSelection;
        Uri newUri;
        int count;

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "update uri[" + uri + "]" );
        }

        switch (uriMatcher.match(uri)) {
            case PROJECTS_PENDING_ID:

                // Triggered by RestfulActivity
                // to update title and artist
                // and start a PUT request.

                segment = uri.getPathSegments().get(2);

                values.put( SqlOpenHelper.TableConstants.COL_STATUS,
                        MethodEnum.PUT.toString() );
                values.put( SqlOpenHelper.TableConstants.COL_TRANSACTING,
                        App_Constants.TRANSACTION_PENDING );
                values.put( SqlOpenHelper.TableConstants.COL_RESULT,
                        0 );
                values.put( SqlOpenHelper.TableConstants.COL_TRANS_DATE,
                        MyDateUtils.currentDateMillis() );
                values.put( SqlOpenHelper.TableConstants.COL_TRY_COUNT,
                        0 );

                keyWithSelection = new StringBuilder();
                keyWithSelection.append( SqlOpenHelper.TableConstants._ID );
                keyWithSelection.append( "=" );
                keyWithSelection.append( segment );
                keyWithSelection.append( " AND " );
                keyWithSelection.append( SqlOpenHelper.TableConstants.COL_TRANSACTING );
                keyWithSelection.append( " != " );
                keyWithSelection.append( App_Constants.TRANSACTION_PENDING );

                if ( (!TextUtils.isEmpty(selection) )) {
                    keyWithSelection.append( " AND (" );
                    keyWithSelection.append( selection );
                    keyWithSelection.append( ")" );
                }

                count = db.update(SqlOpenHelper.TableConstants.TABLE_NAME, values,
                        keyWithSelection.toString(), selectionArgs);

                ContentResolver.requestSync(
                        getAccount( getContext() ), App_Constants.AUTHORITY, new Bundle() );

                newUri = ContentUris.withAppendedId(
                        TickteeProvider.CONTENT_URI, Long.valueOf(segment));
                getContext().getContentResolver().notifyChange(newUri, null);

                return count;
            case PROJECTS_IN_PROGRESS_ID:

                // Triggered by SyncAdapter to set rows
                // to TRANSACTION_IN_PROGRESS status

                segment = uri.getPathSegments().get(2);

                values.put( SqlOpenHelper.TableConstants.COL_TRANSACTING,
                        App_Constants.TRANSACTION_IN_PROGRESS );

                keyWithSelection = new StringBuilder();
                keyWithSelection.append( SqlOpenHelper.TableConstants._ID );
                keyWithSelection.append( "=" );
                keyWithSelection.append( segment );

                if ( (!TextUtils.isEmpty(selection) )) {
                    keyWithSelection.append( " AND (" );
                    keyWithSelection.append( selection );
                    keyWithSelection.append( ")" );
                }

                count = db.update(SqlOpenHelper.TableConstants.TABLE_NAME, values,
                        keyWithSelection.toString(), selectionArgs);

                newUri = ContentUris.withAppendedId(
                        TickteeProvider.CONTENT_URI, Long.valueOf(segment));
                getContext().getContentResolver().notifyChange(newUri, null);

                return count;
            case PROJECTS_COMPLETED_ID:

                // Triggered by Processor for update
                // and insert operations to set rows
                // to TRANSACTION_COMPLETED status.
                // Select by _id column.

                segment = uri.getPathSegments().get(2);

                keyWithSelection = new StringBuilder();
                keyWithSelection.append( SqlOpenHelper.TableConstants._ID );
                keyWithSelection.append( "=" );
                keyWithSelection.append( segment );

                if ( (!TextUtils.isEmpty(selection) )) {
                    keyWithSelection.append( " AND (" );
                    keyWithSelection.append( selection );
                    keyWithSelection.append( ")" );
                }

                count = db.update(SqlOpenHelper.TableConstants.TABLE_NAME, values,
                        keyWithSelection.toString(), selectionArgs);

                newUri = ContentUris.withAppendedId(
                        TickteeProvider.CONTENT_URI_PROJECTS_COMPLETED, Long.valueOf(segment));
                getContext().getContentResolver().notifyChange(newUri, null);

                return count;

            case PROJECTS_QUERY_COMPLETED_ID:

                // Triggered by Processor for
                // query operations to set rows
                // to TRANSACTION_COMPLETED status.
                // Select by songs_id column.

                segment = uri.getPathSegments().get(2);

                keyWithSelection = new StringBuilder();
                keyWithSelection.append( SqlOpenHelper.TableConstants.COL_PROJECT_ID );
                keyWithSelection.append( "=" );
                keyWithSelection.append( segment );

                if ( (!TextUtils.isEmpty(selection) )) {
                    keyWithSelection.append( " AND (" );
                    keyWithSelection.append( selection );
                    keyWithSelection.append( ")" );
                }

                count = db.update(SqlOpenHelper.TableConstants.TABLE_NAME, values,
                        keyWithSelection.toString(), selectionArgs);

                newUri = ContentUris.withAppendedId(
                        TickteeProvider.CONTENT_URI_PROJECTS_QUERY_COMPLETED,
                        Long.valueOf(segment));
                getContext().getContentResolver().notifyChange(newUri, null);

                return count;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private Account getAccount( Context context )
    {
        // In a real world app where there are multiple
        // Accounts, we would allow user to choose one.

        AccountManager am = AccountManager.get( context );
        Account[] accounts;
        Account account;

        accounts = am.getAccountsByType( App_Constants.ACCOUNT_TYPE );

        if ( accounts.length > 0 ) {
            account = accounts[0];
        } else {
            throw new IllegalStateException(
                    "Cannot get Account for " + App_Constants.ACCOUNT_TYPE );
        }

        return account;
    }
}
