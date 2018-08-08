package com.tuitui.tool.http.invoker;

import com.glab.log.LoggerManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpParams;


public class HttpClientPoolUtil {

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
	@SuppressWarnings("unused")
    private static HttpParams parentParams;

	/**
	 * http线程池管理器
	 */
	@SuppressWarnings("unused")
    private static PoolingClientConnectionManager cm;

	/**
	 * http客户端
	 */
	@SuppressWarnings("unused")
    private static HttpClient httpClient;

	/**
	 * 初始化http连接池，设置参数、http头等等信息
	 *
	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		cm = new PoolingClientConnectionManager(schemeRegistry);
		cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
		parentParams = new BasicHttpParams();
		parentParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		parentParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		parentParams.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, CONN_MANAGER_TIMEOUT);
		parentParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
		parentParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
		parentParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		parentParams.setParameter(ClientPNames.HANDLE_REDIRECTS, true);
		// 设置头信息,模拟浏览器
		Collection<Header> collection = new ArrayList<Header>();
		collection.add(new BasicHeader("User-Agent", "DMP-SDK/0.0.1"));
		collection.add(new BasicHeader("Accept", ApiConfig.API_ACCEPT_HEADER));
		collection.add(new BasicHeader("Accept-Language", "zh-cn,zh,en-US,en;q=0.5"));
		collection.add(new BasicHeader("Accept-Charset", "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7"));
		collection.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
		parentParams.setParameter(ClientPNames.DEFAULT_HEADERS, collection);
		httpClient = new DefaultHttpClient(cm, parentParams);
	}

	public static HttpClient getHttpClient() {
		return httpClient;
	}
	*/
	
	public static HttpClient getSimpleHttpClient() {
		return  HttpClientBuilder.create().build();
	}

	/**
	 * 不采用连接池
	 * @param request
	 * @return
	 */
	public static HttpResponse execute(HttpUriRequest request) {
		HttpClient httpClient = getSimpleHttpClient();
		try {
			HttpResponse response = httpClient.execute(request);
			return response;
		} catch (Exception e) {
			request.abort();
			LoggerManager.exception(e, "httpclient pool error ");
		}
		httpClient.getConnectionManager().shutdown();

		return null;
	}

}
