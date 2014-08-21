package com.huhukun.tickteeforandroid.providers;

import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.Exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.Exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.Exception.WebServiceFailedException;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.utils.MyDateUtils;
import com.huhukun.tickteeforandroid.network.WebApiConstants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class InsertCommand extends RESTCommand {

    private static final String TAG = App_Constants.APP_TAG +"InsertCommand";

    private long requestId;
    private String name;
    private String description;
    private String startAt;
    private String endAt;
    private String expectedProgress;
    private String currentProgress;
    private String createdAt;
    private String updatedAt;

    public InsertCommand( long requestId, String name, String description, String startAt,
                          String endAt, String expectedProgress, String currentProgress,
                          String createdAt, String updatedAt )
    {
        this.requestId = requestId;
        this.name = name;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.expectedProgress = expectedProgress;
        this.currentProgress = currentProgress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    protected int handleRequest( String authToken )
            throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException
    {

        HttpResponse resp;
        JSONObject jsonObject;
        int statusCode;
        String respText;
        Project detail;

        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        final List<NameValuePair> httpHeaders = new ArrayList<NameValuePair>();




        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_NAME, name));
        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_DESCRIPTION, description));
        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_START_AT, startAt));
        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_END_AT, endAt));
        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_EXPECTED_PROGRESS, expectedProgress));
        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_CURRENT_PROGRESS, currentProgress));
        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_CREATED_AT, createdAt));
        httpParams.add(new BasicNameValuePair(WebApiConstants.PARAM_UPDATED_AT, updatedAt));

        String email = TickTeeAndroid.appSetting.getString(App_Constants.PREF_EMAIL, null);
        String token = TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null);

        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_EMAIL_PARAM, email));
        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, token));

        try {
            final HttpPost put;

            put = NetworkUtils.BUILDER(WebApiConstants.PROJECTS_URL)
                    .setHeader(httpHeaders).setParams(httpParams).toPost();


            createHttpClient();


            resp = mHttpClient.execute(put);
        } catch (IOException e) {
            String msg = "PUT method failed: Cannot connect to network.";
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
                String msg = "POST method failed: Invalid response.";
                Log.e(TAG, msg, e);
                throw new WebServiceFailedException(msg, e);
            }

            try {
                jsonObject = new JSONObject(respText);

                detail = new Project(jsonObject);
                detail.setRequestId(requestId);
                detail.setHttpResult(statusCode);
                detail.setTransDate(
                        MyDateUtils.stringToDateForWS(
                                jsonObject.getString(WebApiConstants.PARAM_DATE_UPDATED)));
                Processor.getInstance().update(detail);
            } catch (JSONException e) {
                String msg =
                        "PUT method failed: Cannot parse data returned from web service.";
                Log.e(TAG, msg);
                throw new WebServiceFailedException(msg);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return statusCode;
    }

    /**
     * Delegate error handling to the Processor.
     */
    public boolean handleError( int httpResult, boolean allowRetry )
    {
        return Processor.getInstance()
                .requestFailure( this.requestId, httpResult, allowRetry );
    }

}