package com.yanan.billing.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.yanan.billing.constant.Roles;
import com.yanan.billing.model.BillingModel;
import com.yanan.billing.model.UserAccountModel;
import com.yanan.billing.model.response.BaseAppResult;
import com.yanan.billing.model.response.BillingListAppResult;
import com.yanan.billing.service.billing.BillingService;
import com.yanan.frame.jdb.operate.Insert;
import com.yanan.frame.plugin.annotations.Service;
import com.yanan.frame.servlets.annotations.GetMapping;
import com.yanan.frame.servlets.annotations.Groups;
import com.yanan.frame.servlets.annotations.PostMapping;
import com.yanan.frame.servlets.annotations.RequestMapping;
import com.yanan.frame.servlets.parameter.HTTP.POST;
import com.yanan.frame.servlets.response.annotations.ResponseJson;
import com.yanan.frame.servlets.session.annotation.Authentication;
import com.yanan.frame.servlets.session.parameter.TokenAttribute;

@Authentication(roles=Roles.USER)
@RequestMapping("/billing")
public class BillingController {
	@Service
	private BillingService billingService;
	@ResponseJson
	@GetMapping
	public BillingListAppResult list(@NotNull(message="登录失效，请重新登录！")@TokenAttribute UserAccountModel userAccountModel){
		BillingListAppResult result = new BillingListAppResult();
		result.setPage(1);
		result.setPageCount(1);
		List<BillingModel> list = billingService.queryBillingByAccount(userAccountModel.getUid());
//		list.clear();
		result.setList(list);
		return result;
	}
	@ResponseJson
	@Groups(POST.class)
	@PostMapping
	public BaseAppResult post(BillingModel billingModel,@NotNull(message="登录失效，请重新登录！")@TokenAttribute UserAccountModel userAccountModel){
		billingModel.setAccountId(userAccountModel.getUid());
		Insert insert = new Insert(billingModel);
		return insert.insert()?BaseAppResult.success("数据添加成功",billingModel.getBid()):BaseAppResult.failed("服务器繁忙，请稍后再试！");
	}
}