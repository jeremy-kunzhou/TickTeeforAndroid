package com.huhukun.tickteeforandroid.providers;

import com.huhukun.utils.RequestBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * Created by kun on 20/08/2014.
 */
public class NetworkUtils extends RequestBuilder {

    private String url;
    private List<NameValuePair> header;
    private List<NameValuePair> params;

    private NetworkUtils(String url) {this.url = url;}

    public static NetworkUtils BUILDER(String url){
        NetworkUtils networkUtils = new NetworkUtils(url);
        return networkUtils;
    }

    public static NetworkUtils BUILDER(String url, long id){

        NetworkUtils networkUtils = new NetworkUtils(String.format(url, id));
        return networkUtils;
    }

    public NetworkUtils addParam(String name, String value)
    {
        synchronized (params) {
            if (params == null) {
                this.params = new ArrayList<NameValuePair>();
            }
            params.add(new BasicNameValuePair(name, value));
        }
        return this;

    }

    public NetworkUtils addHeader(String name, String value)
    {
        synchronized (header) {
            if (header == null) {
                this.header = new ArrayList<NameValuePair>();
            }
            header.add(new BasicNameValuePair(name, value));
        }
        return this;

    }

    public NetworkUtils setHeader(List<NameValuePair> header)
    {
        this.header = header;
        return this;

    }

    public NetworkUtils setParams(List<NameValuePair> params)
    {
        this.params = params;
        return this;

    }

    public Request toPost(String raw) throws UnsupportedEncodingException {
        return this.buildPostNew(url, header, raw);
    }

    public Request toPut(String raw) throws UnsupportedEncodingException {
        return this.buildPutNew(url, header, raw);
    }

    public Request toGet() throws UnsupportedEncodingException {
        return this.buildGetNew(url, header, params);
    }

    public Request toDelete() throws UnsupportedEncodingException {
        return this.buildDeleteNew(url, header);
    }
}
