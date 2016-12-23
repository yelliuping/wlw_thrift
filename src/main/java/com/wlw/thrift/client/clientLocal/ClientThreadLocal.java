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
import com.wlw.thrift.client.serviceClient.MutiServiceClientPool;
import com.wlw.thrift.util.Logger;

/**
 * 重写部分ThreadLocal，为TServiceClient管理提供方便
 * 
 * @author yelp
 *
 * @param <T>
 *            extends TServiceClient
 * 
 */
public class ClientThreadLocal<T> extends ThreadLocal<T> {

	private static final Logger logger = Logger.getLogger(ClientThreadLocal.class);

	private final ConcurrentHashMap<Thread, ClientThreadInfo> clientMap = new ConcurrentHashMap<>();

	private final MutiServiceClientPool<? extends TServiceClient> mutiPool;

	private boolean isShutDown = false;

	public ClientThreadLocal(MutiServiceClientPool<? extends TServiceClient> mutiPool) {
		super();
		this.mutiPool = mutiPool;
	}

	@Override
	public T get() {
		return super.get();
	}

	@Override
	public void set(T value) {
		super.set(value);
		Thread thread = Thread.currentThread();
		ClientThreadInfo clientInfo = clientMap.get(thread);
		if (clientInfo == null) {
			clientInfo = new ClientThreadInfo();
			clientInfo.setTServiceClient(value);
			clientMap.put(thread, clientInfo);
		} else {
			T tem = clientInfo.getList().get(0);
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

	public void shutdown() {
		isShutDown = true;
	}

	public void startThreadCheck() {
		new Thread(new Runnable() {
			public void run() {
				while (!isShutDown) {
					try {
						TimeUnit.SECONDS.sleep(1);
						HashMap<Thread, ClientThreadInfo> dieMap = new LinkedHashMap<>();
						Iterator<Map.Entry<Thread, ClientThreadInfo>> Iterator = clientMap.entrySet().iterator();
						Map.Entry<Thread, ClientThreadInfo> entry;
						while (Iterator.hasNext()) {
							entry = Iterator.next();
							if (!entry.getKey().isAlive()) {
								dieMap.put(entry.getKey(), entry.getValue());
							}
						}
						Iterator = null;
						entry = null;
						if (dieMap.size() > 0) {
							Iterator = dieMap.entrySet().iterator();
							while (Iterator.hasNext()) {
								entry = Iterator.next();
								clientMap.remove(entry.getKey());
								logger.info("ClientThreadLocal CheckThread thread die return thread:"
										+ entry.getKey().getName());
								// 放回连接池
								mutiPool.returnClientInfo((List<ServiceClientInfo>) entry.getValue().list);
							}
						}
					} catch (Exception e) {
						logger.error("ClientThreadLocal CheckThread running error", e);
						if (isShutDown) {

						}
					}
				}
			}
		}).start();
	}

	class ClientThreadInfo {
		private List<T> list = new ArrayList<T>();
		private long ctreateTime = System.currentTimeMillis();
		private long modifyTime;

		public void setTServiceClient(T t) {
			list.add(t);
		}

		public List<T> getList() {
			return list;
		}

		public void setList(List<T> list) {
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
