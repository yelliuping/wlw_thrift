package com.wlw.thrift.zookeeper.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import com.wlw.thrift.server.ServerManager;
import com.wlw.thrift.util.Logger;


public class ZkConnectionStateListener implements ConnectionStateListener {
	private static final Logger logger = Logger.getLogger(ZkConnectionStateListener.class);
	
	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		logger.info("zk state changed:" + newState + ",client: " + client);
		// state的这几种状态，如何区分?
		if (ConnectionState.CONNECTED == newState) {
			// Sent for the first successful connection to the server.
			// NOTE: You will only get one of these messages for any
			// CuratorFramework instance.
			// 注册所有服务
			new Thread(new ServerZkServiceRegisterRunnable(client, ServerManager.thriftConfog)).start();
		} else if (ConnectionState.SUSPENDED == newState) {
			// There has been a loss of connection. Leaders, locks, etc.
			// should suspend until the connection is re-established.
			// If the connection times-out you will receive a LOST notice
		} else if (ConnectionState.RECONNECTED == newState) {
			// A suspended or read-only connection has been re-established
			// 重新注册所有服务
			new Thread(new ServerZkServiceRegisterRunnable(client, ServerManager.thriftConfog)).start();
		} else if (ConnectionState.LOST == newState) {
			// The connection is confirmed to be lost.
			// Close any locks, leaders, etc. and attempt to re-create them.
			// NOTE: it is possible to get a RECONNECTED state after this but
			// you should still consider any locks, etc. as dirty/unstable
			
		} else if (ConnectionState.READ_ONLY == newState) {
			// The connection has gone into read-only mode.
			// This can only happen if you pass true for
			// CuratorFrameworkFactory.Builder.canBeReadOnly().
			// See the ZooKeeper doc regarding read only connections:
			// http://wiki.apache.org/hadoop/ZooKeeper/GSoCReadOnlyMode.
			// The connection will remain in read only mode until another state
			// change is sent.
		} else {
			logger.error("unknown state: " + newState);
		}
		
	}

}
