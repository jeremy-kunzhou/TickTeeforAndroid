package com.huhukun.tickteeforandroid.providers;

import android.os.Bundle;

import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.Exception.AuthenticationFailureException;
import com.huhukun.tickteeforandroid.Exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.Exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.Exception.WebServiceFailedException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public abstract class RESTCommand {

    private static final String TAG = App_Constants.APP_TAG +"RESTCommand";

    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    protected static HttpClient mHttpClient;

    protected static void createHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
            ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);
        }
    }


    /**
     * Get the authToken used to authenticate the request to the REST API
     *
     * @return The authToken.
     * @throws com.huhukun.tickteeforandroid.Exception.DeviceConnectionException Network connection is not available.
     * @throws com.huhukun.tickteeforandroid.Exception.AuthenticationFailureException Failed to authenticate the request.
     * Probably due to invalid credentials.
     */
    protected String getAuthToken()
            throws DeviceConnectionException, AuthenticationFailureException
    {
        Bundle authBundle;
        String authToken = null;
        // TODO: write real auth
//
//        try {
//            authBundle = AuthenticationHelper.getInstance().getAuthTokenInBackground(
//                    null, null, false );
//
//            authToken = authBundle.getString( AccountManager.KEY_AUTHTOKEN );
//        } catch ( IOException e ) {
//            String msg = "Authentication failed: Cannot connect to network.";
//            Log.i(TAG, msg, e);
//            throw new DeviceConnectionException(msg, e);
//        } catch ( AuthenticatorException e ) {
//            String msg = "Authentication failed: Invalid credentials.";
//            Log.i(TAG, msg, e);
//            throw new AuthenticationFailureException( msg );
//        }


        return authToken;
    }

    /**
     * Get the authToken and pass it to handleRequest().
     *
     * @return The HttpStatus code.
     * @throws DeviceConnectionException Network connection is not available.
     * @throws com.huhukun.tickteeforandroid.Exception.NetworkSystemException Error configuring the network connection.
     * @throws com.huhukun.tickteeforandroid.Exception.WebServiceFailedException Error configuring the http request or
     * an invalid json response has been returned.
     * @throws AuthenticationFailureException Failed to authenticate the request.
     */
    public int execute()
            throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException,
            AuthenticationFailureException
    {
        String authToken;

        authToken = getAuthToken();
        return handleRequest( authToken );
    }

    /**
     * Implemented by concrete command classes to handle the request and response
     * specific to a particular to REST method type.
     *
     * @param authToken Used to authenticate the request to the REST API.
     * @return The HttpStatus code.
     * @throws DeviceConnectionException Network connection is not available.
     * @throws NetworkSystemException Error configuring the network connection.
     * @throws WebServiceFailedException Error configuring the http request or
     * an invalid json response has been returned.
     */
    protected abstract int handleRequest( String authToken )
            throws DeviceConnectionException,
            NetworkSystemException,
            WebServiceFailedException;

    /**
     * This method should be overwritten by concrete classes to handle
     * REST method specific logic for a resource not found by the
     * REST API in the web service.
     */
    public void handleNotFound() {}

    /**
     * This method must be overwritten by concrete classes to handle
     * REST method specific logic for HttpStatus error codes returned by
     * the REST API.
     */
    public abstract boolean handleError( int httpResult, boolean allowRetry );

}