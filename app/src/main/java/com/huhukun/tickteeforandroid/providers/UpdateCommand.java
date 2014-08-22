package com.huhukun.tickteeforandroid.providers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.exception.WebServiceFailedException;
import com.huhukun.tickteeforandroid.R;
import com.huhukun.tickteeforandroid.TickTeeAndroid;
import com.huhukun.tickteeforandroid.model.Project;
import com.huhukun.utils.FormatHelper;
import com.huhukun.utils.MyDateUtils;
import com.huhukun.utils.NumberUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.huhukun.tickteeforandroid.providers.WebApiConstants.LOGIN_URL;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_CREATED_AT;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_CURRENT_PROGRESS;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_DESCRIPTION;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_END_AT;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_EXPECTED_PROGRESS;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_NAME;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_PROJECT;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_PROJECTS_ID;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_START_AT;
import static com.huhukun.tickteeforandroid.providers.WebApiConstants.PARAM_UPDATED_AT;


public class UpdateCommand extends RESTCommand {

    private static final String TAG = App_Constants.APP_TAG +"UpdateCommand";

    private long requestId;
    private long projectId;
    private JSONObject projectJson;

    public UpdateCommand(long requestId, long projectsId, String name, String description, String startAt,
                         String endAt, String expectedProgress, String currentProgress,
                         String createdAt, String updatedAt ) throws JSONException {
        this.requestId = requestId;
        this.projectId = projectsId;
        JSONObject json = new JSONObject();
        json.put(PARAM_NAME, name);
        json.put(PARAM_DESCRIPTION, description);
        json.put(PARAM_START_AT, startAt);
        json.put(PARAM_END_AT, endAt);
        json.put(PARAM_EXPECTED_PROGRESS, expectedProgress);
        json.put(PARAM_CURRENT_PROGRESS,currentProgress);
        json.put(PARAM_CREATED_AT, createdAt);
        json.put(PARAM_UPDATED_AT, updatedAt);
        projectJson = new JSONObject();
        projectJson.put(PARAM_PROJECT, json);
        Log.d(TAG, "prepare for update command project "+ projectsId);
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

        final List<NameValuePair> httpHeaders = new ArrayList<NameValuePair>();




        String email = TickTeeAndroid.appSetting.getString(App_Constants.PREF_EMAIL, null);
        String token = TickTeeAndroid.appSetting.getString(App_Constants.PREF_TOKEN, null);

        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_EMAIL_PARAM, email));
        httpHeaders.add(new BasicNameValuePair(WebApiConstants.HEADER_ACCESS_TOKEN_PARM, token));

        try {
            final HttpPut put;

            put = NetworkUtils.BUILDER(WebApiConstants.PROJECT_URL, projectId)
                    .setHeader(httpHeaders).toPut(projectJson.toString());

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

            Log.d(TAG, respText);
            try {
                jsonObject = new JSONObject(respText);

                detail = new Project(jsonObject);

                detail.setRequestId(requestId);
                detail.setHttpResult(statusCode);
//                detail.setTransDate(
//                        MyDateUtils.stringToDateForWS(
//                                jsonObject.getString(WebApiConstants.PARAM_DATE_UPDATED)));
                detail.setTransDate(new Date());
                Processor.getInstance().update(detail);

            } catch (JSONException e) {
                String msg =
                        "PUT method failed: Cannot parse data returned from web service."+ respText;
                Log.e(TAG, msg);
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