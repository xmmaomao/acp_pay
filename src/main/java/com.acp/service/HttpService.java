package com.acp.service;

import com.acp.base.CommonException;
import com.acp.util.CollectionUtil;
import com.acp.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/14.
 */
public class HttpService {
	private static final Logger logger = Logger.getLogger(HttpService.class);

	public static Map<String, Object> createErrorMap(String message) {
		return CollectionUtil.arrayAsMap("errorCode", 1, "message", message);
	}

	public static Map<String, Object> createSuccessMap(Map data) {
		return CollectionUtil.arrayAsMap("errorCode", 0, "message", "成功", "data", data);
	}

	/**
	 * 通用api请求，返回结果为json
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static Map<String, Object> doPost(String url, Map<String, Object> params)
		throws CommonException {
		String result = HttpUtil.post(url, params);
		JSONObject json = null;
		try {
			json = JSON.parseObject(result);
		} catch (Exception e) {
			throw new CommonException("网络超时");
		}
		return CollectionUtil.arrayAsMap("errorCode", 0, "data", json);
	}

	/**
	 * 通用api请求，返回结果为string
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doPost4StrResult(String url, Map<String, Object> params)
		throws CommonException {
		return HttpUtil.post(url, params);
	}

}
