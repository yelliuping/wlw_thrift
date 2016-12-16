package com.wlw.thrift.entity;

import org.apache.thrift.TServiceClient;

public class ClientClassInfo {
	
	private String server;
	private String service;
	private Class<? extends TServiceClient>  clientClass;
	
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
	public Class<? extends TServiceClient> getClientClass() {
		return clientClass;
	}
	public void setClientClass(Class<? extends TServiceClient> clientClass) {
		this.clientClass = clientClass;
	}

	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + server.hashCode();
		result = 37 * result + service.hashCode();
		result = 37 * result + clientClass.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		ClientClassInfo obj1=(ClientClassInfo)obj;
		if(server.equals(obj1.service)&&
		   service.equals(obj1.service)&&
		   clientClass==obj1.clientClass){
			return true;
		}
	    return false;
	}
	
}
