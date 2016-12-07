package com.wlw.thrift.zookeeper.server;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.wlw.thrift.consts.ServerProperties;
import com.wlw.thrift.util.Logger;

public class ZkManager {
	private static final Logger logger = Logger.getLogger(ZkConnectionStateListener.class);
	private static CuratorFramework client = null;
	
	public static void start() {
		try {
			ServerProperties properties = ServerProperties.getInstance();
				RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);
				client = CuratorFrameworkFactory.builder()//
						.connectString(properties.getZkClusterAddress())//
						.sessionTimeoutMs(properties.getSessionTimeoutMs())//
						.connectionTimeoutMs(properties.getConnectionTimeoutMs())//
						.retryPolicy(retryPolicy)//
						.build();
				
				logger.debug("client created :" + client);
				// 设置连接状态监听器
				client.getConnectionStateListenable().addListener(new ZkConnectionStateListener());
				client.start();
			// logger.info("zk线程启动成功...");
		} catch (Exception e) {
			logger.error(e.toString());
		}
		//
		//
		//
		// 进入睡眠阶段
		while (true) {
			try {
				Thread.currentThread().sleep(6000);
			} catch (Exception e) {
				logger.info(e.toString());
			}
		}

	}

}
