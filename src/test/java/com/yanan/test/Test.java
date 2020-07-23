package com.yanan.test;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.yanan.frame.plugin.annotations.Register;
import com.yanan.frame.plugin.annotations.Service;
import com.yanan.frame.servlets.annotations.RequestMapping;
import com.yanan.frame.servlets.parameter.annotations.RequestParam;
import com.yanan.frame.servlets.response.annotations.ResponseJson;
import com.yanan.frame.servlets.session.Token;
import com.yanan.frame.servlets.session.annotation.Authentication;

@Register
public class Test {
	@Service
	private Logger log;
	@ResponseJson
	@RequestMapping("/token")
	public String test(@RequestParam("token") String token) {
		log.debug("Token验证："+token+"   "+Token.getToken().getTokenId());
		return token;
	}
	@Authentication(roles="root")
	@RequestMapping("/smsCode")
	public String testAuth() {
		
		return "Welcome "+Token.getToken().get("phone");
	}
	@RequestMapping("/testSign")
	public String testSign(@NotNull(message = "{\"code\":1,\"msg\":\"请填写电话号码\"}") @RequestParam("phone") String phone,
			@NotNull(message = "{\"code\":1,\"msg\":\"请填写验证码\"}") @RequestParam("sms") String sms,
			@NotNull(message = "{\"code\":1,\"msg\":\"请填写请求码\"}") @RequestParam("sid") String sid) {
		Token.getToken().addRole("root");
		System.out.println("电话:"+phone+" 验证码："+sms+"  请求码："+sid);
		Token.getToken().set("phone", phone);
		return "OK";
	}
}