package com.yanan.test.ant;

import com.yanan.frame.ant.annotations.Ant;

@Ant("provider")
public interface AntService2 {
	public String add(int a,int b);
}