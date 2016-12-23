package com.wlw.thrift.zookeeper.client;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import com.wlw.thrift.consts.CommonConst;
import com.wlw.thrift.util.Logger;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;

public class ClientZkPathChildrenCacheListener implements PathChildrenCacheListener {
	private static final Logger logger = Logger.getLogger(ClientZkPathChildrenCacheListener.class);

	private String server;
	
	public ClientZkPathChildrenCacheListener(String server){
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
		
		List<ChildData> datas = new ArrayList<>();
		
		Type type = event.getType();
		if(event.getData()!=null){
			datas.add(event.getData());
		}
		if (event.getInitialData()!=null&&event.getInitialData().size()>0) {
			datas.addAll(event.getInitialData());
		}
		if(datas.size()==0){
			logger.info("listner:"+server+",event received, type --- " + type + " data: null");
			return;
		}
		for(ChildData data:datas){
			String path = data.getPath();
			logger.info("listner:"+server+",event received, type --- " + type + " path: "+path );
			if(!path.startsWith("/"+CommonConst.zk_server_root)){
				logger.warn("listner:"+server+",event received, path not start whith \""+CommonConst.zk_server_root+"\"");
				return;
			}
			String [] patchs = path.split("/");
			// /root/server/type/node
			if(patchs.length<5){
				logger.warn("listner:"+server+",event received, patchs.length<4");
				return;
			}
			String root=patchs[1];
			String server=patchs[2];
			String serverType=patchs[3];
			String regNode=patchs[4];
			regNode=URLDecoder.decode(regNode, "utf-8");
			// 计算-类型
			ClientZkEventType eType = null;
			if (Type.CHILD_ADDED == type||Type.INITIALIZED==type) {
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
			ClientZkEventQueue.put(zkEvent);
			logger.info("listner:"+server+",zkEvent---" + zkEvent);
		}
		
		
	}

}
