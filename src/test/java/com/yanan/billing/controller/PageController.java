package com.yanan.billing.controller;

import com.yanan.billing.constant.Roles;
import com.yanan.billing.model.UserAccountModel;
import com.yanan.framework.token.Token;
import com.yanan.framework.token.annotation.Authentication;
import com.yanan.framework.webmvc.annotations.RequestMapping;

@RequestMapping("/mime")
public class PageController {
	@Authentication(roles=Roles.USER)
	@RequestMapping
	public String myPage(){
		System.out.println(Token.getToken().getId());
		System.out.println(Token.getToken().get(UserAccountModel.class));
		return "hello world";
	}

}