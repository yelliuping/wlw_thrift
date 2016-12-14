package com.wlw.thrift.client.serviceClient;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TServiceClient;

public class ServiceClientPool<T extends TServiceClient> {
	private LinkedBlockingDeque<T> objectQeque=new LinkedBlockingDeque<T>();
	private ServiceClientFactory<T> factory;
	private AtomicInteger count = new AtomicInteger();
	
	public ServiceClientPool(ServiceClientFactory factory){
		this.factory=factory;
	}
	
	public T get() throws Exception{
		T t=objectQeque.pollFirst();
		if(t!=null){
			count.decrementAndGet();
		}
		if(t==null){
			t=factory.create();
		}
		return t;
	}
	
    public void put(T t){
    	if(!objectQeque.contains(t)){
    		objectQeque.push(t);
    		count.incrementAndGet();
    	}
    }
	
	private T create(){
		
		return null;
		
	}

}
