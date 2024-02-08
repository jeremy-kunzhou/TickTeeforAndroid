package com.huhukun.utils;

import android.net.Uri;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by kun on 20/08/2014.
 */
public abstract class RequestBuilder {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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

    public static Request buildPostNew(String url, List<NameValuePair> headers, String raw) throws UnsupportedEncodingException {

        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headers != null) {
            for (NameValuePair header : headers) {
                requestBuilder.addHeader(header.getName(), header.getValue());
            }
        }
        requestBuilder.addHeader("Content-Type", "application/json");

        Request request = requestBuilder.post(RequestBody.create(raw, JSON)).build();

        return request;
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

    public static Request buildGetNew(String url, List<NameValuePair> headers, List<NameValuePair> params) throws UnsupportedEncodingException {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (NameValuePair param : params) {
                urlBuilder.addQueryParameter(param.getName(), param.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build());
        if (headers != null) {
            for (NameValuePair header : headers) {
                requestBuilder.addHeader(header.getName(), header.getValue());
            }
        }
        requestBuilder.addHeader("Content-Type", "application/json");
        Request request = requestBuilder.build();

        return request;
    }

    public static Request buildDeleteNew(String url, List<NameValuePair> headers) throws UnsupportedEncodingException {

        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headers != null) {
            for (NameValuePair header : headers) {
                requestBuilder.addHeader(header.getName(), header.getValue());
            }
        }
        requestBuilder.addHeader("Content-Type", "application/json");

        Request request = requestBuilder.delete().build();

        return request;
    }

    public static HttpDelete buildDelete(String url, List<NameValuePair> headers) throws UnsupportedEncodingException {


        HttpDelete httpDelete = new HttpDelete(url);
        for (NameValuePair nvp : headers) {
            httpDelete.addHeader(nvp.getName(), nvp.getValue());
            httpDelete.addHeader("Content-Type", "application/json");

        }
        return httpDelete;
    }

    public static Request buildPutNew(String url, List<NameValuePair> headers, String raw) throws UnsupportedEncodingException {


        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headers != null) {
            for (NameValuePair header : headers) {
                requestBuilder.addHeader(header.getName(), header.getValue());
            }
        }
        requestBuilder.addHeader("Content-Type", "application/json");

        Request request = requestBuilder.put(RequestBody.create(raw, JSON)).build();

        return request;
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
