package com.tuitui.tool.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Map;

/**
 * Http 工具类
 *
 * @author liujianxue
 * @date 2018/1/5
 */
public final class HttpUtil {

    private HttpUtil() {
    }

    public static String postJson(String url, String json, Map<String, String> headers) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                post.setHeader(entry.getKey(), entry.getValue());
            }
        }

        String content = null;
        try {
            StringEntity entity = new StringEntity(json);
            entity.setContentType(MediaType.APPLICATION_JSON_VALUE);
            post.setEntity(entity);

            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(res.getEntity(), "UTF-8");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
            try {
                client.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }
}
