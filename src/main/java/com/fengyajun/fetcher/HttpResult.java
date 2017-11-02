package com.fengyajun.fetcher;

import java.util.Map;

/**
 * @Title: HttpResult.java 
 * @Package com.fyj.utils.fetcher  
 * @author 冯亚军
 * @date 2017年5月18日下午4:28:52
 * @Description: 封装http返回的结果
 * @version V1.0   
 */
public class HttpResult {
	
	private Object content;

	private int code;
	
	/**
	 * 是否允许自动重定向
	 */
	private boolean noRedirect;
	
	/**
	 * 是否post请求，默认get
	 */
	private boolean post;
	
	/**
	 * 内容类型
	 */
	private boolean binary;
	
	/** 
	* @Fields hasMore : 是否还有下一页默认没有
	*/ 
	private boolean hasMore;
	
	/** 
	* @Fields InitUrl : 初始抓取的url
	*/ 
	private String InitUrl;
	
	/** 
	* @Fields Url : 抓取的url
	*/ 
	private String Url;
	
	private Map<String, String> requestHead;
	
	private Map<String, String> responseHead;
	
	private Map<String, Object> param;
	
	private Object other;
	
	private Object data;

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public boolean isNoRedirect() {
		return noRedirect;
	}

	public void setNoRedirect(boolean noRedirect) {
		this.noRedirect = noRedirect;
	}

	public boolean isPost() {
		return post;
	}

	public void setPost(boolean post) {
		this.post = post;
	}
	
	public boolean isBinary() {
		return this.binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public String getInitUrl() {
		return InitUrl;
	}

	public void setInitUrl(String initUrl) {
		InitUrl = initUrl;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public Map<String, String> getRequestHead() {
		return requestHead;
	}

	public void setRequestHead(Map<String, String> requestHead) {
		this.requestHead = requestHead;
	}

	public Map<String, String> getResponseHead() {
		return responseHead;
	}

	public void setResponseHead(Map<String, String> responseHead) {
		this.responseHead = responseHead;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public Object getOther() {
		return other;
	}

	public void setOther(Object other) {
		this.other = other;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
