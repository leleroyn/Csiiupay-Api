package com.ucs.xcbank.csiiupay.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {
    static   Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    public static Map<String,String> post(String url, List<NameValuePair> params) {

        HttpClient httpClient = new DefaultHttpClient();
        Map<String,String> responseMap = new HashMap<>();
        try {
            HttpPost  httpPost = new HttpPost(url);
            httpPost.setHeader("Connection", "close");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            responseMap.put("Status",Integer.toString( response.getStatusLine().getStatusCode()));
            responseMap.put("Content", EntityUtils.toString(entity, "UTF-8"));
        }
        catch (Exception ex){
            logger.error(ex.getMessage(),ex);
            httpClient.getConnectionManager().shutdown();
        }
        return responseMap;
    }

}
