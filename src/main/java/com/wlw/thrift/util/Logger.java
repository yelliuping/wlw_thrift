package com.wlw.thrift.util;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */
public class Logger {// log4j
	private org.apache.log4j.Logger logger;

	private Logger(@SuppressWarnings("rawtypes") Class clazz) {
		logger = org.apache.log4j.LogManager.getLogger(clazz);
	}

	public void debug(String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug(msg);
		}
	}

	public void info(String msg) {
		if (logger.isInfoEnabled()) {
			logger.info(msg);
		}
	}

	public void warn(String msg) {
		logger.warn(msg);
	}
	
	public void warn(String msg,Throwable e) {
		logger.warn(msg,e);
	}
	
	public void error(String msg) {
		logger.error(msg);
	}
	
	public void error(String msg,Throwable e) {
		logger.error(msg,e);
	}

	public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
		return new Logger(clazz);
	}
}
