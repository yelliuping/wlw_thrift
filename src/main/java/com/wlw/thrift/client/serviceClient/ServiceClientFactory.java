package com.wlw.thrift.client.serviceClient;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

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

	private String server; //服务器名称
	
	private String service;// 服务名称

	private Class<T> serviceClass;// 服务class

	public ServiceClientFactory(String ip, int port, String server, String service, Class<T> serviceClass) {
		this.ip = ip;
		this.port = port;
		this.server=server;
		this.service = service;
		this.serviceClass = serviceClass;
	}

	/**
	 * 創建 TServiceClient
	 * @return
	 * @throws Exception
	 */
	public ServiceClientInfo<T> create() throws Exception {
		if (serviceClass == null) {
			throw new NullPointerException("service class is null");
		}
		ServiceClientInfo<T> clientInfo= null;
		try {
			TSocket transport = new TSocket(ip, port);
			transport.open();
			TMultiplexedProtocol tMultiProtocol = new ClientSideTMultiplexedProtocol(
					new TBinaryProtocol(new TFramedTransport(transport)), service);
			Constructor constructor = serviceClass.getConstructor(new Class[] { TProtocol.class });
			T client = (T) constructor.newInstance(new Object[] { tMultiProtocol });
			clientInfo=new ServiceClientInfo<T>(client, this);
		} catch (Exception e) {
			logger.error("create ServiceClientInfo error",e);

			throw e;
		}
		return clientInfo;
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
	 * 销毁关闭全部
	 * 
	 * @param objectQeque
	 * @throws Exception
	 */
	public void destroy(LinkedBlockingDeque<ServiceClientInfo<T>> objectQeque) throws Exception {
		if(objectQeque!=null&&objectQeque.size()>0){
			Iterator<ServiceClientInfo<T>> itarator=objectQeque.iterator();
			while(itarator.hasNext()){
				destroy(itarator.next());
			}
		}
	}

	/**
	 * 销毁关闭
	 * 
	 * @param t
	 * @throws Exception
	 */
	public void destroy(ServiceClientInfo<T> t) throws Exception {
		TProtocol protocol = t.getServiceClient().getInputProtocol();
		TProtocolCommad.close(protocol);
		if (protocol != t.getServiceClient().getOutputProtocol()) {
			protocol = t.getServiceClient().getOutputProtocol();
			TProtocolCommad.close(protocol);
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Class<T> getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(Class<T> serviceClass) {
		this.serviceClass = serviceClass;
	}

	@Override
	public String toString() {
		return "ServiceClientFactory [ip=" + ip + ", port=" + port + ", server=" + server + ", service=" + service
				+ ", serviceClass=" + serviceClass.getName() + "]";
	}
	
	
}
