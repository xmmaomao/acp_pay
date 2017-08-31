package com.acp.controller.consume;

import com.acp.controller.BaseController;
import com.acp.util.StringUtil;
import com.acp.util.acp.sdk.AcpService;
import com.acp.util.acp.sdk.DemoBase;
import com.acp.util.acp.sdk.SDKConfig;
import com.acp.util.acp.sdk.SDKConstants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description： 消费相关接口
 * <br /> Author： galsang
 */
@Slf4j
public class ConsumeController extends BaseController {

    /**
     * 获取请求参数中所有的信息
     * 当商户上送frontUrl或backUrl地址中带有参数信息的时候，
     * 这种方式会将url地址中的参数读到map中，会导多出来这些信息从而致验签失败，这个时候可以自行修改过滤掉url中的参数或者使用getAllRequestParamStream方法。
     *
     * @param request
     * @return
     */
    private static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (res.get(en) == null || "".equals(res.get(en))) {
                    log.info("======为空的字段名====" + en);
                    res.remove(en);
                }
            }
        }
        return res;
    }

    public void consumeIndex() {
        renderJsp("consume.jsp");
    }

    /**
     * 处理前台提交的数据 == 调用银联确认支付确认接口
     */
    public void frontConsume() {

        //前台页面传过来的
        String merId = getPara("merId");
        String txnAmt = getPara("txnAmt");
        String orderId = getPara("orderId");

        // TODO 待改进
        if(StringUtil.isBlank(merId) || StringUtil.isBlank(txnAmt) || StringUtil.isBlank(orderId)){
            log.error("参数格式不正确！");
            return;
        }

        Map<String, String> requestData = new HashMap<>();

        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        requestData.put("version", DemoBase.version);              //版本号，全渠道默认值
        requestData.put("encoding", DemoBase.encoding);              //字符集编码，可以使用UTF-8,GBK两种方式
        requestData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        requestData.put("txnType", "01");                          //交易类型 ，01：消费
        requestData.put("txnSubType", "01");                          //交易子类型， 01：自助消费
        requestData.put("bizType", "000201");                      //业务类型，B2C网关支付，手机wap支付
        requestData.put("channelType", "08");                      //渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板  08：手机

        /***商户接入参数***/
        requestData.put("merId", merId);                              //商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
        requestData.put("accessType", "0");                          //接入类型，0：直连商户
        requestData.put("orderId", orderId);             //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        requestData.put("txnTime", DemoBase.getCurrentTime());        //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        requestData.put("currencyCode", "156");                      //交易币种（境内商户一般是156 人民币）
        requestData.put("txnAmt", txnAmt);                              //交易金额，单位分，不要带小数点
        //requestData.put("reqReserved", "透传字段");        		      //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,
        // 对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节。出现&={}[]符号时可能导致查询接口应答报文解析失败，建议尽量只传字母数字并使用|分割，或者可以最外层做一次base64编码
        // (base64编码之后出现的等号不会导致解析失败可以不用管)。

        //前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
        //如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
        //异步通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
        requestData.put("frontUrl", DemoBase.frontUrl);

        //后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
        //后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
        //注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码
        //    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
        //    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
        requestData.put("backUrl", DemoBase.backUrl);

        // 订单超时时间。
        // 超过此时间后，除网银交易外，其他交易银联系统会拒绝受理，提示超时。 跳转银行网银交易如果超时后交易成功，会自动退款，大约5个工作日金额返还到持卡人账户。
        // 此时间建议取支付时的北京时间加15分钟。
        // 超过超时时间调查询接口应答origRespCode不是A6或者00的就可以判断为失败。
        requestData.put("payTimeout", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime() + 15 * 60 * 1000));

        //////////////////////////////////////////////////
        //
        //       报文中特殊用法请查看 PCwap网关跳转支付特殊用法.txt
        //
        //////////////////////////////////////////////////

        /**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
        Map<String, String> submitFromData = AcpService.sign(requestData, DemoBase.encoding);  //报文中certId,
        // signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

        String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
        String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData, DemoBase.encoding);   //生成自动跳转的Html表单

        log.info("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);
        //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
//        try {
//            getResponse().getWriter().write(html);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        renderHtml(html);
    }

    public void frontRcvResponse() {

        log.info("FrontRcvResponse前台接收报文返回开始");

        String encoding = getPara(SDKConstants.param_encoding);
        log.info("返回报文中encoding=[" + encoding + "]");
        String pageResult = "/utf8_result.jsp";

        Map<String, String> respParam = getAllRequestParam(getRequest());

        // 打印请求报文
        respParam.forEach((k, v) -> log.info(k + " == " + v));

        Map<String, String> valideData = null;
        StringBuffer page = new StringBuffer();
        if (null != respParam && !respParam.isEmpty()) {
            Iterator<Entry<String, String>> it = respParam.entrySet().iterator();
            valideData = new HashMap<>(respParam.size());
            while (it.hasNext()) {
                Entry<String, String> e = it.next();
                String key = e.getKey();
                String value = e.getValue();
                try {
                    value = new String(value.getBytes(encoding), encoding);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                page.append("<tr><td width=\"30%\" align=\"right\">" + key + "(" + key + ")</td><td>" + value + "</td></tr>");
                valideData.put(key, value);
            }
        }
        if (!AcpService.validate(valideData, encoding)) {
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>失败</td></tr>");
            log.info("验证签名结果[失败].");
        } else {
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>成功</td></tr>");
            log.info("验证签名结果[成功].");
            log.info(valideData.get("orderId")); //其他字段也可用类似方式获取

            String respCode = valideData.get("respCode");
            //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
        }
        getRequest().setAttribute("result", page.toString());
        renderJsp(pageResult);
    }


    public void consumeUndoIndex() {
        renderJsp("consumeUndo.jsp");
    }


    public void ConsumeUndo() {

        String merId = getPara("merId");
        String origQryId = getPara("origQryId");
        String txnAmt = getPara("txnAmt");

        // TODO 待改进
        if(StringUtil.isBlank(merId) || StringUtil.isBlank(txnAmt) || StringUtil.isBlank(origQryId)){
            log.error("参数格式不正确！");
            return;
        }

        Map<String, String> data = new HashMap<>();

        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", DemoBase.version);            //版本号
        data.put("encoding", DemoBase.encoding);          //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "31");                        //交易类型 31-消费撤销
        data.put("txnSubType", "00");                     //交易子类型  默认00
        data.put("bizType", "000201");                    //业务类型 B2C网关支付，手机wap支付
        data.put("channelType", "08");                    //渠道类型，07-PC，08-手机

        /***商户接入参数***/
        data.put("merId", merId);                         //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("accessType", "0");                      //接入类型，商户接入固定填0，不需修改
        data.put("orderId", DemoBase.getOrderId());       //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
        data.put("txnTime", DemoBase.getCurrentTime());   //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("txnAmt", txnAmt);                       //【撤销金额】，消费撤销时必须和原消费金额相同
        data.put("currencyCode", "156");                  //交易币种(境内商户一般是156 人民币)
        //data.put("reqReserved", "透传信息");                 //请求方保留域，，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,
        // 对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节。出现&={}[]符号时可能导致查询接口应答报文解析失败，建议尽量只传字母数字并使用|分割，或者可以最外层做一次base64编码
        // (base64编码之后出现的等号不会导致解析失败可以不用管)。
        data.put("backUrl", DemoBase.backUrl);            //后台通知地址，后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费撤销交易 商户通知,
        // 其他说明同消费交易的商户通知

        /***要调通交易以下字段必须修改***/
        data.put("origQryId", origQryId);              //【原始交易流水号】，原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取

        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文**/
        Map<String, String> reqData = AcpService.sign(data, DemoBase.encoding);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String reqUrl = SDKConfig.getConfig().getBackRequestUrl();//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl


        Map<String, String> rspData = AcpService.post(reqData, reqUrl, DemoBase.encoding);//发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;
        // 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/

        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, DemoBase.encoding)) {
                log.info("验证签名成功");
                String respCode = rspData.get("respCode");
                if ("00".equals(respCode)) {
                    //交易已受理(不代表交易已成功），等待接收后台通知确定交易成功，也可以主动发起 查询交易确定交易状态。
                    //TODO
                    log.info("respCode = 00");
                } else if ("03".equals(respCode) || "04".equals(respCode) || "05".equals(respCode)) {
                    //后续需发起交易状态查询交易确定交易状态。
                    //TODO
                } else {
                    //其他应答码为失败请排查原因
                    //TODO
                }
            } else {
                log.error("验证签名失败");
                //TODO 检查验证签名失败的原因
            }
        } else {
            //未返回正确的http状态
            log.error("未获取到返回报文或返回http状态码非200");
        }
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);


        renderHtml("<br />请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage + "");

    }


}
