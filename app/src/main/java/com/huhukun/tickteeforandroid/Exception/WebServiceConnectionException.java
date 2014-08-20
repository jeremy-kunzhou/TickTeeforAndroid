package com.huhukun.tickteeforandroid.Exception;

/**
 * Created by kun on 19/08/2014.
 */
public class WebServiceConnectionException extends Exception {
    private boolean retry;

    public WebServiceConnectionException(String conMsg, boolean retry) {

    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }
}
