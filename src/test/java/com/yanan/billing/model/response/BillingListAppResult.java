package com.yanan.billing.model.response;

import java.util.List;

import com.yanan.billing.model.BillingModel;

public class BillingListAppResult extends PageAppResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8729791545511216018L;
	public List<BillingModel> list;
	public List<BillingModel> getList() {
		return list;
	}
	public void setList(List<BillingModel> list) {
		this.list = list;
	}
}