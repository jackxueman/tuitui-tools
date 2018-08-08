package com.tuitui.tool.http;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * http工具类（添加连接池）
 *
 * @author liujianxue
 * @since  2018/5/20
 */
public class HttpClientUtil {

    /**
     * 超时时间
     */
    private static final int TIME_OUT = 10 * 1000;

    /**
     * 重试次数
     */
    private static final int RETRY_COUNT = 5;

    /**
     * 最大连接数
     */
    private static final int MAX_TOTAL = 200;

    /**
     * 每个路由的最大连接数
     */
    private static final int MAX_PER_ROUTE = 40;

    /**
     * 最大路由数
     */
    private static final int MAX_ROUTE = 100;

    private final static Object SYNC_LOCK = new Object();

    private static CloseableHttpClient httpClient = null;

    /**
     * 超时时间配置
     *
     * @param httpRequestBase
     */
    private static void config(HttpRequestBase httpRequestBase) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(TIME_OUT)
                .setConnectTimeout(TIME_OUT)
                .setSocketTimeout(TIME_OUT)
                .build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient
     *
     * @param url 请求url
     * @return
     */
    private static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (SYNC_LOCK) {
                if (httpClient == null) {
                    httpClient = createHttpClient(hostname, port);
                }
            }
        }
        return httpClient;
    }

    /**
     * 连接池初始化
     *
     * @param hostname host
     * @param port     端口
     * @return
     */
    private static CloseableHttpClient createHttpClient(String hostname, int port) {
        ConnectionSocketFactory factory = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory socketFactory = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", factory)
                .register("https", socketFactory).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);

        HttpHost httpHost = new HttpHost(hostname, port);

        // 将最大连接数增加
        cm.setMaxTotal(MAX_TOTAL);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), MAX_ROUTE);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = (IOException exception, int executionCount, HttpContext
                context) -> {
            if (executionCount >= RETRY_COUNT) {
                // 如果已经重试了5次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                // 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                // 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // 超时
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // 目标服务器不可达
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL握手异常
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();

            // 如果请求是幂等的，就再次尝试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };

        return HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
    }

    /**
     * 参数加载
     *
     * @param httpPost
     * @param json
     */
    private static void setPostParams(HttpPost httpPost, String json) {
        try {
            StringEntity entity = new StringEntity(json);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送post请求
     *
     * @param url    地址
     * @param json 参数
     */
    public static String post(String url, String json) {
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        setPostParams(httppost, json);
        return sendHttp(url, httppost);
    }

    /**
     * 发送post请求 BI
     *
     * @param url    地址
     * @param json 参数
     */
    public static String post(String url, String json, Map<String, String> headers) {
        HttpPost httppost = new HttpPost(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httppost.setHeader(entry.getKey(), entry.getValue());
        }
        config(httppost);
        setPostParams(httppost, json);
        return sendHttp(url, httppost);
    }

    /**
     * 发送Get请求
     */
    public static String get(String url) {
        HttpGet httpget = new HttpGet(url);
        config(httpget);
        return sendHttp(url, httpget);
    }

    /**
     * 连接池获取HttpClient
     *
     * @param url
     * @param request
     * @return
     */
    private static String sendHttp(String url, HttpUriRequest request){
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(request, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
