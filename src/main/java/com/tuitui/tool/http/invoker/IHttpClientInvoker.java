package com.tuitui.tool.http.invoker;

import org.apache.http.HttpResponse;

import java.util.Map;

public interface IHttpClientInvoker {
	HttpResponse sendRequest(String url, Object paramObj, Map<String, String> headerMap);
}
