package com.yanan.billing.model.response;

public class PageAppResult extends BaseAppResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = -437130304787654272L;
	//页面数量
	private int pageCount;
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	//页面号
	private int page;
	//页面大小
	private int pageSize;
}