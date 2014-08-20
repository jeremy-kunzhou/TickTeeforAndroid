package com.huhukun.tickteeforandroid.providers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.Exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.Exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.Exception.WebServiceFailedException;
import com.huhukun.tickteeforandroid.R;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.utils.MyDateUtils;
import com.huhukun.utils.NumberUtils;
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


public class UpdateCommand extends RESTCommand {

    private static final String TAG = "UpdateCommand";

    private long requestId;
    private long projectsId;
    private String name;
    private String description;

    public UpdateCommand(long requestId, long projectsId, String name, String description) {
        this.requestId = requestId;
        this.projectsId = projectsId;
        this.name = name;
        this.description = description;
    }

    @Override
    protected int handleRequest(String authToken)
            throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException {
        HttpResponse resp;
        JSONObject jsonObject;
        int statusCode;
        String respText;
        Project detail;

        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        final List<NameValuePair> httpHeaders = new ArrayList<NameValuePair>();



        httpParams.add(new BasicNameValuePair(
                WebApiConstants.PARAM_PROJECTS_ID, NumberUtils.longToString(projectsId)));
        httpParams.add(new BasicNameValuePair(
                WebApiConstants.PARAM_NAME, name));
        httpParams.add(new BasicNameValuePair(
                WebApiConstants.PARAM_DESCRIPTION, description));

        String email = TickTeeAndroid.appSetting.getString(App_Constants.PREF_EMAIL, null);
        String token = TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null);

        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_EMAIL_PARAM, email));
        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, token));

        try {
            final HttpPost put;

            put = NetworkUtils.BUILDER(WebApiConstants.PROJECT_URL, projectsId)
                    .setHeader(httpHeaders).setParams(httpParams).toPost();


            createHttpClient();


            resp = mHttpClient.execute(put);
        } catch (IOException e) {
            String msg = "PUT method failed: Cannot connect to network.";
            Log.i(TAG, msg, e);
            throw new DeviceConnectionException(msg, e);
        }

        statusCode = resp.getStatusLine().getStatusCode();

        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "HTTP statusCode[" + statusCode + "]");
        }

        if (statusCode == HttpStatus.SC_CREATED) {

            try {
                respText = EntityUtils.toString(resp.getEntity());
            } catch (IOException e) {
                String msg = "PUT method failed: Invalid response.";
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
     * If attempt to update resource results in not found http status code (404)
     * returned from the REST API, delete the same row from the local database.
     * Show notification on device and info message in activity.
     */
    @Override
    public void handleNotFound() {
        Processor.getInstance().delete(requestId);

        final Context ctx = TickTeeAndroid.getAppContext();

        final Resources res = ctx.getResources();
        final String titleFormat = res.getString(R.string.song_error_title);
        final String contentText = res.getString(R.string.song_remote_delete);
        String contentTitle;

        contentTitle = String.format(titleFormat, "update");

//        NotificationUtil.errorNotify( contentTitle, contentText );
    }

    /**
     * Delegate error handling to the Processor
     */
    @Override
    public boolean handleError(int httpResult, boolean allowRetry) {
        return Processor.getInstance()
                .requestFailure(this.requestId, httpResult, allowRetry);
    }

}