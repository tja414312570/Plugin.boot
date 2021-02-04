package com.yanan.test;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.yanan.framework.boot.utils.Log;
import com.yanan.framework.jdb.SqlSession;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.token.Token;
import com.yanan.framework.token.annotation.Authentication;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.parameter.annotations.RequestParam;
import com.yanan.framework.webmvc.response.annotations.ResponseJson;

@Register
public class Test {
	@Log
	private Logger log;
	
	@Service
	private SqlSession sqlSession;
	@ResponseJson
	@RequestMapping("/token")
	public Map<String,Object> test(Token token,@RequestParam Map<String,Object> param) {
		log.debug("Token验证："+token.getId()+"   "+Token.getToken().getId());
		log.debug("获得参数:"+param);
		return sqlSession.selectOne("testSql.query", param);
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