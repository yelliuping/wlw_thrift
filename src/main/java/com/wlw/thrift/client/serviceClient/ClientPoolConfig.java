package com.wlw.thrift.client.serviceClient;

import org.apache.thrift.TServiceClient;

public class ClientPoolConfig {
	
	private String ip;// 服务ip
	
	private int port;// 服务端口
	
	private String service;// 服务名称
	
	private Class<? extends TServiceClient> serviceClass;// 服务class
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public Class<? extends TServiceClient> getServiceClass() {
		return serviceClass;
	}
	public void setServiceClass(Class<? extends TServiceClient> serviceClass) {
		this.serviceClass = serviceClass;
	}

}
