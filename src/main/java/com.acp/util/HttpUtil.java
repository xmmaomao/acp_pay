package com.acp.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class HttpUtil {
	private static final Logger logger = Logger.getLogger(HttpUtil.class);

	public static String get(String url) {
		return get(url, "UTF-8");
	}

	/**
	 * 使用Get方式获取数据
	 * 
	 * @param url URL包括参数，http://HOST/XX?XX=XX&XXX=XXX
	 * @param charset
	 * @return
	 */
	public static String get(String url, String charset) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection connection = ((HttpURLConnection) realUrl.openConnection());
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * POST请求，字符串形式数据
	 * 
	 * @param url 请求地址
	 * @param params 请求数据
	 * @param charset 编码方式
	 */
	public static String post(String url, String params, String charset) {

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(params);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String post(String url, Map<String, Object> params) {
		return post(url, params, "UTF-8");
	}

	/**
	 * POST请求，Map形式数据
	 * 
	 * @param url 请求地址
	 * @param params 请求数据
	 * @param charset 编码方式
	 */
	public static String post(String url, Map<String, Object> params, String charset) {
		StringBuffer buffer = new StringBuffer();
		if (params != null && !params.isEmpty()) {
			int i = 0;
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				try {
					Object value = entry.getValue();
					if (value != null) {
						buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(String
							.valueOf(value), "UTF-8"));
					} else {
						buffer.append(entry.getKey()).append("=");
					}
				} catch (UnsupportedEncodingException e) {
					return StringUtil.EMPTY;
				}
				if (i++ != params.size() - 1) {
					buffer.append("&");
				}
			}
		}

		PrintWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = ((HttpURLConnection) realUrl.openConnection());
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Length", String.valueOf(buffer.toString()
				.getBytes().length));
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(buffer);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			String lineSeparator = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				result.append(line).append(lineSeparator);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		return result.toString();
	}

}
