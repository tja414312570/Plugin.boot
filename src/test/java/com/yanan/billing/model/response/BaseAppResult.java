package com.yanan.billing.model.response;

import java.io.Serializable;

import com.yanan.billing.constant.ResponseCode;

/**
 * 基础响应内容
 * @author yanan
 *
 */
public class BaseAppResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3998466059134579035L;
	private int code;
	private String message;
	private String Token;
	private String attach;
	@Override
	public String toString() {
		return "BaseAppResult [code=" + code + ", message=" + message + ", Token=" + Token + ", attach=" + attach + "]";
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getToken() {
		return Token;
	}
	public void setToken(String token) {
		Token = token;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	/**
	 * 错误响应
	 * @param msg
	 * @return
	 */
	public static BaseAppResult failed(String msg) {
		BaseAppResult result = failed();
		result.setMessage(msg);
		return result;
	}
	/**
	 * 错误响应
	 * @return
	 */
	public static BaseAppResult failed() {
		BaseAppResult result = new BaseAppResult();
		result.setCode(ResponseCode.Failed);
		return result;
	}
	/**
	 * 错误响应
	 * @return
	 */
	public static BaseAppResult failed(int code,String msg) {
		BaseAppResult result = new BaseAppResult();
		result.setCode(code);
		result.setMessage(msg);
		return result;
	}
	/**
	 * 错误响应
	 * @return
	 */
	public static BaseAppResult failed(int code,String msg,String attach) {
		BaseAppResult result = new BaseAppResult();
		result.setCode(code);
		result.setMessage(msg);
		result.setAttach(attach);
		return result;
	}
	/**
	 * 错误响应
	 * @return
	 */
	public static BaseAppResult failed(String msg,String attach) {
		BaseAppResult result = new BaseAppResult();
		result.setCode(ResponseCode.Failed);
		result.setMessage(msg);
		result.setAttach(attach);
		return result;
	}
	/**
	 * 成功响应
	 * @return
	 */
	public static BaseAppResult success() {
		BaseAppResult result = new BaseAppResult();
		return result;
	}
	public static BaseAppResult success(String msg) {
		BaseAppResult result =success();
		result.setMessage(msg);
		return result;
	}
	public static BaseAppResult success(String msg,String attach) {
		BaseAppResult result = success(msg);
		result.setAttach(attach);
		return result;
	}
}