package com.wlw.thrift.zookeeper.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.wlw.thrift.consts.ClientConst;
import com.wlw.thrift.consts.CommonConst;
import com.wlw.thrift.util.Logger;

public class ClientZkThread implements Runnable {

	private static final Logger logger = Logger.getLogger(ClientZkThread.class);
	private static CuratorFramework client = null;
	private static ArrayList<PathChildrenCache> cacheList = new ArrayList<PathChildrenCache>();
	private Set<String> servers;

	public ClientZkThread(Set<String> servers) {
		this.servers = servers;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		// http://macrochen.iteye.com/blog/1366136/
		// http://ifeve.com/zookeeper-curato-framework/
		// http://www.codelast.com/%E5%8E%9F%E5%88%9B-zookeeper%E6%B3%A8%E5%86%8C%E8%8A%82%E7%82%B9%E7%9A%84%E6%8E%89%E7%BA%BF%E8%87%AA%E5%8A%A8%E9%87%8D%E6%96%B0%E6%B3%A8%E5%86%8C%E5%8F%8A%E6%B5%8B%E8%AF%95%E6%96%B9%E6%B3%95/
		try {
			String threadName = Thread.currentThread().getName();
			logger.info("当前运行线程: {" + threadName + "}");
			{
				RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);
				client = CuratorFrameworkFactory.builder()//
						.connectString(ClientConst.zkInfo.getAddress())
						.sessionTimeoutMs(ClientConst.zkInfo.getSessionTimeoutMs())
						.connectionTimeoutMs(ClientConst.zkInfo.getConnectionTimeoutMs())
						.retryPolicy(retryPolicy)
						// .namespace(properties.getProjectName())// 增加命名空间
						.build();
				CountDownLatch cdl = new CountDownLatch(1);
				client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
					@Override
					public void stateChanged(CuratorFramework client, ConnectionState newState) {
						if (ConnectionState.CONNECTED == newState) {// 只会接收一次
							cdl.countDown();
						}
					}
				});
				client.start();
				logger.info("client 创建完毕 :" + client);
				cdl.await();
				Set<String> array = new HashSet<>();
				for (String server : servers) {
					array.add("/" + CommonConst.zk_server_root + "/"+server+"/" + CommonConst.zk_server_provider);
				}
				logger.info("all path here: " + array);
				for (String s : array) {
					// 注册监听
					PathChildrenCache cache = new PathChildrenCache(client, s, true);
					cacheList.add(cache);
					cache.getListenable().addListener(new ClientZkPathChildrenCacheListener(s));
					cache.start(StartMode.POST_INITIALIZED_EVENT);
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
			System.exit(-1);// 有异常，必须退出
		}
		//
		//
		{
			// TODO 6秒只是一个预估数字,需要优化
			// 过6秒后，触发client端的ready为可用
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;// 是为了给下面的zk监听留出时间准备就绪
			ClientZkReadyListener.countdown();// 告知客户端，可以开始了
		}
		//
		// 进入睡眠阶段
		logger.info("zk thread succeed to start...线程启动成功...");
		while (true) {
			try {
				Thread.currentThread().sleep(6000);
			} catch (Exception e) {
				logger.error(e.toString());
			}
		}

	}

}
