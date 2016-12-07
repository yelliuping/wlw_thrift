package com.wlw.thrift.entity;

import org.apache.thrift.TProcessor;



public class TProcessorInfo {
	
   private	String      serviceName;
   private   TProcessor processor;
  
   public TProcessorInfo(String serviceName, TProcessor processor) {
		this.serviceName = serviceName;
		this.processor = processor;
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
