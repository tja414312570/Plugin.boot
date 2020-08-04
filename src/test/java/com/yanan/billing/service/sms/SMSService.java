package com.yanan.billing.service.sms;
import com.yanan.framework.plugin.annotations.Service;

@Service
public interface SMSService {

	/**
	 *发送短信
	 * @param Mobile
	 *            手机号码
	 * @param Content
	 *            发送内容
	 * @param send_time
	 *            定时发送的时间；可以为空，为空时为及时发送
	 * @return 返回值
	 */
	public int sendSMS(String mobile,String send_time);

	/**
	 * 账号余额查询
	 * @param corpId 账号
	 * @param pwd 密码
	 * @return
	 */
	public String getSelSum();
	
	/**
	 * 获取禁止发送的号码
	 * @param corpId 账号
	 * @param pwd 密码
	 * @return
	 */
	public String getNotSend();

	public boolean valid(String mobile, String code);
}