package com.wlw.thrift.client.clientLocal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TServiceClient;

import com.wlw.thrift.client.entity.ServiceClientInfo;
import com.wlw.thrift.util.Logger;

/**
 * 重写部分ThreadLocal，为TServiceClient管理提供方便
 * 
 * @author yelp
 *
 * @param <T> extends TServiceClient
 * 
 */
public class ClientThreadLocal<ServiceClientInfo> extends ThreadLocal<ServiceClientInfo> {
	
	private static final Logger logger = Logger.getLogger(ClientThreadLocal.class);
	
	private ConcurrentHashMap<Thread, ClientThreadInfo> clientMap = new ConcurrentHashMap<>();

	private boolean isShutDown=false;
	
	public ClientThreadLocal() {
		super();
	}

	@Override
	public ServiceClientInfo get() {
		ServiceClientInfo t = super.get();
		return t;
	}

	@Override
	public void set(ServiceClientInfo value) {
		super.set(value);
		Thread thread = Thread.currentThread();
		ClientThreadInfo clientInfo = clientMap.get(thread);
		if (clientInfo == null) {
			clientInfo = new ClientThreadInfo();
			clientInfo.setTServiceClient(value);
			clientMap.put(thread, clientInfo);
		} else {
			ServiceClientInfo tem = clientInfo.getList().get(0);
			if (tem == null || tem != value) {
				clientInfo.setTServiceClient(value);
			}
			clientInfo.setModifyTime(System.currentTimeMillis());
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		clientMap.remove(Thread.currentThread());
	}
	
	public void shutdown(){
		isShutDown=true;
	}
	
	public void startCheckThread(){
		new Thread(new Runnable() {
			public void run() {
				while(!isShutDown){
					try {
						TimeUnit.SECONDS.sleep(1);
						HashMap<Thread,ClientThreadInfo> dieMap = new LinkedHashMap<>();
						Iterator<Map.Entry<Thread,ClientThreadInfo>> Iterator=	clientMap.entrySet().iterator();
						Map.Entry<Thread,ClientThreadInfo> entry;
						while(Iterator.hasNext()){
							entry=Iterator.next();
							if(!entry.getKey().isAlive()){
								dieMap.put(entry.getKey(), entry.getValue());
							}
						}
						Iterator=null;
						entry	=null;
						if(dieMap.size()>0){
							Iterator=dieMap.entrySet().iterator();
							while(Iterator.hasNext()){
								entry=Iterator.next();
								clientMap.remove(entry.getKey());
								//放回连接池
								
								logger.debug("ClientThreadLocal CheckThread thread die return thread:"+entry.getKey().getName());
							}
						}
					} catch (Exception e) {
						logger.error("ClientThreadLocal CheckThread running error", e);
					}
				}
			}
		}).start();
	}

	
	class ClientThreadInfo {
		private List<ServiceClientInfo> list = new ArrayList<ServiceClientInfo>();
		private long ctreateTime = System.currentTimeMillis();
		private long modifyTime;

		public void setTServiceClient(ServiceClientInfo t) {
			list.add(t);
		}

		public List<ServiceClientInfo> getList() {
			return list;
		}

		public void setList(List<ServiceClientInfo> list) {
			this.list = list;
		}

		public long getCtreateTime() {
			return ctreateTime;
		}

		public void setCtreateTime(long ctreateTime) {
			this.ctreateTime = ctreateTime;
		}

		public long getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(long modifyTime) {
			this.modifyTime = modifyTime;
		}
	}

}
