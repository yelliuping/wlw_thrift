package com.wlw.thrift.consts;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.wlw.thrift.util.Logger;

/**
 * 
 * @author zhiqiang.liu
 * @2016年10月24日
 *
 */

@SuppressWarnings("unused")
public class ServerProperties {
	// 以下为全局需要
	private static final Logger logger = Logger.getLogger(ServerProperties.class);

	// 测试
	public static void main(String[] args) {
		// just for test
		ServerProperties property = ServerProperties.getInstance();
		logger.debug(property.toString());
	}

	public static ServerProperties getInstance() {
		return myProperties;
	}

	//
	private static ServerProperties myProperties = null;// 全局单例变量，一开始就存在
	static {// 静态块里，只加载一次

		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(SERVER_CONFIG.SERVER_CONFIG_FILE));
			// Thread.currentThread().getContextClassLoader().getResourceAsStream(MyConstants.CONFIG_FILE);
			props.load(in);
			in.close();
		} catch (Exception e) {
			// logger.error(e.toString());
			logger.error("fail to read config file " + SERVER_CONFIG.SERVER_CONFIG_FILE);
			System.exit(-1);
		}
		// 读取值
		logger.debug("succeed to read config file " + SERVER_CONFIG.SERVER_CONFIG_FILE);
		// thrift
		String  name = props.getProperty(SERVER_CONFIG.SERVER_NAME);
		String  ip 	= props.getProperty(SERVER_CONFIG.SERVER_IP);
		int 	port 	= Integer.parseInt(props.getProperty(SERVER_CONFIG.SERVER_PORT, "10000"));
		int 	acceptThreads = Integer.parseInt(props.getProperty(SERVER_CONFIG.ACCEPT_THREADS, "1"));
		int 	selectThreads = Runtime.getRuntime().availableProcessors()
				* Integer.parseInt(props.getProperty(SERVER_CONFIG.SELECT_THREADS, "2").trim());// 2倍cpu
		// worker
		int workerThreads = Runtime.getRuntime().availableProcessors()
				* Integer.parseInt(props.getProperty(SERVER_CONFIG.WORKER_THREADS, "2").trim());// 2倍cpu
		// size
		int acceptedQueueSize = Integer.parseInt(props.getProperty(SERVER_CONFIG.QUEUE_SIZE, "10000").trim());
		// ZK
		String zkClusterAddress = props.getProperty(SERVER_CONFIG.ZK_CLUSTER_ADDRESS);
		int sessionTimeoutMs = Integer.parseInt(props.getProperty(SERVER_CONFIG.SESSION_TIMEOUT_MS));
		int connectionTimeoutMs = Integer.parseInt(props.getProperty(SERVER_CONFIG.CONNECTION_TIMEOUT_MS));
		// Project
		String group = props.getProperty(SERVER_CONFIG.GROUP);
		String version = props.getProperty(SERVER_CONFIG.VERSION);
		// Package
		String scanPackage = props.getProperty(SERVER_CONFIG.PACKAGE);
		props.clear();
		props = null;
		// 构造新的对象
		myProperties = new ServerProperties(name,ip, port, acceptThreads, selectThreads, acceptedQueueSize, workerThreads,
				zkClusterAddress, //
				sessionTimeoutMs, //
				connectionTimeoutMs, //
				scanPackage);
		logger.info("succeed to create my properties object ");
		logger.info("cpu - " + Runtime.getRuntime().availableProcessors());
	}

	// 私有属性开始//////////////////////////////////////////////////////////////////
	// thrift
	 private String ip;
	private String name;
	private int port;
	private int accpetThreads;
	private int selectorThreads;
	private int queueSize;
	private int workerThreads;
	private String zkClusterAddress;
	private int sessionTimeoutMs;
	private int connectionTimeoutMs;
	private String scanPackage;

	private ServerProperties() {// 私有方法，保证单例
	}

	private ServerProperties(String name,String ip, int port, int aThreads, int sThreads, int qSize, int wThreads, String zkAddress,
			int sTimeoutMs, int cTimeoutMs, String _package) {
		// used by netty
	    this.ip = ip;
		this.name=name;
		this.port = port;
		this.accpetThreads = aThreads;
		this.selectorThreads = sThreads;
		this.queueSize = qSize;
		this.workerThreads = wThreads;
		this.zkClusterAddress = zkAddress;
		this.sessionTimeoutMs = sTimeoutMs;
		this.connectionTimeoutMs = cTimeoutMs;
		this.scanPackage = _package;
	}

	public String getScanPackage() {
		return scanPackage;
	}

	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}

	public int getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}

	public int getPort() {
		return port;
	}

	public String getZkClusterAddress() {
		return zkClusterAddress;
	}

	public int getAccpetThreads() {
		return accpetThreads;
	}

	public int getSelectorThreads() {
		return selectorThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	// public String getIp() {
	// return ip;
	// }

	public String toString() {
		StringBuilder strBuilder = new StringBuilder("\n");
		strBuilder.append(SERVER_CONFIG.SERVER_PORT).append(": ").append(port).append("\n");
		strBuilder.append(SERVER_CONFIG.ACCEPT_THREADS).append(": ").append(accpetThreads).append("\n");
		strBuilder.append(SERVER_CONFIG.SELECT_THREADS).append(": ").append(selectorThreads).append("\n");
		strBuilder.append(SERVER_CONFIG.QUEUE_SIZE).append(": ").append(queueSize).append("\n");
		strBuilder.append(SERVER_CONFIG.WORKER_THREADS).append(": ").append(workerThreads).append("\n");
		// zk相关
		strBuilder.append(SERVER_CONFIG.ZK_CLUSTER_ADDRESS).append(": ").append(zkClusterAddress).append("\n");
		strBuilder.append(SERVER_CONFIG.SESSION_TIMEOUT_MS).append(": ").append(sessionTimeoutMs).append("\n");
		strBuilder.append(SERVER_CONFIG.CONNECTION_TIMEOUT_MS).append(": ").append(connectionTimeoutMs).append("\n");
		// project 相关
		// strBuilder.append(SERVER_CONFIG.PROJECT_NAME).append(":
		// ").append(projectName).append("\n");

		return strBuilder.toString();
	}

	public int getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public int getQueueSize() {
		return queueSize;
	}

	// 内部类
	static class SERVER_CONFIG {
		
		public static String SERVER_CONFIG_FILE = System.getProperty("serverProperties","src/main/resources/server.properties");
		
		public static String SERVER_NAME 	= "thrift.server.name";
		public static String SERVER_IP 		= "thrift.server.ip";
		public static String SERVER_PORT    = "thrift.server.port";
		
		public static String ACCEPT_THREADS = "acceptThreads";
		public static String SELECT_THREADS = "selectThreads";
		public static String WORKER_THREADS = "workThreads";
		public static String QUEUE_SIZE 	= "acceptedQueueSize";
		// thrift服务在zk的根目录
		public static String SERVER_ZK_ROOT = "thrift";
		
		
		//zk集群
		public static String ZK_CLUSTER_ADDRESS    = "zkClusterAddress";
		public static String SESSION_TIMEOUT_MS    = "sessionTimeoutMs";
		public static String CONNECTION_TIMEOUT_MS = "connectionTimeoutMs";
		// 版本
		public static String VERSION 	= "version";
		// 项目分组
		public static String GROUP 		= "group";
		// 扫描包
		public static String PACKAGE 	= "package";

	}
}
