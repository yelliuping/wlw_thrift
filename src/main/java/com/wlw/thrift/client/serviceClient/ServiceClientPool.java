package com.wlw.thrift.client.serviceClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TServiceClient;

import com.wlw.thrift.util.Logger;

/**
 * ServiceClient 连接池
 * 
 * @author yelp
 *
 * @param <T>
 */
public class ServiceClientPool<T extends TServiceClient> {
	private static final Logger logger = Logger.getLogger(ServiceClientPool.class);
	
	private LinkedBlockingDeque<ServiceClientInfo<T>> objectQeque=new LinkedBlockingDeque<ServiceClientInfo<T>>();
	private List<ServiceClientFactory<T>> factorys=new CopyOnWriteArrayList<>();
	private AtomicInteger count = new AtomicInteger();
	private int index=0;
	public ServiceClientPool(){
	}
	public ServiceClientPool(ServiceClientFactory<T> factory){
		factorys.add(factory);
	}
	
	public ServiceClientInfo<T> get() throws Exception{
		ServiceClientInfo<T> clientInfo=objectQeque.pollFirst();
		if(clientInfo!=null){
			count.decrementAndGet();
		}
		if(clientInfo==null){
			if(factorys.size()==0){
				throw new NullPointerException("factorys is empty");
			}
			int temIndex=index++;
			if(temIndex>=factorys.size()){
				temIndex=index=0;
			}
			ServiceClientFactory factory=factorys.get(temIndex);
			clientInfo=factory.create();
		}
		return clientInfo;
	}
	
    public LinkedBlockingDeque<ServiceClientInfo<T>> getObjectQeque() {
		return objectQeque;
	}

	public void setObjectQeque(LinkedBlockingDeque<ServiceClientInfo<T>> objectQeque) {
		this.objectQeque = objectQeque;
	}

	public List<ServiceClientFactory<T>> getFactorys() {
		return factorys;
	}

	public void addFactory(ServiceClientFactory<T> factory) {
		this.factorys.add(factory);
	}

	public void put(ServiceClientInfo<T> t){
    	if(!objectQeque.contains(t)){
    		objectQeque.push(t);
    		count.incrementAndGet();
    	}
    }
	
	public void add() throws Exception{
		if(factorys.size()==0){
			throw new NullPointerException("factorys is empty");
		}
		int temIndex=index++;
		if(temIndex>=factorys.size()){
			temIndex=index=0;
		}
		ServiceClientInfo<T> clientInfo =factorys.get(temIndex).create();
		objectQeque.push(clientInfo);
		count.incrementAndGet();
	}

}
