package com.acp.base;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

/**
 * Description： 从 JFinal 排除的 Servlet 对应的路径
 * <br /> Author： galsang
 */
public class ServletExcludeHadler extends Handler {

    /**
     * 从 JFinal 排除的 Servlet 对应的路径
     */
    public static final HashSet<String> servletSet = new HashSet<String>() {{
        add("/autoLoadServlet");
    }};

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (servletSet.contains(target)) {
            return;
        }
        next.handle(target, request, response, isHandled);

    }
}
