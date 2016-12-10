package com.wlw.thrift.entity;

import org.apache.thrift.TProcessor;



public class TProcessorInfo {
	
	private	String      serverName;
   private	String      serviceName;
   private   TProcessor processor;
  

	public String getServerName() {
	return serverName;
}

public void setServerName(String serverName) {
	this.serverName = serverName;
}

	public String getServiceName() {
		return serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public TProcessor getProcessor() {
		return processor;
	}
	
	public void setProcessor(TProcessor processor) {
		this.processor = processor;
	}

	
	
}
