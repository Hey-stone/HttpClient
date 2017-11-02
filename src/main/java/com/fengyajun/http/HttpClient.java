package com.fengyajun.http;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: HttpClient.java 
 * @Package com.fyj.utils.http  
 * @author 冯亚军
 * @date 2017年5月18日下午4:04:14
 * @Description:封装HttpClient请求
 * @version V1.0   
 */
public class HttpClient {
	
	private final static Logger LOG = LoggerFactory.getLogger(HttpClient.class);
	
	private CloseableHttpClient httpClient;
	
	public void shutdown() {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpClient = null;
		}
	}

}
