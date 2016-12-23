package com.wlw.thrift.consts;

import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import com.wlw.thrift.entity.ThriftConfigInfo;
import com.wlw.thrift.entity.ZkConfigInfo;
import com.wlw.thrift.util.Logger;

/**
 * 服务端常量
 * 
 * @author yelp
 *
 */
public class ClientConst {

	private static final Logger logger = Logger.getLogger(ClientConst.class);

	public static LinkedHashMap<String, ThriftConfigInfo> serversMap = new LinkedHashMap<>();
	public static ZkConfigInfo zkInfo = null;

	public static void init() {// 静态块里，只加载一次
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(KEYS.config_name);
			String zookeeperAddress = bundle.getString(KEYS.zookeeper_address);
			int zookeeperSessionTimeoutMs = Integer.parseInt(bundle.getString(KEYS.zookeeper_sessionTimeoutMs));
			int zookeeperConnectionTimeoutMs = Integer.parseInt(bundle.getString(KEYS.zookeeper_connectionTimeoutMs));
			zkInfo = new ZkConfigInfo(zookeeperAddress, zookeeperSessionTimeoutMs, zookeeperConnectionTimeoutMs);
			logger.info("ClientConst get zookeeper properties from config_name=" + KEYS.config_name + ",zookeeperInfo:"
					+ zkInfo.toString());
		} catch (Exception e) {
			serversMap.clear();
			logger.error("ClientConst ResourceBundle.getBundle error config_name=" + KEYS.config_name, e);
		}
	}

	public static class KEYS {
		public static String config_name = "thrift-client";
		public static String zookeeper_address = "zookeeper.address";
		public static String zookeeper_sessionTimeoutMs = "zookeeper.sessionTimeoutMs";
		public static String zookeeper_connectionTimeoutMs = "zookeeper.connectionTimeoutMs";
	}
}
