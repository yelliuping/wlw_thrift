package com.wlw.thrift.client.serviceClient;

import org.apache.thrift.TServiceClient;

public class ServiceClientInfo<T extends TServiceClient> {
	T serviceClient;
	ServiceClientFactory<T> factory;
	
	public ServiceClientInfo(T serviceClient, ServiceClientFactory<T> factory) {
		this.serviceClient = serviceClient;
		this.factory = factory;
	}
	public T getServiceClient() {
		return serviceClient;
	}
	public void setServiceClient(T serviceClient) {
		this.serviceClient = serviceClient;
	}
	public ServiceClientFactory<T> getFactory() {
		return factory;
	}
	public void setFactory(ServiceClientFactory<T> factory) {
		this.factory = factory;
	}
}
