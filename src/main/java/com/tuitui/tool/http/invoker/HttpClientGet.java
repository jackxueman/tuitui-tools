package com.tuitui.tool.http.invoker;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpClientGet extends AbstractHttpClientInvoker {
	
	private static final Logger logger = LoggerFactory.getLogger("workOrderCommonLogger");

	@SuppressWarnings("unchecked")
	@Override
	protected HttpUriRequest constructRequest(String url, Object param) {
		//处理url中已经包含?的情况
		boolean alreadyHasParam = false;
		if(url.indexOf('?')!=-1){
			alreadyHasParam = true;
		}
		
		StringBuilder urlBuilder = new StringBuilder(url);
		// 构造 get url
		Map<String, Object> paramMap = (Map<String, Object>) param;
		try {
			if (paramMap != null && !paramMap.isEmpty()) {
				Set<String> keySet = paramMap.keySet();
				Iterator<String> it = keySet.iterator();
				if(!alreadyHasParam){
					String firstKey = it.next();
					urlBuilder.append("?").append(firstKey).append("=").append(URLEncoder.encode(String.valueOf(paramMap.get(firstKey)),"UTF-8"));
				}
				
				while (it.hasNext()) {
					String key = it.next();
					urlBuilder.append("&").append(key).append("=").append(URLEncoder.encode(String.valueOf(paramMap.get(key)),"UTF-8"));
				}
			}
		} catch (Exception e) {
			logger.error("construct HttpClientGet exception,{}", e.getMessage());
			return null;
		}
		
		HttpGet get = new HttpGet(urlBuilder.toString());
		return get;
	}
}
