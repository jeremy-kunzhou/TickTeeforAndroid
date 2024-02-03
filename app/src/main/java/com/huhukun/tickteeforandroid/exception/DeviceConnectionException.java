package com.huhukun.tickteeforandroid.exception;

import java.io.IOException;

/**
 * Created by kun on 19/08/2014.
 */
public class DeviceConnectionException extends Exception {
    private boolean retry;

    public DeviceConnectionException(String msg, IOException e) {

    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public boolean isRetry() {
        return retry;
    }
}
