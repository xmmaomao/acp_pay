<%@ page language="java" contentType="text/html; charset=UTF-8" import="java.text.*" import="java.util.*"
         pageEncoding="UTF-8" %>

<form class="api-form" method="post" action="<%request.getContextPath();%>/consume/frontConsume" target="_blank">
    <p>
        <label>商户号：</label>
        <input id="merId" type="text" name="merId" placeholder="金保证测试账号： 777290058150723" value="777290058150723"
               title="商户号码（商户号码777290058110097仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】" required="required"/>
    </p>
    <p>
        <label>交易金额：</label>
        <input id="txnAmt" type="text" name="txnAmt" placeholder="交易金额" value="1000" title="单位为分,不能带小数点 " required="required"/>
    </p>
    <p>
        <label>订单发送时间：</label>
        <input id="txnTime" type="text" name="txnTime" placeholder="订单发送时间，YYYYMMDDhhmmss格式"
               value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="取北京时间,，YYYYMMDDhhmmss格式" required="required"/>
    </p>
    <p>
        <label>商户订单号：</label>
        <input id="orderId" type="text" name="orderId" placeholder="商户订单号"
               value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="自行定义，8-32位数字字母 " required="required"/>
    </p>
    <p>
        <label>&nbsp;</label>
        <input type="submit" class="button" value="提交"/>
        <input type="button" class="showFaqBtn" value="遇到问题？"/>
    </p>
</form>

<div class="question">
    <hr/>
    <h4>消费您可能会遇到...</h4>
    <p class="faq">
        <a href="https://open.unionpay.com/ajweb/help/faq/list?id=4&level=0&from=0" target="_blank">测试卡信息</a><br>
        <a href="https://open.unionpay.com/ajweb/help/faq/list?id=24&level=0&from=0" target="_blank">http400错误</a><br>
        <a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=9100004" target="_blank">测试环境9100004</a><br>
        <a href="https://open.unionpay.com/ajweb/help/faq/list?id=40&level=0&from=0" target="_blank">正式环境9100004</a><br>
        <a href="https://open.unionpay.com//ajweb/help/respCode/respCodeList?respCode=6100030" target="_blank">6100030</a><br>
        <br>
    </p>
    <hr/>
</div>
