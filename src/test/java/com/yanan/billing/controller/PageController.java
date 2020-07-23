package com.yanan.billing.controller;

import com.yanan.billing.constant.Roles;
import com.yanan.billing.model.UserAccountModel;
import com.yanan.frame.servlets.annotations.RequestMapping;
import com.yanan.frame.servlets.session.Token;
import com.yanan.frame.servlets.session.annotation.Authentication;

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