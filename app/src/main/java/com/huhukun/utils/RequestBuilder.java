package com.huhukun.utils;

import android.net.Uri;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by kun on 20/08/2014.
 */
public abstract class RequestBuilder {

    public static HttpPost buildPost(String url, List<NameValuePair> headers, String raw) throws UnsupportedEncodingException {


        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (NameValuePair nvp : headers) {
                httpPost.addHeader(nvp.getName(), nvp.getValue());
                httpPost.addHeader("Content-Type", "application/json");
            }
        }
        httpPost.setEntity(new ByteArrayEntity(raw.getBytes("UTF-8")));
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
            httpGet.addHeader("Content-Type", "application/json");

        }

        return httpGet;
    }

    public static HttpDelete buildDelete(String url, List<NameValuePair> headers) throws UnsupportedEncodingException {


        HttpDelete httpDelete = new HttpDelete(url);
        for (NameValuePair nvp : headers) {
            httpDelete.addHeader(nvp.getName(), nvp.getValue());
            httpDelete.addHeader("Content-Type", "application/json");

        }
        return httpDelete;
    }

    public static HttpPut buildPut(String url, List<NameValuePair> headers, String raw) throws UnsupportedEncodingException {


        HttpPut httpPut = new HttpPut(url);
        for (NameValuePair nvp : headers) {
            httpPut.addHeader(nvp.getName(), nvp.getValue());
            httpPut.addHeader("Content-Type", "application/json");

        }
        httpPut.setEntity(new ByteArrayEntity(raw.getBytes("UTF-8")));
        return httpPut;
    }
}
