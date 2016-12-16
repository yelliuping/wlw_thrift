package com.wlw.thrift.client.entity;

/**
 * 通用连接池配置
 * 
 * @author yelp
 *
 */
public class CommonPoolConfig {
	
	private int initNum;//初始化个数

	public int getInitNum() {
		return initNum;
	}

	public void setInitNum(int initNum) {
		this.initNum = initNum;
	}

}
