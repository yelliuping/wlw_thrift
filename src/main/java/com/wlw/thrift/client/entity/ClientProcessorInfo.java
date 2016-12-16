package com.wlw.thrift.client.entity;

import org.apache.thrift.TServiceClient;

public class ClientProcessorInfo {
	
	private String server;//服务器
	
	private String service;// 服务名称
	
	private Class<? extends TServiceClient> serviceClass;// 服务class
	
	public ClientProcessorInfo() {
	}
	
	public ClientProcessorInfo(String server, String service, Class<? extends TServiceClient> serviceClass) {
		this.server = server;
		this.service = service;
		this.serviceClass = serviceClass;
	}


	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
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
