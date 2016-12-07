package com.wlw.thrift.client;

import java.lang.reflect.Field;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TMultiplexedProtocol;

import com.wlw.thrift.util.Logger;


public class TServiceClientFieldHelper {
	private static final Logger logger = Logger.getLogger(TServiceClientFieldHelper.class);
	public static final String[] NAMES = { "iprot_", "oprot_" };
	private static final Field[] FIELDS = new Field[NAMES.length];
	static {
		// 开始取反射字段
		int index = 0;
		Class<TServiceClient> clazz = TServiceClient.class;
		for (String name : NAMES) {
			try {
				Field field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				FIELDS[index++] = field;
				logger.info("use reflection to get field[" + field + "] succeed.");
			} catch (Exception e) {
				logger.error("use reflection to get field fail." + e.toString());
				System.exit(-1);
			}
		}
	}

	public static void set(TServiceClient tServiceClient, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		for (Field field : FIELDS) {
			field.set(tServiceClient, value);
		}
	}

	public static TMultiplexedProtocol get(TServiceClient tServiceClient) {
		//
		for (Field field : FIELDS) {
			try {
				TMultiplexedProtocol protocol = (TMultiplexedProtocol) field.get(tServiceClient);
				if (null != protocol) {
					return protocol;
				}
			} catch (Exception e) {
				//
			}
		}
		return null;
	}

	public static void main(String[] args) {

	}
}
