package com.acp.controller;

import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2016/4/26 0026.
 */
public class IndexController extends BaseController {

	private static final Logger logger = Logger.getLogger(IndexController.class);

	public void index() {
		renderJsp("index.html");
	}


}
