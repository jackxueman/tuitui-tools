package com.tuitui.tool.http.invoker;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpClientFormPost extends AbstractHttpClientInvoker {
//	private static final Logger log = LoggerFactory.getLogger(HttpClientFormPost.class);

	@SuppressWarnings("unchecked")
    @Override
	protected HttpUriRequest constructRequest(String url, Object param) {
		HttpPost post = new HttpPost(url);
		if (param != null) {
			Map<String, String> paramMap = (Map<String, String>) param;
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		}

		return post;
	}

}
