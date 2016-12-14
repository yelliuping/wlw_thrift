package com.wlw.thrift.client.clientLocal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TServiceClient;

/**
 * 重写部分ThreadLocal，为TServiceClient管理提供方便
 * 
 * @author yelp
 *
 * @param <T> extends TServiceClient
 * 
 */
public class ClientThreadLocal<T extends TServiceClient> extends ThreadLocal<T> {

	private ConcurrentHashMap<Thread, ClientThreadInfo> clientMap = new ConcurrentHashMap<>();

	public ClientThreadLocal() {
		super();
	}

	public T get() {
		T t = super.get();
		return t;
	}

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

	public void remove() {
		super.remove();
		clientMap.remove(Thread.currentThread());
	}

	class ClientThreadInfo {
		private List<T> list = new ArrayList<>();
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
