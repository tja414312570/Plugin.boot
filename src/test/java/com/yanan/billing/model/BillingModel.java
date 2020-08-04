package com.yanan.billing.model;

import javax.validation.constraints.NotNull;

import com.yanan.framework.jdb.annotation.Column;
import com.yanan.framework.jdb.annotation.Tab;
import com.yanan.framework.webmvc.parameter.HTTP.DELETE;
import com.yanan.framework.webmvc.parameter.HTTP.POST;
import com.yanan.framework.webmvc.parameter.HTTP.PUT;
import com.yanan.framework.webmvc.parameter.annotations.Date;
import com.yanan.framework.webmvc.parameter.annotations.UUID;

/**
 * 统计信息数据表
 * @author yanan
 *
 */
@Tab(dataSource="yanan_Billing",name="car_billing")
public class BillingModel {
	@UUID(groups=POST.class)
	@NotNull(message="标识为空",groups={PUT.class,DELETE.class})
	@Column(Primary_Key=true,type="varchar",length=255,Annotations="信息标识")
	private String bid;
	@Column(type="varchar",length=255,Annotations="用户账户",Not_Null=true)
	private String accountId;
	@NotNull(message="车牌号为空",groups={POST.class,PUT.class,DELETE.class})
	@Column(type="varchar",length=255,Annotations="车牌号")
	private String number;
	@NotNull(message="文件标识为空",groups={POST.class})
	@Column(type="varchar",length=255,Annotations="文件标识")
	private String fileId;
	@Date(groups=POST.class)
	@Column(type="datetime",Annotations="信息创建时间")
	private String dateCreate;
	@NotNull(message="记录时间为空",groups={POST.class})
	@Column(type="datetime",Annotations="前端记录开始时间")
	private String dateStart;
	@Column(Annotations="状态")
	private int status;
	@Column(type="varchar",length=255,Annotations="备注")
	private String note;
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getDateCreate() {
		return dateCreate;
	}
	public void setDateCreate(String dateCreate) {
		this.dateCreate = dateCreate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDateStart() {
		return dateStart;
	}
	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}
	@Override
	public String toString() {
		return "BillingModel [bid=" + bid + ", accountId=" + accountId + ", number=" + number + ", fileId=" + fileId
				+ ", dateCreate=" + dateCreate + ", dateStart=" + dateStart + ", status=" + status + ", note=" + note
				+ "]";
	}
}