package com.tuitui.tool.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * http异步请求util
 *
 * @author liujianxue
 * @since 2018/1/31
 */
public final class HttpAsyncClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpAsyncClientUtil.class);

    private HttpAsyncClientUtil() {

    }

    /**
     * 批量get请求
     *
     * @param requestMap 请求map
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static Map<String, String> batchGet(Map<String, String> requestMap) {
        Map<String, String> responseMap = new HashMap<>();

        CountDownLatch latch = new CountDownLatch(requestMap.size());
        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
            httpclient.start();

            requestMap.forEach((key, value) -> {
                HttpGet httpGet = new HttpGet(value);

                httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(HttpResponse response) {
                        HttpEntity entity = response.getEntity();
                        try {
                            responseMap.put(key, IOUtils.toString(entity.getContent(), "UTF-8"));
                        } catch (IOException e) {
                            logger.error("url: {} , batchGet response parse error, url: {}", WebUtil.getRequestId(), value);
                        }
                        latch.countDown();
                    }

                    @Override
                    public void failed(Exception e) {
                        logger.error("url: {} , batchGet response parse error, url: {}", WebUtil.getRequestId(), value);
                        latch.countDown();
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                    }
                });
            });
            latch.await(30, TimeUnit.SECONDS);
            httpclient.close();
        } catch (Exception e) {
            logger.error("url: {} , batchGet error, params: {}", WebUtil.getRequestId(), requestMap);
            return Collections.emptyMap();
        }
        return responseMap;
    }
}
