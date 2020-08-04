package com.yanan.test;

import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.response.annotations.ResponseJson;

public class Api {
	@ResponseJson()
	@RequestMapping("/hels")
	public String test(){
		
		return "hello测试";
	}
}