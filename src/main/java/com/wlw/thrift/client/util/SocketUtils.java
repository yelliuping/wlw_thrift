package com.wlw.thrift.client.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.springframework.aop.framework.ProxyFactory;

import com.wlw.thrift.client.ClientSideTMultiplexedProtocol;
import com.wlw.thrift.client.ServiceNameFieldHelper;
import com.wlw.thrift.client.TServiceClientFieldHelper;
import com.wlw.thrift.client.socket.MyTSocket;
import com.wlw.thrift.client.socket.TFramedTransportFieldHelper;
import com.wlw.thrift.client.socket.TSocketPool;
import com.wlw.thrift.util.Logger;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SocketUtils {
	private static final Logger logger = Logger.getLogger(SocketUtils.class);
	// private static final String CLIENT = "$Client";

	// @SuppressWarnings("rawtypes")
	// private static ThreadLocal<HashMap<Class, TServiceClient>> originClients
	// = new ThreadLocal<HashMap<Class, TServiceClient>>() {
	//
	// public HashMap<Class, TServiceClient> initialValue() {
	// return new HashMap<Class, TServiceClient>();
	// }
	// };
	@SuppressWarnings("rawtypes")
	private static ThreadLocal<HashMap<Class, TServiceClient>> proxyClients = new ThreadLocal<HashMap<Class, TServiceClient>>() {

		public HashMap<Class, TServiceClient> initialValue() {
			return new HashMap<Class, TServiceClient>();
		}
	};

	@SuppressWarnings("rawtypes")
	private static ThreadLocal<HashMap<Class, TServiceClient>> targetClients = new ThreadLocal<HashMap<Class, TServiceClient>>() {

		public HashMap<Class, TServiceClient> initialValue() {
			return new HashMap<Class, TServiceClient>();
		}
	};

	// 参考:http://www.cnblogs.com/best/p/5679656.html
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TServiceClient getSyncAopObject(Class clz) throws Exception {// HelloWorldService.Client
		//
		// 1)获取线程变量,避免 GC
		TServiceClient proxyClient = proxyClients.get().get(clz);
		if (null == proxyClient) {
			ProxyFactory factory = new ProxyFactory();
			// 生成被代理的对象
			Constructor constructor = clz.getConstructor(new Class[] { TProtocol.class });
			TServiceClient targetClient = (TServiceClient) constructor.newInstance(new Object[] { null });
			factory.setTarget(targetClient);
			// 添加通知，横切逻辑
			factory.addAdvice(new MethodInterceptor() {
				@Override
				public Object invoke(MethodInvocation invoke) throws Throwable {
					// 1)获取连接对象
					TMultiplexedProtocol tMultiProtocol = getProtocol(clz);
					if (null == tMultiProtocol) {
						throw new Exception("fail to get protocol from pool...");
					}
					// logger.debug("Thread:" + Thread.currentThread().getName()
					// + " MethodInterceptor---" + this);
					// 2)赋值
					TServiceClient targetClient = targetClients.get().get(clz);
					TServiceClientFieldHelper.set(targetClient, tMultiProtocol);
					// 3)调用
					try {
						Object ret = invoke.proceed();
						return ret;
					} catch (Exception e) {
						SocketUtils.setAlive(tMultiProtocol, false, e);// 通过业务侧，我们也做了连接可用性的探测
						throw e;
					} finally {
						TServiceClientFieldHelper.set(targetClient, null);// 释放关联
						SocketUtils.returnObject(tMultiProtocol);
					}
				}
			});
			proxyClient = (TServiceClient) factory.getProxy();
			// 存到线程变量里
			targetClients.get().put(clz, targetClient);
			proxyClients.get().put(clz, proxyClient);
		}
		// 2)返回
		return proxyClient;
	}

	@SuppressWarnings("rawtypes")
	private static TMultiplexedProtocol getProtocol(Class clz) throws Exception {
		// clz-->HelloWorldService$Client
		//
		Model model = ClientSideProcessorFetcherHelper.getModelByClassName(clz.getName());
		if (null == model) {
			throw new Exception("fail to find Model[group,service,edition] for clz: " + clz);
		}
		String group = model.getGroup();
		String service = model.getService();
		String edition = model.getEdition();
		TSocketPool pool = LoadBalancerHelper.getInstance().getSocketPool(group, service, edition);// pool
		if (null == pool) {
			throw new Exception("fail to fetch socket pool...");
		}
		TMultiplexedProtocol tMultiProtocol = pool.borrowObject();// tMultiProtocol
		if (null == tMultiProtocol) {
			throw new Exception("fail to fetch a socket from pool...");
		}
		logger.info(
				"use " + TFramedTransportFieldHelper.get((TFramedTransport) tMultiProtocol.getTransport()).getSocket());
		//
		ServiceNameFieldHelper.set(tMultiProtocol, service);
		//
		ClientSideTMultiplexedProtocol extendedProtocol = (ClientSideTMultiplexedProtocol) tMultiProtocol;
		extendedProtocol.setPoolRef(pool);
		// 返回
		return tMultiProtocol;
	}

	private static void returnObject(TMultiplexedProtocol tMultiProtocol) {
		if (null == tMultiProtocol) {
			return;
		}
		//
		ClientSideTMultiplexedProtocol extendedProtocol = (ClientSideTMultiplexedProtocol) tMultiProtocol;
		TSocketPool pool = extendedProtocol.getPoolRef();
		extendedProtocol.setPoolRef(null);//
		//
		try {
			ServiceNameFieldHelper.set(tMultiProtocol, null);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		if (null != pool) {
			pool.returnObject(tMultiProtocol);
		}
	}

	private static void setAlive(TMultiplexedProtocol tMultiProtocol, boolean alive, Throwable t) {
		//
		if (null == tMultiProtocol) {
			return;
		}
		try {
			TFramedTransport transport = (TFramedTransport) tMultiProtocol.getTransport();
			MyTSocket myTSocket = (MyTSocket) TFramedTransportFieldHelper.get(transport);
			myTSocket.setAlive(alive);
			logger.error("set socket to be not alive --- " + myTSocket.getSocket() + " due to :" + t);
		} catch (Exception e0) {
			logger.error(e0.toString());
		}
	}
}