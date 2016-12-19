package com.wlw.thrift.zookeeper.client;

import java.util.concurrent.TimeUnit;

import com.wlw.thrift.client.serverData.ClientData;
import com.wlw.thrift.consts.CommonConst;
import com.wlw.thrift.entity.ProtocolModel;
import com.wlw.thrift.util.Logger;
import com.wlw.thrift.util.ThriftUriUtil;

public class ClientZkEventConsumer implements Runnable {

	private static final Logger logger = Logger.getLogger(ClientZkEventConsumer.class);
	private boolean isShutDown = false;

	public void stop() {
		isShutDown = true;
	}

	@Override
	public void run() {
		while (true) {
			ClientZkEvent event=null;
			try {
				// 循环消费
				event = ClientZkEventQueue.take();
				if (null == event) {
					TimeUnit.SECONDS.sleep(1);
					continue;
				}
				logger.info("ClientSideZkEventConsumer take ClientSideZkEvent:" + event);
				ClientZkEventType type = event.getType();
				ProtocolModel protocolModel=ThriftUriUtil.uri(event.regNode);
				
				if(CommonConst.zk_server_provider.equals(event.getServerType())){
					if (ClientZkEventType.ADDED == type) {
						ClientData.addProvider(protocolModel);
					} else if (ClientZkEventType.REMOVED == type) {
						//删除在线
						ClientData.delProvider(protocolModel);
					}else{
						logger.warn("ClientSideZkEventConsumer other event info:" + event);
					}
				}
				
			} catch (Exception e) {
				logger.error("ClientSideZkEventConsumer error event info:" + event, e);
			}
			if (isShutDown) {
				break;
			}
		} 

	}

}
