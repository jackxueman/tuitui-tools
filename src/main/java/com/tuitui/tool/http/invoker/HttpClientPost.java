package com.tuitui.tool.http.invoker;

import com.glab.log.LoggerManager;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;

public class HttpClientPost extends AbstractHttpClientInvoker {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected HttpUriRequest constructRequest(String url, Object param) {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-type", "application/json");

		if (param != null) {

			StringEntity entity = null;
			try {
				String jsonParam = objectMapper.writeValueAsString(param);

				entity = new StringEntity(jsonParam, "UTF-8");
			} catch (Exception e) {
				LoggerManager.exception(e, "httpclient post exception");
			}

			post.setEntity(entity);
		}

		return post;
	}

}
