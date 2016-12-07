package com.wlw.thrift.server;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;

import com.wlw.thrift.util.Logger;

public class ServerSideExtendedTMultiplexedProcessor extends TMultiplexedProcessor {
	
	private static final Logger logger = Logger.getLogger(ServerSideExtendedTMultiplexedProcessor.class);
	@Override
	public void registerProcessor(String value, TProcessor processor) {
		// 本地保留一份,再抛给真正的注册器
		logger.info("register :" + value + " " + processor);
		
	}

}
