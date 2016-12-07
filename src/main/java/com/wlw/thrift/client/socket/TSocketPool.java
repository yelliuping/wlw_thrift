package com.wlw.thrift.client.socket;

import java.util.Properties;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TMultiplexedProtocol;

import com.wlw.thrift.consts.ClientProperties;


/**
 * 连接池
 * 
 * @author liuzq 2016年10月29日
 *
 */
public class TSocketPool {

	private GenericObjectPool<TMultiplexedProtocol> socketPool;

	private int refCount = 0;// 默认创建为0

	public void close() {
		if (null != socketPool) {
			socketPool.close();
		}
	}

	public int decrRefCountAndGet() {
		refCount--;
		return refCount;
	}

	public int incrRefCountAndGet() {
		refCount++;
		return refCount;

	}

	public TMultiplexedProtocol borrowObject() throws Exception {
		return socketPool.borrowObject();
	}

	public void returnObject(TMultiplexedProtocol protocol) {
		socketPool.returnObject(protocol);
	}

	private TSocketPool() {
	}

	private TSocketPool(String ip, int port) {
		// 初始化对象池配置
		Properties pro = ClientProperties.getInstance();
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		// 配置连接池参数
		poolConfig.setMinIdle(Integer.parseInt(pro.getProperty("minIdle")));
		poolConfig.setMaxTotal(Integer.parseInt(pro.getProperty("maxTotal")));
		poolConfig.setBlockWhenExhausted(Boolean.parseBoolean(pro.getProperty("blockWhenExhausted")));
		poolConfig.setMaxWaitMillis(Long.parseLong(pro.getProperty("maxWait")));
		poolConfig.setTestOnBorrow(Boolean.parseBoolean(pro.getProperty("testOnBorrow")));
		poolConfig.setTestOnCreate(Boolean.parseBoolean(pro.getProperty("testOnCreate")));
		poolConfig.setMaxIdle(Integer.parseInt(pro.getProperty("maxIdle")));
		poolConfig.setTestOnReturn(Boolean.parseBoolean(pro.getProperty("testOnReturn")));
		poolConfig.setTimeBetweenEvictionRunsMillis(Integer.parseInt(pro.getProperty("timeBetweenEvictionRunsMillis")));
		poolConfig.setMinEvictableIdleTimeMillis(Integer.parseInt(pro.getProperty("minEvictableIdleTimeMillis")));
		poolConfig.setTestWhileIdle(Boolean.parseBoolean(pro.getProperty("testWhileIdle")));
		poolConfig.setLifo(Boolean.parseBoolean(pro.getProperty("lifo")));
		// 初始化对象池
		socketPool = new GenericObjectPool<TMultiplexedProtocol>(new TSocketFactory(ip, port), poolConfig);
	}

	// 获取此单例
	public static TSocketPool getInstance(String ip, int port) {
		return new TSocketPool(ip, port);
	}
}
