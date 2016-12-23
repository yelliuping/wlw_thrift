package com.wlw.thrift.entity;

public class ZkConfigInfo {
	
	private String address;
	private int    sessionTimeoutMs;
	private int    connectionTimeoutMs;
	
	public ZkConfigInfo(){}
	public ZkConfigInfo(String address, int sessionTimeoutMs, int connectionTimeoutMs) {
		this.address = address;
		this.sessionTimeoutMs = sessionTimeoutMs;
		this.connectionTimeoutMs = connectionTimeoutMs;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}
	public void setSessionTimeoutMs(int sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
	}
	public int getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}
	public void setConnectionTimeoutMs(int connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}
	@Override
	public String toString() {
		return "ZkConfigInfo [address=" + address + ", sessionTimeoutMs=" + sessionTimeoutMs + ", connectionTimeoutMs="
				+ connectionTimeoutMs + "]";
	}
	
	
}
