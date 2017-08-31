package com.acp.controller.query;

import com.acp.controller.BaseController;
import com.acp.util.StringUtil;
import com.acp.util.acp.sdk.AcpService;
import com.acp.util.acp.sdk.DemoBase;
import com.acp.util.acp.sdk.SDKConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description： 查询 （支付状态查询、对账单下载）
 * <br /> Author： galsang
 */
@Slf4j
public class QueryController extends BaseController {


    public void queryIndex() {
        renderJsp("query.jsp");
    }

    public void query() {
        String merId = getPara("merId");
        String orderId = getPara("orderId");
        String txnTime = getPara("txnTime");

        // TODO 待改进
        if(StringUtil.isBlank(merId) || StringUtil.isBlank(orderId) || StringUtil.isBlank(txnTime)){
            log.error("参数格式不正确！");
            return;
        }

        Map<String, String> data = new HashMap<>();

        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", DemoBase.version);                 //版本号
        data.put("encoding", DemoBase.encoding);               //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "00");                             //交易类型 00-默认
        data.put("txnSubType", "00");                          //交易子类型  默认00
        data.put("bizType", "000201");                         //业务类型 B2C网关支付，手机wap支付

        /***商户接入参数***/
        data.put("merId", merId);                  //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("accessType", "0");                           //接入类型，商户接入固定填0，不需修改

        /***要调通交易以下字段必须修改***/
        data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
        data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/

        Map<String, String> reqData = AcpService.sign(data, DemoBase.encoding);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

        String url = SDKConfig.getConfig().getSingleQueryUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
        //这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        Map<String, String> rspData = AcpService.post(reqData, url, DemoBase.encoding);

        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, DemoBase.encoding)) {
                log.info("验证签名成功");
                if ("00".equals(rspData.get("respCode"))) {//如果查询交易成功
                    //处理被查询交易的应答码逻辑
                    String origRespCode = rspData.get("origRespCode");
                    if ("00".equals(origRespCode)) {
                        //交易成功，更新商户订单状态
                        //TODO
                    } else if ("03".equals(origRespCode) || "04".equals(origRespCode) || "05".equals(origRespCode)) {
                        //需再次发起交易状态查询交易
                        //TODO
                    } else {
                        //其他应答码为失败请排查原因
                        //TODO
                    }
                } else {//查询交易本身失败，或者未查到原交易，检查查询交易报文要素
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

        renderHtml("</br>请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage + "");
    }

    /**
     * 对账单下载
     */
    public void fileTransferIndex() {
        renderJsp("file_transfer.jsp");
    }

    public void fileTransfer() throws IOException {

        String merId = getPara("merId");
        String settleDate = getPara("settleDate");

        // TODO 待改进
        if(StringUtil.isBlank(merId) || StringUtil.isBlank(settleDate)){
            log.error("参数格式不正确！");
            return;
        }

        Map<String, String> data = new HashMap<>();

        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", DemoBase.version);               //版本号 全渠道默认值
        data.put("encoding", DemoBase.encoding);             //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        data.put("txnType", "76");                           //交易类型 76-对账文件下载
        data.put("txnSubType", "01");                        //交易子类型 01-对账文件下载
        data.put("bizType", "000000");                       //业务类型，固定

        /***商户接入参数***/
        data.put("accessType", "0");                         //接入类型，商户接入填0，不需修改
        data.put("merId", merId);
        //商户代码，请替换正式商户号测试，如使用的是自助化平台注册的777开头的商户号，该商户号没有权限测文件下载接口的，请使用测试参数里写的文件下载的商户号和日期测。如需777商户号的真实交易的对账文件，请使用自助化平台下载文件。
        data.put("settleDate", settleDate);                  //清算日期，如果使用正式商户号测试则要修改成自己想要获取对账文件的日期， 测试环境如果使用700000000000001商户号则固定填写0119
        data.put("txnTime", DemoBase.getCurrentTime());       //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("fileType", "00");                          //文件类型，一般商户填写00即可

        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/

        Map<String, String> reqData = AcpService.sign(data, DemoBase.encoding);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String url = SDKConfig.getConfig().getFileTransUrl();//获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.fileTransUrl
        Map<String, String> rspData = AcpService.post(reqData, url, DemoBase.encoding);

        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        String fileContentDispaly = "";
        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, DemoBase.encoding)) {
                log.info("验证签名成功");
                String respCode = rspData.get("respCode");
                if ("00".equals(respCode)) {
                    String outPutDirectory = "d:\\";
                    // 交易成功，解析返回报文中的fileContent并落地
                    String zipFilePath = AcpService.deCodeFileContent(rspData, outPutDirectory, DemoBase.encoding);
                    //对落地的zip文件解压缩并解析
                    List<String> fileList = DemoBase.unzip(zipFilePath, outPutDirectory);
                    //解析ZM，ZME文件
                    fileContentDispaly = "<br>获取到商户对账文件，并落地到" + outPutDirectory + ",并解压缩 <br>";
                    for (String file : fileList) {
                        if (file.indexOf("ZM_") != -1) {
                            List<Map> ZmDataList = DemoBase.parseZMFile(file);
                            fileContentDispaly = fileContentDispaly + DemoBase.getFileContentTable(ZmDataList, file);
                        } else if (file.indexOf("ZME_") != -1) {
                            DemoBase.parseZMEFile(file);
                        }
                    }
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

        // getResponse().getWriter().write("</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+fileContentDispaly);

        renderHtml("</br>请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage + fileContentDispaly);

    }


}
