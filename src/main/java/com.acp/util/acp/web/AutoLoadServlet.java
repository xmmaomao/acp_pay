package com.acp.util.acp.web;

import com.acp.util.acp.sdk.SDKConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Description： 加载配置信息
 * <br /> Author： galsang
 */
public class AutoLoadServlet extends HttpServlet{
    @Override
    public void init(ServletConfig config) throws ServletException {

        SDKConfig.getConfig().loadPropertiesFromSrc();

        super.init();
    }
}
