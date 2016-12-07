package com.wlw.thrift.client.socket;

import java.lang.reflect.Field;
import org.apache.thrift.transport.TFramedTransport;

import com.wlw.thrift.util.Logger;


public class TFramedTransportFieldHelper {
	private static final Logger logger = Logger.getLogger(TFramedTransportFieldHelper.class);
	private static Field transport = null;
	public static String FIELD_NAME = "transport_";
	static {
		// 开始取反射字段
		try {
			Class<TFramedTransport> clazz = TFramedTransport.class;
			Field field = clazz.getDeclaredField(FIELD_NAME);
			field.setAccessible(true);
			transport = field;
			logger.info("use reflection to get transport_ field succeed.");
		} catch (Exception e) {
			logger.error("use reflection to get transport_ field fail." + e);
			System.exit(-1);
		}
	}

	public static MyTSocket get(TFramedTransport framedTransport)
			throws IllegalArgumentException, IllegalAccessException {
		return (MyTSocket) transport.get(framedTransport);
	}

	public static void main(String[] args) {

	}
}
