package com.wlw.thrift.consts;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.wlw.thrift.util.Logger;


public class ClientProperties {
	// logger
	private static final Logger logger = Logger.getLogger(ClientProperties.class);

	// 私有方法，保证单例
	private ClientProperties() {
	}

	//
	private static Properties myProperties = null;// 全局单例变量，一开始就存在
	static {// 静态块里，只加载一次
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(CLIENT_CONFIG.CLIENT_CONFIG_FILE));
			// Thread.currentThread().getContextClassLoader().getResourceAsStream(MyConstants.CONFIG_FILE);
			props.load(in);
			in.close();
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("fail to read config file " + CLIENT_CONFIG.CLIENT_CONFIG_FILE);
			System.exit(-1);
		}
		// 赋值
		logger.debug("succeed to read config file " + CLIENT_CONFIG.CLIENT_CONFIG_FILE);
		myProperties = props;
		props = null;
		logger.info("succeed to create my client properties object ");
		// 结束
	}

	// 获取单例
	public static Properties getInstance() {
		return myProperties;
	}

	// 内部类
	static class CLIENT_CONFIG {
		public static String CLIENT_CONFIG_FILE = System.getProperty("clientProperties",
				"src/main/resources/client.properties");
	}
}
