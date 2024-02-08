package com.huhukun.utils;

/**
 * Created by kun on 15/08/2014.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JSONParser {


    public static String getStringFromUrlViaPostNew(String url, List<NameValuePair> headers, List<NameValuePair> params) {
        String json = "";
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            if (params != null) {
                for (NameValuePair param : params) {
                    bodyBuilder.add(param.getName(), param.getValue());
                }
            }

            RequestBody formBody = bodyBuilder.build();
            Request.Builder requestBuilder = new Request.Builder().url(url);
            if (headers != null) {
                for (NameValuePair header : headers) {
                    requestBuilder.addHeader(header.getName(), header.getValue());
                }
            }
            Request request = requestBuilder
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                json = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String getStringFromUrlViaPost(String url, List<NameValuePair> headers, List<NameValuePair> params) {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            if (headers != null) {
                for (NameValuePair nvp : headers) {
                    httpPost.addHeader(nvp.getName(), nvp.getValue());
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // return String
        return json;
    }


    public static String getStringFromUrlViaGetNew(String url, List<NameValuePair> headers, List<NameValuePair> params) {
        String json = "";
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            if (params != null) {
                for (NameValuePair param : params) {
                    urlBuilder.addQueryParameter(param.getName(), param.getValue());
                }
            }

            OkHttpClient client = new OkHttpClient();

            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build());
            if (headers != null) {
                for (NameValuePair header : headers) {
                    requestBuilder.addHeader(header.getName(), header.getValue());
                }
            }
            Request request = requestBuilder.build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                json = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String getStringFromUrlViaGet(String urlString, List<NameValuePair> headers, List<NameValuePair> params) {

        return getStringFromUrlViaGetNew(urlString, headers, params);
//
//        InputStream is = null;
//        JSONObject jObj = null;
//        String json = "";
//        // Making HTTP request
//        try {
//            Uri.Builder uriBuilder = Uri.parse(urlString).buildUpon();
//            if (params != null) {
//                for (NameValuePair nvp : params) {
//                    uriBuilder.appendQueryParameter(nvp.getName(), nvp.getValue());
//                }
//            }
//            // defaultHttpClient
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
//            for (NameValuePair nvp : headers) {
//                httpGet.addHeader(nvp.getName(), nvp.getValue());
//            }
//
//            HttpResponse httpResponse = httpClient.execute(httpGet);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            is = httpEntity.getContent();
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    is, "iso-8859-1"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "n");
//            }
//            is.close();
//            json = sb.toString();
//        } catch (Exception e) {
//            Log.e("Buffer Error", "Error converting result " + e.toString());
//        }
//
//
//        // return String
//        return json;
    }
}
