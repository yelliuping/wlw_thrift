package com.wlw.thrift.client;

import java.lang.reflect.Field;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import com.wlw.thrift.util.Logger;

public class ServiceNameFieldHelper {
	private static final Logger logger = Logger.getLogger(ServiceNameFieldHelper.class);
	private static Field service = null;
	public static String FIELD_NAME = "SERVICE_NAME";
	static {
		// 开始取反射字段
		try {
			Class<TMultiplexedProtocol> clazz = TMultiplexedProtocol.class;
			Field field = clazz.getDeclaredField(FIELD_NAME);
			field.setAccessible(true);
			service = field;
			logger.info("use reflection to get service field succeed.");
		} catch (Exception e) {
			logger.error("use reflection to get service field fail." + e);
			System.exit(-1);
		}

	}

	public static void set(TMultiplexedProtocol tMultiProtocol, String value)
			throws IllegalArgumentException, IllegalAccessException {
		service.set(tMultiProtocol, value);
	}

	public static void main(String[] args) {

	}
}
