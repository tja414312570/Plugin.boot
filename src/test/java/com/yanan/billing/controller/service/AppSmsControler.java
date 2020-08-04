package com.yanan.billing.controller.service;

import javax.validation.constraints.Pattern;

import com.yanan.billing.model.response.BaseAppResult;
import com.yanan.billing.service.sms.SMSService;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.webmvc.annotations.PostMapping;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.parameter.annotations.RequestParam;
import com.yanan.framework.webmvc.response.annotations.ResponseJson;
/**
 * app短信验证码发送
 * @author yanan
 *
 */
@RequestMapping("/sms/code")
public class AppSmsControler {
	@Service 
	private SMSService smsService;
	/**
	 * 获取短信验证码
	 * @param phone 电话号码
	 * @param user 自动注入用户数据，非前端参数
	 * @return
	 */
	@ResponseJson
	@PostMapping
	public BaseAppResult getSMSCode(@Pattern(regexp="^1\\d{10}$",message="{\"ret\":1,\"msg\":\"请填写正确的手机号\"}")
	@RequestParam("phone") String phone){
		int code = smsService.sendSMS(phone, "");
		return code>0 ? BaseAppResult.success():BaseAppResult.failed("服务器繁忙，请稍后再试！");
	}
}