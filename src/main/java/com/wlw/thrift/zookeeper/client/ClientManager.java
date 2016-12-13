package com.wlw.thrift.zookeeper.client;

import com.wlw.thrift.util.Logger;

public class ClientManager {
	private static final Logger logger = Logger.getLogger(ClientManager.class);
	public static void start() {
		
		ClientSideZkReadyListener.ready();
		

	}
}
