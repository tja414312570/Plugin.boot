package com.yanan.billing.service.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.property.Property;
import com.yanan.utils.string.StringUtil;

@Register(signlTon=true)
public class DefaultSMSService implements SMSService{
	private final static Logger log = LoggerFactory.getLogger(DefaultSMSService.class);
	private static Map<String,String> smsCodeCache = new HashMap<String,String>();
	@Property("sms.api.url")
	private String url;
	
	@Property("sms.api.account")
	private String account;
	
	@Property("sms.api.password")
	private String password;
	
	@Property("sms.api.content")
	private String content;
	
	@Property("sms.api.params")
	private String params;
	
	private String getCode(){
		String str = "";
		for(int i =0;i<6;i++){
			str +=(int)(Math.random()*10)+"";
		}
		return str;
	}
	
	public static void main(String[] args) {
		SMSService sms = PlugsFactory.getPluginsInstance(SMSService.class);
		sms.sendSMS("15536920037", "");
	}

	/**
	 * Hppt POST请求发送方法 返回值>0 为 提交成功
	 * 
	 * @param Mobile 电话号码
	 * @param Content 发送内容
	 * @param send_time 定时发送时间，为空时，为及时发送
	 * @return
	 */
	public int sendSMS(String mobile,String send_time) {
		String inputLine = "";
		String param = "";
		String send_content = "";
		String code = getCode();
		smsCodeCache.put(mobile, code);
		int value = -2;
		try {
			send_content = StringUtil.decodeBaseVar(content, code)
			.replaceAll("<br/>", " ");
		param =URLEncoder.encode(send_content
				, "GBK");
		param =StringUtil.decodeBaseVar(params,mobile,param,send_time);
		inputLine = sendPost(url, param);
		value = new Integer(inputLine).intValue();
		log.debug("mobile:"+mobile+",\r\ncontent:"+send_content+",\r\ncallback:"+ value);
		} catch (Exception e) {
			log.error("An error occurred when sending an SMS verification code\r\nurl:"
					+"\r\nparams:"+param
					+url+"\r\ncontent:"+send_content+"\r\ncallback:"+inputLine, e);
			value = -2;
		}
		return value;
	}

	
	/**
	 * 账号余额查询
	 * @param corpId 账号
	 * @param pwd 密码
	 * @return
	 */
	public String getSelSum(){
		String result="";
		String msg = "";
		String strUrl = "https://sdk2.028lk.com/sdk2/SelSum.aspx";
		String param = StringUtil.decodeBaseVar("CorpID=${}&Pwd=${}", account,password);
		try {
			result = sendPost(strUrl,param);
			if(result.equals("-1")){
				msg = "账号未注册";
			}else if(result.equals("-2")){
				msg = "其他错误";
			}else if(result.equals("-3")){
				msg = "密码错误";
			}else if(result.equals("-101")){
				msg = "调用接口频率过快(大于30s调用一次)";
			}else if(result.equals("-100")){
				msg = "IP黑名单";
			}else if(result.equals("-102")){
				msg = "账号黑名单";
			}else if(result.equals("-103")){
				msg = "IP未导白";
			}else {
				msg = String.format("获取成功！%s",result);
			}
		} catch (Exception e) {
			log.error("An error occurred when sms service getSelSum \r\nmsg:"
					+msg, e);
		}
		
		return result;
	}
	
	/**
	 * 获取禁止发送的号码
	 * @param corpId 账号
	 * @param pwd 密码
	 * @return
	 */
	public String getNotSend(){
		String result="";
		String msg = "";
		String strUrl = "https://sdk2.028lk.com/sdk2/NotSend.aspx";
		String param = StringUtil.decodeBaseVar("CorpID=${}&Pwd=${}", account,password);
		try {
			result = sendPost(strUrl,param);
			
			if(result.equals("-1")){
				msg = "账号未注册";
			}else if(result.equals("-2")){
				msg = "其他错误";
			}else if(result.equals("-3")){
				msg = "密码错误";
			}else if(result.equals("-101")){
				msg = "调用接口频率过快(大于30s调用一次)";
			}else if(result.equals("-100")){
				msg = "IP黑名单";
			}else if(result.equals("-102")){
				msg = "账号黑名单";
			}else if(result.equals("-103")){
				msg = "IP未导白";
			}else {
				msg = result;
			}
		} catch (Exception e) {
			log.error("An error occurred when sms service getNotSend \r\nmsg:"
					+msg, e);
		}
		return result;
	}
	
	
	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *      发送请求的 URL
	 * @param param
	 *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public String sendPost(String url, String param) {

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";

		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("An error occurred when sending an SMS verification code\r\nurl:"
					+url+"\r\nparams:"+param, e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				log.error("网络异常,回复消息获取失败！", ex);
			}
		}
		return result;
	}

	@Override
	public boolean valid(String mobile, String code) {
		String sc = smsCodeCache.get(mobile);
		return sc!=null&&sc.equals(code);
	}

}