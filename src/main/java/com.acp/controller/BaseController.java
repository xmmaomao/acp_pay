package com.acp.controller;

import com.acp.service.HttpService;
import com.acp.util.BeanUtil;
import com.jfinal.core.Controller;

import java.util.Map;

/**
 * Created by Administrator on 2016/6/23.
 */
public class BaseController extends Controller {


	/**
	 * 渲染错误json
	 *
	 * @param message
	 */
	protected void renderErrorJson(String message) {
		renderJson(HttpService.createErrorMap(message));
	}

	/**
	 * 渲染成功json
	 *
	 * @param data
	 */
	protected void renderSuccessJson(Map<String, Object> data) {
		renderJson(HttpService.createSuccessMap(data));
	}

	/**
	 * 渲染成功json
	 *
	 * @param data
	 */
	protected void renderSuccessObj(Object data) throws BeanUtil.BeanException {
		renderJson(HttpService.createSuccessMap(BeanUtil.bean2map(data)));
	}

	/**
	 * 渲染错误attr
	 *
	 * @param message
	 * @param view 视图页面
	 */
	protected void renderErrorAttr(String message, String view) {
		setAttrs(HttpService.createErrorMap(message));
		renderFreeMarker(view);
	}

	/**
	 * 渲染成功attrs
	 *
	 * @param data
	 * @param view
	 */
	protected void renderSuccessAttr(Map<String, Object> data, String view) {
		setAttrs(HttpService.createSuccessMap(data));
		renderFreeMarker(view);
	}

}
