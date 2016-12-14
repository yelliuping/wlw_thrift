package com.wlw.thrift.client.serviceClient;

import java.lang.reflect.Constructor;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.wlw.thrift.client.ClientSideTMultiplexedProtocol;
import com.wlw.thrift.util.Logger;

public class ServiceClientFactory<T extends TServiceClient> {
	private static final Logger logger = Logger.getLogger(ServiceClientFactory.class);
	private String ip;// 服务ip

	private int port;// 服务端口

	private String service;// 服务名称

	private Class<T> serviceClass;// 服务class

	public ServiceClientFactory(String ip, int port, String service, Class<T> serviceClass) {
		this.ip = ip;
		this.port = port;
		this.service = service;
		this.serviceClass = serviceClass;
	}

	/**
	 * 創建 TServiceClient
	 * @return
	 * @throws Exception
	 */
	public T create() throws Exception {
		if (serviceClass == null) {
			throw new NullPointerException("service class is null");
		}
		T client = null;
		try {
			TSocket transport = new TSocket(ip, port);
			transport.open();
			TMultiplexedProtocol tMultiProtocol = new ClientSideTMultiplexedProtocol(
					new TBinaryProtocol(new TFramedTransport(transport)), service);
			Constructor constructor = serviceClass.getConstructor(new Class[] { TProtocol.class });
			client = (T) constructor.newInstance(new Object[] { tMultiProtocol });
		} catch (Exception e) {
			logger.error("create ");

			throw e;
		}

		return client;

	}

	/**
	 * 是否活跃
	 * 
	 * @param t
	 * @return
	 */

	public boolean validate(T t) {
		return TProtocolCommad.isActive(t.getInputProtocol());
	}

	/**
	 * 刷新
	 * 
	 * @param t
	 * @throws Exception
	 */
	public void flush(T t) throws Exception {
		TProtocolCommad.flush(t.getInputProtocol());
	}

	/**
	 * 销毁关闭
	 * 
	 * @param t
	 * @throws Exception
	 */
	public void destroy(T t) throws Exception {
		TProtocol protocol = t.getInputProtocol();
		TProtocolCommad.close(protocol);
		if (protocol != t.getOutputProtocol()) {
			protocol = t.getOutputProtocol();
			TProtocolCommad.close(protocol);
		}
	}

}
