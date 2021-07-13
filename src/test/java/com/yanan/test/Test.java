package com.yanan.test;
import java.io.File;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.yanan.framework.jdb.SqlSession;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Service;
import com.yanan.framework.plugin.autowired.enviroment.Variable;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.framework.token.Token;
import com.yanan.framework.token.annotation.Authentication;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.parameter.annotations.PathVariable;
import com.yanan.framework.webmvc.parameter.annotations.RequestParam;
import com.yanan.framework.webmvc.response.annotations.ResponseJson;
import com.yanan.utils.resource.Resource;

@Register
public class Test {
	@Service
	private Logger log;
	
	@Variable("jdb.password")
	private String test;
//	@Service
//	private SqlSession sqlSession;
	@ResponseJson
	@RequestMapping("/token/{last*}/{first*}")
	public Map<String,Object> test(Token token,@RequestParam Map<String,Object> param,
			@PathVariable("first") String first,
			@PathVariable("last") String last) {
		Resource resource = new DefaultResourceLoader().getResource("nacos:DEFAULT_GROUP/boot-jdb");
		System.out.println(test);
		log.debug("first:"+first+",last:"+last);
		log.debug("Token验证："+token.getId()+"   "+Token.getToken().getId());
		log.debug("获得参数:"+param);
		return null;//sqlSession.selectOne("testSql.query", param);
	}
	@RequestMapping("/smsCode")
	public File testAuth() {
		return new File("/Users/yanan/Desktop/5691623394514_.pic.jpg.jpg");
	}
	@RequestMapping("/html")
	public String html() {
		return "<img src='smsCode' width='100%'/>";
	}
	@RequestMapping("/RATWdowMxh.txt")
	public File wxFile() {
		return new File("/Users/yanan/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/501bd47da9f7d97c4d48b6d63d92d67d/Message/MessageTemp/c22073576953a786c298b37f75b8d769/File/RATWdowMxh.txt");
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