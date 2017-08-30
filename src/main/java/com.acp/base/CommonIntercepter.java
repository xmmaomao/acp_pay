package com.acp.base;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/6/17.
 */
public class CommonIntercepter implements Interceptor {
	private static final Logger logger = Logger.getLogger(CommonIntercepter.class);

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		long start = System.currentTimeMillis();
		try {
			inv.invoke();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			controller.renderJson(CommonException.getDefaultError().toMap());
		} finally {
			logger.info(inv.getActionKey() + ","
					+ JSON.toJSONString(controller.getParaMap())
					+ "【共耗时：" + BigDecimal.valueOf(System.currentTimeMillis() - start).divide(BigDecimal.valueOf(1000)) + "秒】");
		}
	}
}
