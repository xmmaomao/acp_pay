<%@ page language="java" contentType="text/html; charset=UTF-8"  import="java.text.*" import="java.util.*" 
    pageEncoding="UTF-8"%>

<form class="api-form" method="post" action="<%request.getContextPath();%>/refund/Refund" target="_blank">
<p>
<label>商户号：</label>
<input id="merId" type="text" name="merId" placeholder="" value="777290058150723" title="默认商户号仅作为联调测试使用，正式上线还请使用正式申请的商户号" required="required"/>
</p>
<p>
<label>订单发送时间：</label>
<input id="txnTime" type="text" name="txnTime" placeholder="订单发送时间" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="取北京时间，YYYYMMDDhhmmss格式" required="required"/>
</p>
<p>
<label>商户订单号：</label>
<input id="orderId" type="text" name="orderId" placeholder="商户订单号" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) %>" title="自行定义，8-32位数字字母" required="required"/>
</p>
<p>
<label>交易金额：</label>
<input id="txnAmt" type="text" name="txnAmt" placeholder="交易金额" value="" title="单位分，退货总金额要小于等于原消费" required="required"/>
</p>
<p>
<label>原交易流水号：</label>
<input id="origQryId" type="text" name="origQryId" placeholder="原交易流水号" value="" title="原交易流水号，从交易状态查询返回报文或代收的通知报文中获取 " required="required"/>
</p>
<p>
<label>&nbsp;</label>
<input type="submit" class="button" value="提交" />
<input type="button" class="showFaqBtn" value="遇到问题？" />
</p>
</form>

<div class="question">
<hr />
<h4>退货接口您可能会遇到...</h4>
<p class="faq">
<a href="https://open.unionpay.com/ajweb/help/respCode/respCodeList?respCode=2010002" target="_blank">2010002</a><br>
<a href="https://open.unionpay.com/ajweb/help/respCode/respCodeList?respCode=2040004" target="_blank">2040004</a><br>
<a href="https://open.unionpay.com/ajweb/help/respCode/respCodeList?respCode=2050004" target="_blank">2050004</a><br>
</p>
<hr />
</div>