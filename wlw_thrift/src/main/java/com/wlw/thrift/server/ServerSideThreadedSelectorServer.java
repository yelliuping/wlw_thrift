package com.wlw.thrift.server;

import org.apache.thrift.server.TThreadedSelectorServer;

import com.wlw.thrift.util.Logger;


public class ServerSideThreadedSelectorServer extends TThreadedSelectorServer {

	private static final Logger logger = Logger.getLogger(ServerSideThreadedSelectorServer.class);

	public ServerSideThreadedSelectorServer(Args args) {
		super(args);
	}

	@Override
	protected void setServing(boolean serving) {
		// 先调用父类的方法
		super.setServing(serving);
		// 这个函数会被调用2次，一次为true,一次为false
		// 只有为true,才会需要启动注册线程，false就不需要了
		if (false == serving) {
			return;
		}
		logger.info("setServing[" + serving + "] 行为被感知...");
		logger.info("---------------------------------------------------------------");
		// 运行到这里，说明服务器确实事实启动成功，端口也处于监听状态，此时才可以注册ZooKeeper
		// 否则，会发生注册机器被客户端发现，客户端却连接失败的情况
		/*thread = new Thread(new ServerSideZkThread());
		thread.setDaemon(true);
		thread.setName("ZooKeeper Register Thread...");
		thread.start();*/
		logger.info("zookeeper register thread start...");
	}

}
