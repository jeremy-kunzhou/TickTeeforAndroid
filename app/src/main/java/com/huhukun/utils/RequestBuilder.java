package com.huhukun.utils;

import android.net.Uri;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by kun on 20/08/2014.
 */
public abstract class RequestBuilder {

    public static HttpPost buildPost(String url, List<NameValuePair> headers, List<NameValuePair> params) throws UnsupportedEncodingException {


        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (NameValuePair nvp : headers) {
                httpPost.addHeader(nvp.getName(), nvp.getValue());
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        return httpPost;
    }

    public static HttpGet buildGet(String url, List<NameValuePair> headers, List<NameValuePair> params) throws UnsupportedEncodingException {

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        if (params != null) {
            for (NameValuePair nvp : params) {
                uriBuilder.appendQueryParameter(nvp.getName(), nvp.getValue());
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
        for (NameValuePair nvp : headers) {
            httpGet.addHeader(nvp.getName(), nvp.getValue());
        }
        return httpGet;
    }

    public static HttpDelete buildDelete(String url, List<NameValuePair> headers, List<NameValuePair> params) throws UnsupportedEncodingException {

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        if (params != null) {
            for (NameValuePair nvp : params) {
                uriBuilder.appendQueryParameter(nvp.getName(), nvp.getValue());
            }
        }
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build().toString());
        for (NameValuePair nvp : headers) {
            httpDelete.addHeader(nvp.getName(), nvp.getValue());
        }
        return httpDelete;
    }
}
