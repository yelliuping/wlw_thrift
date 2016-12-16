package com.wlw.thrift.client.serviceClient;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.thrift.TServiceClient;

import com.wlw.thrift.client.entity.CommonPoolConfig;
import com.wlw.thrift.client.entity.ServiceClientInfo;
import com.wlw.thrift.client.serverData.ClientData;
import com.wlw.thrift.entity.ClientClassInfo;
import com.wlw.thrift.entity.ProtocolModel;
import com.wlw.thrift.util.Logger;

/**
 * 多个ServiceClient连接池管理
 * 
 * @author yelp
 *
 */
public class MutiServiceClientPool<T extends TServiceClient> {
	
	private static final Logger logger = Logger.getLogger(MutiServiceClientPool.class);
	
	private CommonPoolConfig commonPoolConfig;
	
	private  ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private  HashMap<String/*server_service */, ServiceClientPool<T>/*pool*/> poolMap = new HashMap<>();

	public MutiServiceClientPool(CommonPoolConfig commonPoolConfig) {
		this.commonPoolConfig = commonPoolConfig;
	}
	
	public boolean add(ProtocolModel model) {
		logger.info("MutiServiceClientPool add start,model:" + model);
		boolean addResult = false;
		lock.writeLock().lock();
		try {
			if (model.getServer() == null || model.getServer().length() == 0) {
				logger.error("MutiServiceClientPool add server is null");
				return addResult;
			}
			Set<ClientClassInfo> clientClasseInfo = ClientData.getClientClasseInfo(model.getServer());
			if (clientClasseInfo == null || clientClasseInfo.size() == 0) {
				logger.error("MutiServiceClientPool add clientClasses is empty");
				return addResult;
			}
			String key;
			ServiceClientPool<T> pool;
			 ServiceClientFactory factory;
			for (ClientClassInfo cl : clientClasseInfo) {
				 key=model.getServer()+"_"+model.getServices();
				 pool = poolMap.get(key);
				 factory= new ServiceClientFactory<>(model.getHost(),
							model.getPort(), model.getServer(), cl.getService(), cl.getClientClass());
				 if (pool == null) {
				    pool = new ServiceClientPool<>(factory);
					poolMap.put(key, pool);
				 }else{
					 pool.addFactory(factory); 
				 }
				 logger.info("init pool success server:" + model.getServer() + ",service:" + cl.getService()
					+ ",class:" + cl.getClientClass().getName());
			}
			addResult = true;
		} catch (Exception e) {
			logger.error("MutiServiceClientPool add error,model:" + model, e);
		} finally {
			lock.writeLock().unlock();
		}
		return addResult;
	}
	
	public ServiceClientInfo<T> getClientInfo(String server,String service){
		ServiceClientInfo<T> clientInfo=null;
		
		lock.readLock().lock();
		try {
			String key=server+"_"+service;
			ServiceClientPool<T> pool=poolMap.get(key);
			if(pool==null){
				logger.error("MutiServiceClientPool getClientInfo error,pool is null");
				return clientInfo;
			}
			clientInfo=pool.get();
			logger.debug("MutiServiceClientPool getClientInfo success,key:"+key);
		} catch (Exception e) {
			logger.error("MutiServiceClientPool getClientInfo error,server:"+server+",service:"+service, e);
		}finally{
			lock.readLock().unlock();
		}
		return clientInfo;
	}
	
	public void returnClientInfo(ServiceClientInfo<T> clientInfo){
		lock.writeLock().lock();
		ServiceClientFactory<T> factory=null;
		try {
			 factory=clientInfo.getFactory();
			String key=factory.getServer()+"_"+factory.getService();
			ServiceClientPool<T> pool=poolMap.get(key);
			if(pool==null){
				logger.error("MutiServiceClientPool ServiceClientInfo error,pool is null");
				return;
			}
			pool.put(clientInfo);
			logger.debug("MutiServiceClientPool returnClientInfo success,key:"+key);
		} catch (Exception e) {
			logger.error("MutiServiceClientPool returnClientInfo error,clientInfo:"+factory, e);
		}finally{
			lock.writeLock().unlock();
		}
	}
	
	
	
	
	

}
