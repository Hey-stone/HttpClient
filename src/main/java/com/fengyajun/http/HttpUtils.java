package com.fengyajun.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

public class HttpUtils {
	public static Charset CHARSET = Charset.forName("utf-8");

	public static Charset getCharset(String contentType) {
		if (contentType != null) {
			for (String elem : contentType.split(";")) {
				String[] ss = elem.split("=");
				if (ss[0].trim().equalsIgnoreCase("charset")) {
					try {
						return Charset.forName(ss[1].trim().toLowerCase());
					} catch (Throwable e) {
					}
				}
			}
		}
		return CHARSET;
	}

	public static class HttpResult {
		private int code;
		private Object result;

		public int getCode() {
			return code;
		}

		public Object getResult() {
			return result;
		}

		void setCode(int code) {
			this.code = code;
		}

		void setResult(Object result) {
			this.result = result;
		}
	}

	public static HttpResult getAsString(String url, Charset charset) throws Exception {
		return get(url, false, charset);
	}

	public static HttpResult getAsString(String url) throws Exception {
		return get(url, false, null);
	}

	public static HttpResult getAsBytes(String url) throws Exception {
		return get(url, true, null);
	}

	public static HttpResult get(String url, boolean bytes, Charset charset) throws Exception {
		URL u = new URL(url);
		HttpURLConnection uc = (HttpURLConnection) u.openConnection();
		uc.setRequestMethod("GET");
		uc.setUseCaches(false);
		uc.setConnectTimeout(5000);
		uc.setReadTimeout(10000);
		uc.setRequestProperty("Accept", "*/*");
		uc.setRequestProperty("Accept-Encoding", "gzip,deflate");
		uc.connect();

		int code = uc.getResponseCode();
		String encoding = uc.getContentEncoding();
		InputStream is = null;
		if (encoding != null) {
			for (String elem : encoding.split(",")) {
				if (elem.equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(uc.getInputStream());
					break;
				} else if (elem.equalsIgnoreCase("deflate")) {
					is = new DeflaterInputStream(uc.getInputStream());
					break;
				}
			}
		}
		if (is == null)
			is = uc.getInputStream();

		HttpResult hr = new HttpResult();
		hr.setCode(code);
		int len = uc.getContentLength();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(len > 0 ? len : 200000);
		byte[] b = new byte[4096];
		try {
			int n;
			while ((n = is.read(b)) > 0)
				bos.write(b, 0, n);
		} finally {
			is.close();
			uc.disconnect();
		}
		if (bytes)
			hr.setResult(bos.toByteArray());
		else {
			hr.setResult(new String(bos.toByteArray(), charset != null ? charset : getCharset(uc.getContentType())));
		}

		return hr;
	}

	public static HttpResult postAsString(String url, Object param, Charset charset) throws Exception {
		return post(url, param, false, charset);
	}

	public static HttpResult postAsString(String url, Object param) throws Exception {
		return post(url, param, false, null);
	}

	public static HttpResult postAsBytes(String url, Object param, Charset charset) throws Exception {
		return post(url, param, true, charset);
	}

	public static HttpResult postAsBytes(String url, Object param) throws Exception {
		return post(url, param, true, null);
	}

	public static HttpResult post(String url, Object param, boolean bytes, Charset charset) throws Exception {
		URL u = new URL(url);
		HttpURLConnection uc = (HttpURLConnection) u.openConnection();
		uc.setRequestMethod("POST");
		uc.setUseCaches(false);
		uc.setConnectTimeout(5000);
		uc.setReadTimeout(10000);
		uc.setDoOutput(true);
		byte[] bb;
		if (param instanceof String) {
			uc.setRequestProperty("Content-Type", charset.name());
			bb = ((String) param).getBytes(charset);
		} else if (param instanceof byte[]) {
			bb = (byte[]) param;
		} else {
			throw new Exception("invalid param");
		}
		uc.setRequestProperty("Accept", "*/*");
		uc.setRequestProperty("Accept-Encoding", "gzip,deflate");
		uc.setRequestProperty("Content-Length", String.valueOf(bb.length));
		uc.connect();

		OutputStream os = uc.getOutputStream();
		os.write(bb);
		os.flush();

		int code = uc.getResponseCode();
		String encoding = uc.getContentEncoding();
		InputStream is = null;
		if (encoding != null) {
			for (String elem : encoding.split(",")) {
				if (elem.equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(uc.getInputStream());
					break;
				} else if (elem.equalsIgnoreCase("deflate")) {
					is = new DeflaterInputStream(uc.getInputStream());
					break;
				}
			}
		}
		if (is == null)
			is = uc.getInputStream();

		HttpResult hr = new HttpResult();
		hr.setCode(code);
		int len = uc.getContentLength();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(len > 0 ? len : 200000);
		byte[] b = new byte[4096];
		try {
			int n;
			while ((n = is.read(b)) > 0)
				bos.write(b, 0, n);
		} finally {
			os.close();
			is.close();
			uc.disconnect();
		}
		if (bytes)
			hr.setResult(bos.toByteArray());
		else {
			hr.setResult(new String(bos.toByteArray(), charset != null ? charset : getCharset(uc.getContentType())));
		}

		return hr;
	}
}
