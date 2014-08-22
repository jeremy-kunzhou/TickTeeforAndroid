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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeleteCommand extends RESTCommand {

    private static final String TAG = App_Constants.APP_TAG +"DeleteCommand";

    private long requestId;
    private long projectId;

    public DeleteCommand( long requestId, long projectId )
    {
        this.requestId = requestId;
        this.projectId = projectId;
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

        final List<NameValuePair> httpHeaders = new ArrayList<NameValuePair>();


        String email = TickTeeAndroid.appSetting.getString(App_Constants.PREF_EMAIL, null);
        String token = TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null);

        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_EMAIL_PARAM, email));
        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, token));

        try {
            final HttpDelete delete;

            delete = NetworkUtils.BUILDER(WebApiConstants.PROJECT_URL, projectId)
                    .setHeader(httpHeaders).toDelete();


            createHttpClient();


            resp = mHttpClient.execute(delete);
        } catch (IOException e) {
            String msg = "DELETE method failed: Cannot connect to network.";
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
                String msg = "DELETE method failed: Invalid response.";
                Log.e(TAG, msg, e);
                throw new WebServiceFailedException(msg, e);
            }


            try {
                jsonObject = new JSONObject(respText);

                detail = new Project(jsonObject);
                detail.setRequestId(requestId);
                detail.setHttpResult(statusCode);
//                detail.setTransDate(
//                        MyDateUtils.stringToDateForWS(
//                                jsonObject.getString(WebApiConstants.PARAM_DATE_UPDATED)));
                detail.setTransDate(new Date());
                Processor.getInstance().delete(detail.getRequestId());

            } catch ( JSONException e ) {
                String msg =
                        "DELETE method failed: Cannot parse data returned from web service.";
                Log.e( TAG, msg );
                throw new WebServiceFailedException( msg );
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return statusCode;
    }

    /**
     * If attempt to delete resource results in not found http status code (404)
     * returned from the REST API, delete the same row from the local database.
     */
    @Override
    public void handleNotFound()
    {
        Processor.getInstance().delete( requestId );
    }

    /**
     * Delegate error handling to the Processor
     */
    @Override
    public boolean handleError(int httpResult, boolean allowRetry)
    {
        return Processor.getInstance()
                .requestFailure( this.requestId, httpResult, allowRetry );
    }

}