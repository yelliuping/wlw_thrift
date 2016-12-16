package com.wlw.thrift.zookeeper.client;

import java.net.URLDecoder;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import com.wlw.thrift.consts.CommonConst;
import com.wlw.thrift.util.Logger;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;

public class ClientSideZkPathChildrenCacheListener implements PathChildrenCacheListener {
	private static final Logger logger = Logger.getLogger(ClientSideZkPathChildrenCacheListener.class);

	private String server;
	
	public ClientSideZkPathChildrenCacheListener(String server){
	this.server=server;
	}
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
			logger.info("listner:"+server+",event received, type --- " + type + " data: null");
			return;
		}
		String path = event.getData().getPath();
		
		logger.info("listner:"+server+",event received, type --- " + type + " path: "+path );
		
		if(!path.startsWith(CommonConst.zk_server_root)){
			logger.warn("listner:"+server+",event received, path not start whith \""+CommonConst.zk_server_root+"\"");
		    return;
		}
		String [] patchs = path.split("/");
		// /root/server/type/node
		if(patchs.length<4){
			logger.warn("listner:"+server+",event received, patchs.length<4");
		    return;
		}
		String root=patchs[0];
		String server=patchs[1];
		String serverType=patchs[2];
		String regNode=patchs[4];
		regNode=URLDecoder.decode(regNode, "utf-8");
		// 计算-类型
		ClientZkEventType eType = null;
		if (Type.CHILD_ADDED == type) {
			eType = ClientZkEventType.ADDED;
		}else if (Type.CHILD_REMOVED == type) {
			eType = ClientZkEventType.REMOVED;
		}else if (Type.CHILD_UPDATED == type) {
			eType = ClientZkEventType.UPDATED;
		}else {
			logger.error("listner:"+server+",unknown message type: " + type);
			return;
		}
		// 构造ZkEvent
		ClientZkEvent zkEvent = new ClientZkEvent(eType, root,server,serverType,regNode);
		ClientSideZkEventQueue.put(zkEvent);
		logger.info("listner:"+server+",zkEvent---" + zkEvent);
	}

}
