package com.tuitui.tool.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpClient4ServicePool {

    public static int count = 0;

    private static final Log logger = LogFactory.getLog(HttpClient4ServicePool.class);
    private static ObjectMapper mapper = new ObjectMapper();
    /**
     * 连接池里的最大连接数
     */
    public static final int MAX_TOTAL_CONNECTIONS = 800;

    /**
     * 每个路由的默认最大连接数
     */
    public static final int MAX_ROUTE_CONNECTIONS = MAX_TOTAL_CONNECTIONS >> 1;

    /**
     * 连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 6000;

    /**
     * 套接字超时时间
     */
    public static final int SOCKET_TIMEOUT = 8000;

    /**
     * 连接池中 连接请求执行被阻塞的超时时间
     */
    public static final long CONN_MANAGER_TIMEOUT = 10000;

    /**
     * http连接相关参数
     */
    private static HttpParams parentParams;

    /**
     * http线程池管理器
     */
    private static PoolingClientConnectionManager cm;

    /**
     * http客户端
     */
    private static HttpClient httpClient;

    public static HttpClient getHttpClient() {
        return new DefaultHttpClient();
    }

    public static String post(String url, Map<String, String> params) {
        HttpClient httpclient = getHttpClient();
        String body = null;

        HttpPost post = postForm(url, params);

        body = invoke(httpclient, post);

        return body;
    }

    public static String post(String url, String json) throws UnsupportedEncodingException {
        HttpClient httpclient = getHttpClient();
        String body = null;
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json");
        StringEntity params = null;
        params = new StringEntity(json, "UTF-8");
        post.setEntity(params);
        body = invoke(httpclient, post);
        return body;
    }

    public static String httpPostWithHeader(String url, String json, Map<String, String> headerMap) throws UnsupportedEncodingException {
        HttpClient httpclient = getHttpClient();
        String body = null;

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json");
        post.setHeader("type", headerMap.get("type"));
        post.setHeader("dT", headerMap.get("dT"));

        StringEntity params = null;
        params = new StringEntity(json, "UTF-8");
        post.setEntity(params);

        body = invoke(httpclient, post);

        return body;
    }

    public static String httpPostData(String url, byte[] postData) {
        String body = null;
        HttpClient httpclient = getHttpClient();

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "text/html;charset=UTF-8");

        post.setEntity(new ByteArrayEntity(postData));
        body = invoke(httpclient, post);
        return body;
    }

    public static String post(String url) throws UnsupportedEncodingException {
        return post(url, "");
    }

    public static String get(String url) {
        HttpClient httpclient = getHttpClient();
        HttpGet get = new HttpGet(url);
        String body = invoke(httpclient, get);
        return body;
    }

    private static String invoke(HttpClient httpclient, HttpUriRequest request) {

        HttpResponse response;
        String body = "";
        try {
            response = sendRequest(httpclient, request);
            body = paseResponse(response);
        } catch (Exception e) {
            request.abort();
            logger.error("httpclient pool error", e);
        }
        return body;
    }

    private static String paseResponse(HttpResponse response) throws ParseException, IOException {
        if(response == null){
            return null;
        }
        HttpEntity entity = response.getEntity();
        Header ceheader = entity.getContentEncoding();
        if (ceheader != null) {
            for (HeaderElement element : ceheader.getElements()) {
                if ("gzip".equalsIgnoreCase(element.getName())) {
                    entity = new GzipDecompressingEntity(response.getEntity());
                    response.setEntity(entity);
                    break;
                }
                if ("deflate".equalsIgnoreCase(element.getName())) {
                    entity = new DeflateDecompressingEntity(response.getEntity());
                    response.setEntity(entity);
                    break;
                }
            }
        }

        String body = null;
        String charset = "UTF-8";
        if (null != ContentType.getOrDefault(entity).getCharset()) {
            if (StringUtils.isNotBlank(ContentType.getOrDefault(entity).getCharset().name())) {
                charset = ContentType.getOrDefault(entity).getCharset().name();
            }
        }
        body = EntityUtils.toString(entity, charset);
        EntityUtils.consume(entity);

        return body;
    }

    private static HttpResponse sendRequest(HttpClient httpclient, HttpUriRequest request)
            throws ClientProtocolException, IOException {
        try {
            HttpResponse response = httpclient.execute(request);
            return response;
        } catch (Exception e) {
            request.abort();
            logger.error(e.getMessage()+"httpclient pool error "+request.getURI());
        }
        httpclient.getConnectionManager().shutdown();

        return null;
    }

    private static HttpPost postForm(String url, Map<String, String> params) {

        HttpPost httpost = new HttpPost(url);

        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<>();

            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        }

        return httpost;
    }


}
