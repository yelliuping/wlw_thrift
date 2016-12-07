package com.wlw.thrift.client.socket;

import org.apache.thrift.transport.TSocket;

/**
 * 自己封装的socket
 * 
 * @author liuzq 2016年9月18日
 *
 */
public class MyTSocket extends TSocket {

	private boolean alive = true;

	public MyTSocket(String host, int port) {
		super(host, port);
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
