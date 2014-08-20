package com.huhukun.tickteeforandroid;
import java.util.concurrent.Callable;

import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.providers.TickteeProvider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.util.Log;

public class DeleteTask  implements Callable<Boolean>{

    private static final String TAG = "DeleteTask";

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
                mDeleteId );

        deleteCount = cr.delete( uri, null, null );

        if ( deleteCount == 0 ) {
            Log.e( TAG, "Error setting delete request to PENDING status." );


            return false;
        }

        return true;
    }

}