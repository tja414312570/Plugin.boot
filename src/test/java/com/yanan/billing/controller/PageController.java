package com.yanan.billing.controller;

import com.yanan.billing.constant.Roles;
import com.yanan.billing.model.UserAccountModel;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.session.Token;
import com.yanan.framework.webmvc.session.annotation.Authentication;

@RequestMapping("/mime")
public class PageController {
	@Authentication(roles=Roles.USER)
	@RequestMapping
	public String myPage(){
		System.out.println(Token.getToken().getTokenId());
		System.out.println(Token.getToken().get(UserAccountModel.class));
		return "hello world";
	}

}