package com.yanan.test.ant;

import com.yanan.framework.ant.annotations.Ant;
import com.yanan.framework.plugin.annotations.Service;

@Ant("queue")
@Service
public interface AntService1 {
	public int add(int a,int b);
}