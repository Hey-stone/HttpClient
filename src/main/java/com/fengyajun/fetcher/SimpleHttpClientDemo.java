package com.fengyajun.fetcher;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * @Title: SimpleHttpClientDemo.java
 * @Package com.fyj.utils.fetcher
 * @author 冯亚军
 * @date 2017年3月2日上午11:51:23
 * @Description:简单的HttpClient请求demo
 * @version V1.0
 */
public class SimpleHttpClientDemo {
	
	private void addDefaultHead(HttpRequestBase request){
		request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		request.addHeader("Accept-Encoding", "gzip, deflate, sdch");
		request.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		request.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
	}
	
	private CloseableHttpClient client;
	
	public void shutdown() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client = null;
		}
	}
	
	public void request(HttpResult result){
		try {
			if (result.isPost())
				doPost("utf-8", result);
			else
				doGet("utf-8", result);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

	}
	

	/** 
	* @Title: doPost 
	* @Description: <这里用一句话描述这个方法的作用>
	* @param encoding
	* @param result
	* @throws ParseException
	* @throws IOException
	* @return void    返回类型 
	*/
	private void doPost(String encoding, HttpResult result)
			throws ParseException, IOException {
	//	HttpClient client = HttpClients.createDefault();
		HttpClient client = getHttpClient();
		HttpRequestBase request = new HttpPost(result.getUrl());
		// 装填参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (result.getParam() != null) {
			for (Entry<String, Object> entry : result.getParam().entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
			}
		}
		// 请求头
		if (result.getRequestHead() == null || result.getRequestHead() .size() == 0 ){
			addDefaultHead(request);
		} else {
			for (Map.Entry<String, String> kv : result.getRequestHead() .entrySet()) {
				request.addHeader(kv.getKey(), kv.getValue());
			}
		}

		// 设置参数到请求对象中
		((HttpPost) request).setEntity(new UrlEncodedFormEntity(nvps, encoding));

		// 执行请求操作，并拿到结果（同步阻塞）
		HttpResponse response = client.execute(request);
		
		StatusLine sline = response.getStatusLine();
		
		if (sline != null)
			result.setCode(sline.getStatusCode());
		// FIXME 5xx处理
		if (result.getCode() >= 500) {
			return;
		}
		// FIXME 4xx
		if (result.getCode() >= 400) {
			switch (result.getCode()) {
			case 403:
				break;
			case 404:
			case 410:
				break;
			case 407:
				break;
			default:
			}
			return;
		}
		
		HttpEntity entity = response.getEntity();
		Header ceheader = entity.getContentEncoding();
		if (ceheader != null) {
			HeaderElement[] codecs = ceheader.getElements();
			for (int i = 0; i < codecs.length; i++) {
				if (codecs[i].getName().equalsIgnoreCase("gzip")) {
					entity = new GzipDecompressingEntity(entity);
					break;
				} else if (codecs[i].getName().equalsIgnoreCase("deflate")) {
					entity = new DeflateDecompressingEntity(entity);
					break;
				}
			}
		}
		if (result.isBinary()) {
			byte[] content = EntityUtils.toByteArray(entity);
			result.setContent(content);
		} else {
			String content = EntityUtils.toString(entity, encoding);
			result.setContent(content);
		}
		
	}
	
	
	private void doGet(String encoding,HttpResult  result )
			throws ParseException, IOException {
		HttpClient client = getHttpClient();
		HttpRequestBase request = new HttpGet(result.getUrl());
		
		// 请求头
		if (result.getRequestHead() == null || result.getRequestHead().size() == 0 ){
			addDefaultHead(request);
		} else {
			for (Map.Entry<String, String> kv : result.getRequestHead().entrySet()) {
				request.addHeader(kv.getKey(), kv.getValue());
			}
		}

		// 执行请求操作，并拿到结果（同步阻塞）
		HttpResponse response = client.execute(request);
		// 获取结果实体
		HttpEntity entity = response.getEntity();
		
		if (result.isBinary()) {
			byte[] content = EntityUtils.toByteArray(entity);
			result.setContent(content);
		} else {
			String content = EntityUtils.toString(entity, encoding);
			result.setContent(content);
		}
		
		// 释放链接
		// response.close();
	}

	/**
	 * @Title: getHttpClient
	 * @Description: <这里用一句话描述这个方法的作用>
	 * @return
	 * @return HttpClient 返回类型
	 */
	public CloseableHttpClient getHttpClient() {
		final int maxPerRoute = 1000;
		final int maxTotal = maxPerRoute * 2;

		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setDefaultMaxPerRoute(maxPerRoute);
		connManager.setMaxTotal(maxTotal);

		HttpClientBuilder httpbuilder = HttpClientBuilder.create().setConnectionManager(connManager)
				.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).setSoReuseAddress(true).build());
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setAuthenticationEnabled(true)
				.setConnectionRequestTimeout(5000).setConnectTimeout(5000).setSocketTimeout(50000)
				.setRedirectsEnabled(true).setRelativeRedirectsAllowed(true).setMaxRedirects(5);
		/*
		 * if (autoCookie) {
		 * requestConfigBuilder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
		 * ; }
		 */
		httpbuilder.setDefaultRequestConfig(requestConfigBuilder.build());
		// if (!isLocal) {
		// String proxyInfo = ProxyUtil.getProxy(getMarketName(),
		// getClientType(), getNo());
		// if (!StringUtils.isBlank(proxyInfo)) {
		// String host = proxyInfo.split("[=]")[0];
		// int port = Integer.parseInt(proxyInfo.split("[=]")[1]);
		// logger.info("{}-{}-{},使用代理：{}:{}", getMarketName(), getClientType(),
		// getNo(), host, port);
		// HttpHost proxy = new HttpHost(host, port);
		// if (!proxy.getHostName().equals("127.0.0.1")) {
		// httpbuilder.setProxy(proxy);
		// }
		// } else {
		// logger.info("获取代理失败，使用本机ip");
		// }
		// }

		// HttpHost proxy = new HttpHost("192.168.1.158", 8888);
		// httpbuilder.setProxy(proxy);
		client = httpbuilder.build();
		return client;
	}
	
/*	public static void main(String[] args) {
		System.out.println(String.valueOf(DateTime.now(DateTimeZone.forOffsetHours(8))));
	}*/

}
