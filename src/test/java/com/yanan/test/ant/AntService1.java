package com.yanan.test.ant;

import com.yanan.frame.ant.annotations.Ant;
import com.yanan.frame.plugin.annotations.Service;

@Ant("queue")
@Service
public interface AntService1 {
	public int add(int a,int b);
}