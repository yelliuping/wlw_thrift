package com.wlw.thrift.zookeeper.client;

import com.wlw.thrift.util.Logger;

public class ClientSideZkEventConsumer implements Runnable {

	private static final Logger logger = Logger.getLogger(ClientSideZkEventConsumer.class);

	@Override
	public void run() {
		while (true) {
			try {
				// 循环消费
				ClientSideZkEvent event = ClientSideZkEventQueue.take();
				if (null == event) {// 实际上不会发生，因为take函数做了判断
					Thread.sleep(1000);
					continue;
				}
				// 拿到了event，有效，怎么处理?
				ClientSideZkEventType type = event.getType();
				String serviceWithEdition = event.getService();
				String data = event.getData();
				logger.info("------------------------------------------");
				logger.info("type: " + type);
				logger.info("service:" + serviceWithEdition);
				logger.info("data:" + data);
				logger.info("------------------------------------------");
				// 交给LoadBalancer处理
				if (ClientSideZkEventType.ADDED == type) {
				} else if (ClientSideZkEventType.REMOVED == type) {
				}
			} catch (Exception e) {
				logger.error("ClientSideZkEventConsumer error",e);
			}
		} // while结束

	}

}
