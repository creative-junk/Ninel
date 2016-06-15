package com.crysoft.me.pichat.Network;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxx on 6/15/2016.
 */
public class HttpRequest {
    DefaultHttpClient httpClient;
    HttpContext localContext;
    private  String ret;

    HttpResponse response = null;
    HttpPost httpPost = null;
    HttpGet httpGet = null;
    Map.Entry me;
    Iterator i;

    public HttpRequest(){
        HttpParams httpParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 50000);
        HttpConnectionParams.setSoTimeout(httpParams, 50000);
        httpClient = new DefaultHttpClient(httpParams);
        localContext = new BasicHttpContext();
    }
    public void clearCookies() {
        httpClient.getCookieStore().clear();
    }
    public String sendGet(String url){
        httpGet = new HttpGet(url);

        try{
            response = httpClient.execute(httpGet);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            ret = EntityUtils.toString(response.getEntity());
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    public String postData(String url, List<NameValuePair> nameValuePairs){
        String response = null;
        try{
            ResponseHandler<String> res = new BasicResponseHandler();
            HttpPost postMethod = new HttpPost(url);
            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(postMethod, res);
        } catch (Exception e){
            response = "" + e;
        }
        return  response;
    }
}
