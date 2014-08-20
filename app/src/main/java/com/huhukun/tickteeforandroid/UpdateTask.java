package com.huhukun.tickteeforandroid;
import java.util.concurrent.Callable;

import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

public class UpdateTask implements Callable<Boolean>{

    private static final String TAG = "UpdateTask";

    private long mUpdateId;
    private String mTitle;
    private String mArtist;

    public UpdateTask( long updateId, String title, String artist )
    {
        mUpdateId = updateId;
        mTitle = title;
        mArtist = artist;
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

        values.put( SqlOpenHelper.TableConstants.COL_NAME, mTitle );
        values.put( SqlOpenHelper.TableConstants.COL_DESCRIPTION, mArtist );

        updateCount = cr.update( uri, values, null, null );

        if ( updateCount == 0 ) {
            Log.e( TAG, "Error setting update request to PENDING status." );


            return false;
        }

        return true;
    }

}