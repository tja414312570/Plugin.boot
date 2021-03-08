package com.yanan.billing.service.billing;


import java.util.List;

import com.yanan.billing.model.BillingModel;
import com.yanan.framework.jdb.annotation.Sql;
/**
 * 用户登录服务
 * @author yanan
 *
 */
import com.yanan.framework.plugin.annotations.Service;
@Service
@Sql
public interface BillingService {
	/**
	 * 查询记录通过账户
	 * @param phone
	 * @return
	 */
	List<BillingModel> queryBillingByAccount(String phone);
	
}