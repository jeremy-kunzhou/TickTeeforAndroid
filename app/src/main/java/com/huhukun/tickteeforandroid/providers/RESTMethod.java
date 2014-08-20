package com.huhukun.tickteeforandroid.providers;

import android.util.Log;


import com.huhukun.tickteeforandroid.App_Constants;
import com.huhukun.tickteeforandroid.Exception.AuthenticationFailureException;
import com.huhukun.tickteeforandroid.Exception.DeviceConnectionException;
import com.huhukun.tickteeforandroid.Exception.NetworkSystemException;
import com.huhukun.tickteeforandroid.Exception.WebServiceConnectionException;
import com.huhukun.tickteeforandroid.Exception.WebServiceFailedException;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;

public class RESTMethod {

    private static final String TAG = "RESTMethod";

    private static final RESTMethod instance = new RESTMethod();

    private static HttpClient mHttpClient;

    private RESTMethod() {}

    public static RESTMethod getInstance()
    {
        return instance;
    }

    public void handleRequest( RESTCommand restCommand )
            throws WebServiceConnectionException,
            WebServiceFailedException,
            DeviceConnectionException,
            NetworkSystemException,
            AuthenticationFailureException
    {
        int statusCode;
        boolean retry;

        try {
            statusCode = restCommand.execute();
        } catch ( AuthenticationFailureException e ) {
            String msg = "Authentication failed: Invalid credentials.";
            Log.i(TAG, msg, e);
            restCommand.handleError( App_Constants.NON_HTTP_FAILURE, false );
            throw e;
        } catch (NetworkSystemException e) {
            String msg = "Error configuring http request.";
            Log.e(TAG, msg, e);
            restCommand.handleError( App_Constants.NON_HTTP_FAILURE, false );
            throw e;
        } catch (DeviceConnectionException e) {
            String msg = "RESTCommand failed to execute: "
                    + "Cannot connect to network.";
            Log.i(TAG, msg, e);
            retry = restCommand.handleError( App_Constants.NON_HTTP_FAILURE, true );
            e.setRetry( retry );
            throw e;
        } catch ( WebServiceFailedException e ) {
            String msg = "RESTCommand failed to execute: "
                    + "Error returned from web service.";
            Log.e(TAG, msg, e);
            restCommand.handleError( App_Constants.NON_HTTP_FAILURE, false );
            throw e;
        } catch ( Exception e ) {
            String msg = "RESTCommand failed to execute: "
                    + "Unhandled exception with call to web service.";
            Log.e(TAG, msg, e);
            restCommand.handleError( App_Constants.NON_HTTP_FAILURE, false );
            throw new WebServiceFailedException( msg, e );
        }

        if ( Log.isLoggable( TAG, Log.INFO )) {
            Log.i( TAG, "REST http statusCode[" + statusCode + "]" );
        }

        switch (statusCode) {
            case HttpStatus.SC_OK:
                if ( Log.isLoggable( TAG, Log.INFO) ) {
                    Log.i(TAG, "Request has been processed with success.");
                }
                break;
            case HttpStatus.SC_NOT_FOUND:
                if ( Log.isLoggable( TAG, Log.INFO) ) {
                    Log.i(TAG, "Requested object not found.");
                }
                restCommand.handleNotFound();
                break;
            case HttpStatus.SC_GATEWAY_TIMEOUT:
            case HttpStatus.SC_REQUEST_TIMEOUT:
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
                String conMsg = "REST method failed: Server unavailable.";
                Log.i(TAG, conMsg);
                retry = restCommand.handleError( statusCode, true );
                throw new WebServiceConnectionException( conMsg, retry );
            case HttpStatus.SC_UNAUTHORIZED:
                String authMsg = "Invalid auth token.";
                Log.i(TAG, authMsg);
                restCommand.handleError( statusCode, false );
                throw new AuthenticationFailureException( authMsg );
            case HttpStatus.SC_BAD_REQUEST:
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                String errMsg = "Server error/bad request.";
                Log.e(TAG, errMsg);
                restCommand.handleError( statusCode, false );
                throw new WebServiceFailedException( errMsg );
            default:
                String unknownMsg = "REST method failed: Unknown HTTP status code: "
                        + "statusCode[" + statusCode + "]";
                Log.e( TAG, unknownMsg );
                restCommand.handleError( statusCode, false );
                throw new WebServiceFailedException( unknownMsg );
        }

    }

}