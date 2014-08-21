package com.huhukun.tickteeforandroid;

import java.util.concurrent.Callable;

import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.SqlOpenHelper;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

public class InsertTask implements Callable<Boolean>{

    private static final String TAG = App_Constants.APP_TAG +"InsertTask";

    private String mTitle;
    private String mArtist;

    public InsertTask( String title, String artist )
    {
        mTitle = title;
        mArtist = artist;
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

        values.put( SqlOpenHelper.TableConstants.COL_NAME, mTitle );
        values.put( SqlOpenHelper.TableConstants.COL_DESCRIPTION, mArtist );

        uri = cr.insert( TickteeProvider.CONTENT_URI_PROJECTS_PENDING, values );
        if ( uri == null ) {
            Log.e( TAG, "Error setting insert request to PENDING status." );


            return false;
        }

        return true;
    }

}