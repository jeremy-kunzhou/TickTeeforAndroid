package com.huhukun.tickteeforandroid.providers;

import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.exception.WebServiceFailedException;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.utils.MyDateUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RetrieveCommand extends RESTCommand {

    private static final String TAG = App_Constants.APP_TAG +"RetrieveCommand";

    @Override
    protected int handleRequest( String authToken )
            throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException
    {
        if ( Log.isLoggable( TAG, Log.INFO )) {
            Log.i( TAG, "executing RESTMethod.GET" );
        }

        JSONArray jsonArray;

        Project[] details = null;
        String nextDownloadDate;


        HttpResponse resp;
        JSONObject jsonObject;
        int statusCode;
        String respText;
        Project detail;

        final List<NameValuePair> httpHeaders = new ArrayList<NameValuePair>();
        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();



        String email = TickTeeAndroid.appSetting.getString(App_Constants.PREF_EMAIL, null);
        String token = TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null);

        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_EMAIL_PARAM, email));
        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, token));

        Long dlDate;
        String dlDateParam;

        // get the last download date from shared preferences
        dlDate = TickTeeAndroid.appSetting.getLong( App_Constants.PREFS_DOWNLOAD_DATE, 0 );
        if ( dlDate == null ) {
            dlDate = MyDateUtils.addToCurrent( Calendar.DAY_OF_MONTH, -30 );
        }

        dlDateParam = MyDateUtils.dateToStringForWS( dlDate );

        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_DOWNLOAD_DATE, dlDateParam));

        try {
            final HttpGet get;

            get = NetworkUtils.BUILDER(WebApiConstants.PROJECTS_URL)
                    .setHeader(httpHeaders).setParams(httpParams).toGet();


            createHttpClient();


            resp = mHttpClient.execute(get);
        } catch (IOException e) {
            String msg = "GET method failed: Cannot connect to network.";
            Log.i(TAG, msg, e);
            throw new DeviceConnectionException(msg, e);
        }


        statusCode = resp.getStatusLine().getStatusCode();

        if ( Log.isLoggable( TAG, Log.INFO ) ) {
            Log.i( TAG, "HTTP statusCode[" + statusCode + "]" );
        }

        if ( statusCode == HttpStatus.SC_OK ) {

            try {
                respText = EntityUtils.toString(resp.getEntity());
            } catch ( IOException e ) {
                String msg = "GET method failed: Invalid response.";
                Log.e(TAG, msg, e);
                throw new WebServiceFailedException(msg, e);
            }
            Log.i(TAG, respText);
            try {
//                jsonObject = new JSONObject( respText );
//
//                // get the date of this download from the REST API
//                nextDownloadDate = jsonObject.getString(
//                        WebApiConstants.PARAM_NEXT_DOWNLOAD_DATE );
//
//                if ( Log.isLoggable( TAG, Log.INFO ) ) {
//                    Log.i( TAG, "get: nextDownloadDate[" + nextDownloadDate + "]" );
//                }
                jsonArray = new JSONArray( respText );

                details = new Project[jsonArray.length()];
                for ( int i = 0; i < jsonArray.length(); i++ ) {
                    JSONObject jObj = jsonArray.getJSONObject( i );

                    detail = new Project(jsonArray.getJSONObject(i));


                    // REST API has a syncMode associated with each row:
                    // U: Update, I: Insert, D: Delete
//                    detail.setSyncMode( Project.SyncMode.valueOf(jObj.getString(
//                            WebApiConstants.PARAM_SYNC_MODE)) );
//                    detail.setTransDate(
//                            MyDateUtils.stringToDateForWS(jObj.getString(
//                                    WebApiConstants.PARAM_DATE_UPDATED)));
//                    detail.setSyncMode(Project.SyncMode.I);
                    details[i] = detail;
                }

            } catch ( JSONException e ) {
                String msg =
                        "GET method failed: Cannot parse data returned from web service.";
                Log.e( TAG, msg );
                throw new WebServiceFailedException( msg );
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Processor.getInstance().retrieve( details, MyDateUtils.addToCurrent(Calendar.MINUTE, 2),statusCode );
        }

        return statusCode;
    }

    /**
     * Delegate error handling to the Processor.
     */
    @Override
    public boolean handleError(int httpResult, boolean allowRetry)
    {
        return Processor.getInstance()
                .retrieveFailure( httpResult, allowRetry );
    }

}