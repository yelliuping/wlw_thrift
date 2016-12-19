package com.wlw.thrift.zookeeper.client;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.wlw.thrift.util.Logger;


public class ClientZkReadyListener {
	private static final Logger logger = Logger.getLogger(ClientZkReadyListener.class);
	private static Thread zkThread = null;
	private static Thread zkConsumerThread = null;

	//
	private static CountDownLatch cdLatch = new CountDownLatch(1);

	public static void countdown() {
		cdLatch.countDown();
	}

	public static void ready(Set<String> servers) {
		while (true) {
			try {
				// 启动时，就注册ZK相关线程
				zkThread = new Thread(new ClientZkThread(servers));
				zkThread.setDaemon(true);
				zkThread.setName("Client: ZooKeeper  Register Thread...");
				zkThread.start();
				// 启动时，启动event的消费者线程
				zkConsumerThread = new Thread(new ClientZkEventConsumer());
				zkConsumerThread.setDaemon(true);
				zkConsumerThread.setName("Consumer for Zk event...");
				zkConsumerThread.start();
				//
				logger.info("client zookeeper register thread start...");
				cdLatch.await();
				break;
			} catch (InterruptedException e) {

			}
		}
	}
}
