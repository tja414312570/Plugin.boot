package com.yanan.billing.model;

import java.util.Date;

import javax.validation.constraints.Pattern;

import com.yanan.frame.jdb.annotation.Column;
import com.yanan.frame.jdb.annotation.Tab;
import com.yanan.frame.servlets.parameter.HTTP.POST;
import com.yanan.frame.servlets.parameter.annotations.UUID;

/**
 * 用户账户模型
 * @author yanan
 *
 */
@Tab(dataSource="yanan_Account",name="user_account")
public class UserAccountModel {
	/**
	 * 用户标示
	 */
	@UUID(groups=POST.class)
	@Column(Primary_Key=true,type="varchar",length=255)
	private String uid;
	/**
	 * 用户电话
	 */
	@Pattern(regexp="",message="请填写正确的电话号码")
	@Column(type="varchar",length=255,unique=true,Not_Null=true)
	private String phone;
	/**
	 * 用户密码
	 */
	@Column(type="varchar",length=255,Annotations="用户密码")
	private String pwd;
	/**
	 * 用户状态
	 */
	@Column(Annotations="用户状态")
	private int status;
	/**
	 * 用户创建时间
	 */
	@com.yanan.frame.servlets.parameter.annotations.Date(groups=POST.class)
	@Column(type="datetime",Annotations="用户创建时间")
	private Date createDate;
	/**
	 * 用户备注
	 */
	@Column(type="varchar",length=255,Annotations="备注")
	private String note;
	@Override
	public String toString() {
		return "UserAccountModel [uid=" + uid + ", phone=" + phone + ", pwd=" + pwd + ", status=" + status
				+ ", createDate=" + createDate + ", note=" + note + "]";
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}