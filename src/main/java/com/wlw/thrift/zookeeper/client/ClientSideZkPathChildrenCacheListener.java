package com.wlw.thrift.zookeeper.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import com.wlw.thrift.util.Logger;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;

public class ClientSideZkPathChildrenCacheListener implements PathChildrenCacheListener {
	private static final Logger logger = Logger.getLogger(ClientSideZkPathChildrenCacheListener.class);

	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
		// 测试发现，至少会受到以下几种（包含但不限于)
		// INITIALIZED
		// CONNECTION_RECONNECTED---意味着不需要自己重连
		// CHILD_REMOVED
		// CHILD_ADDED
		//
		Type type = event.getType();
		ChildData data = event.getData();
		if (null == data) {
			// 不是我们需要的类型
			logger.info("event received, type --- " + type + " data: null");
			return;
		}
		logger.info("event received, type --- " + type + " data: " + new String(data.getData(), "utf-8"));
		//
		// 计算path
		String path = event.getData().getPath();// /test/HelloWorldService:0.2/PROVIDER/L0557-[192.168.56.105:10001]
		logger.info("path --- " + path);
		String[] array = path.split("/");
		String service = "/" + array[1] + "/" + array[2];
		//
		// 计算-类型
		ClientSideZkEventType eType = null;
		if (Type.CHILD_ADDED == type) {
			eType = ClientSideZkEventType.ADDED;
		} // 没有更新
		else if (Type.CHILD_REMOVED == type) {
			eType = ClientSideZkEventType.REMOVED;
		} else {
			logger.error("unknown message type: " + type);
			return;
		}
		// 构造ZkEvent
		ClientSideZkEvent zkEvent = new ClientSideZkEvent(service, eType, new String(data.getData(), "utf-8"));
		// 入队
		ClientSideZkEventQueue.put(zkEvent);
		logger.info("zkEvent---" + zkEvent);
	}

}
