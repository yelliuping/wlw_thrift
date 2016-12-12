package com.wlw.thrift.zookeeper.client;

import java.util.concurrent.CountDownLatch;

import com.wlw.thrift.client.ServiceNameFieldHelper;
import com.wlw.thrift.client.TServiceClientFieldHelper;
import com.wlw.thrift.client.socket.TFramedTransportFieldHelper;
import com.wlw.thrift.util.Logger;


public class ClientSideZkReadyListener {
	private static final Logger logger = Logger.getLogger(ClientSideZkReadyListener.class);
	private static Thread zkThread = null;
	private static Thread zkConsumerThread = null;
	static {
		// 启动就判断是否可以创建字段反射，否则没必要启动了...
		try {
			Class.forName(ServiceNameFieldHelper.class.getName());
		} catch (ClassNotFoundException e) {
			logger.error("no class ServiceNameFieldHelper found..." + e);
			System.exit(-1);
		}
		try {
			Class.forName(TFramedTransportFieldHelper.class.getName());
		} catch (ClassNotFoundException e) {
			logger.error("no class TransportFieldHelper found..." + e);
			System.exit(-1);
		}
		try {
			Class.forName(TServiceClientFieldHelper.class.getName());
		} catch (ClassNotFoundException e) {
			logger.error("no class TransportFieldHelper found..." + e);
			System.exit(-1);
		}
		// 启动时，就注册ZK相关线程
		zkThread = new Thread(new ClientSideZkThread());
		zkThread.setDaemon(true);
		zkThread.setName("Client: ZooKeeper  Register Thread...");
		zkThread.start();
		// 启动时，启动event的消费者线程
		zkConsumerThread = new Thread(new ClientSideZkEventConsumer());
		zkConsumerThread.setDaemon(true);
		zkConsumerThread.setName("Consumer for Zk event...");
		zkConsumerThread.start();
		//
		logger.info("client zookeeper register thread start...");
	}

	//
	private static CountDownLatch cdLatch = new CountDownLatch(1);

	public static void countdown() {
		cdLatch.countDown();
	}

	public static void ready() {
		while (true) {
			try {
				cdLatch.await();
				break;
			} catch (InterruptedException e) {

			}
		}
	}
}
