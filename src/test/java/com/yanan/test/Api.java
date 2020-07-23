package com.yanan.test;

import com.yanan.frame.servlets.annotations.RequestMapping;
import com.yanan.frame.servlets.response.annotations.ResponseJson;

public class Api {
	@ResponseJson()
	@RequestMapping("/hels")
	public String test(){
		
		return "hello测试";
	}
}