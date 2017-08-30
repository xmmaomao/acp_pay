package com.acp.config;

import com.acp.base.CommonIntercepter;
import com.acp.base.OriginHandler;
import com.acp.base.ServletExcludeHadler;
import com.acp.controller.IndexController;
import com.acp.controller.consume.ConsumeController;
import com.acp.controller.query.QueryController;
import com.acp.controller.refund.RefundController;
import com.jfinal.config.*;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.template.Engine;

public class AcpConfig extends JFinalConfig {

    public void configConstant(Constants me) {
        // 加载少量必要配置，随后可用PropKit.get(...)获取值
        PropKit.use("common.properties");
        me.setDevMode(PropKit.getBoolean("devMode", true));
    }

    public void configRoute(Routes me) {

        me.add("/", IndexController.class); // 第三个参数为该Controller的视图存放路径

        /**
         * 支付 和 撤销支付
         */
        me.add("/consume", ConsumeController.class, "/pages");

        /**
         * 退款
         */
        me.add("/refund", RefundController.class, "/pages");

        /**
         * 查询支付状态 和 对账单下载
         */
        me.add("/query", QueryController.class, "/pages");

    }

    @Override
    public void configEngine(Engine me) {

    }

    public void configPlugin(Plugins me) {

    }

    public void configInterceptor(Interceptors me) {
        me.add(new CommonIntercepter());
    }

    public void configHandler(Handlers me) {
        me.add(new ContextPathHandler("base"));
        me.add(new OriginHandler());
        me.add(new ServletExcludeHadler());
    }

    @Override
    public void afterJFinalStart() {

    }

}
