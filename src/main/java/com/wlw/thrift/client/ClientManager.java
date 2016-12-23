package com.wlw.thrift.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TServiceClient;

import com.wlw.thrift.annotation.Processor;
import com.wlw.thrift.client.clientLocal.ClientThreadLocal;
import com.wlw.thrift.client.entity.ClientProcessorInfo;
import com.wlw.thrift.client.entity.CommonPoolConfig;
import com.wlw.thrift.client.entity.ServiceClientInfo;
import com.wlw.thrift.client.serviceClient.MutiServiceClientPool;
import com.wlw.thrift.consts.ClientConst;
import com.wlw.thrift.util.Logger;
import com.wlw.thrift.zookeeper.client.ClientZkReadyListener;

public class ClientManager {
	private static final Logger logger = Logger.getLogger(ClientManager.class);

	/**
	 *TServiceClient 客户端服务缓存
	 */
	private static ClientThreadLocal<ServiceClientInfo<? extends TServiceClient>> local;
	/**
	 * 多TServiceClient 客户端服务连接池
	 */
	private static MutiServiceClientPool<? extends TServiceClient> mutiPool;
	
	/**
	 * 客户端初始化信息缓存
	 */
	private static Map<String, ClientProcessorInfo> processorInfoMap = new HashMap<String, ClientProcessorInfo>();

	private static boolean isStart=false;
	/**
	 * 获取客户端服务
	 * 
	 * @param clas
	 * @return
	 */
	public static ServiceClientInfo getServiceClientInfo(Class<? extends TServiceClient> clas){
		ServiceClientInfo client=local.get();
		if(client==null){
			ClientProcessorInfo processorInfo=processorInfoMap.get(clas.getName());
			client=mutiPool.getClientInfo(processorInfo.getServer(), processorInfo.getService());
			if(client!=null){
				local.set(client);
			}
		}
		return client;
		
	}
	
	/**
	 * 初始化客户端服务
	 * 
	 * @param clients
	 */
	public static synchronized void initClients(List<Class<? extends TServiceClient>> clients) {
		if(isStart){
			logger.error("ClientManager initClients already start....");
			return;
		}
		isStart=true;
		ClientConst.init();
		// 先获取第一个配置
		try {
			CommonPoolConfig poolconfig = new CommonPoolConfig();
			List<ClientProcessorInfo> clientInfos = getClientProcessorInfo(clients);
			Set<String> servers = new HashSet<>();
			ClientProcessorInfo info;
			for (int i=0;i<clientInfos.size();i++) {
				info=clientInfos.get(i);
				servers.add(info.getServer());
				processorInfoMap.put(info.getServiceClass().getName(), info);
				logger.info("ClientManager initClient "+(i+1)+" server:"+info.getServer()+",service:"+info.getService());
			}
			ClientZkReadyListener.ready(servers);
			
			mutiPool = new MutiServiceClientPool<>(poolconfig);
			local = new ClientThreadLocal<>(mutiPool);
			mutiPool.addByClients(clientInfos);
			
			//启动线程检测
			local.startThreadCheck();
			mutiPool.startThreadCheck();
		} catch (Exception e) {
			logger.error("ClientManager start error", e);
		}
	}
	
	public static void shownDown(){
		try {
			local.shutdown();
			mutiPool.shutdown();
		} catch (Exception e) {
			logger.error("ClientManager shownDown error", e);
		}
	}

	/**
	 * 处理客户端服务信息
	 * 
	 * @param clients
	 * @return
	 * @throws Exception
	 */
	private static List<ClientProcessorInfo> getClientProcessorInfo(List<Class<? extends TServiceClient>> clients) throws Exception {
		List<ClientProcessorInfo> list = new ArrayList<>();
		for (Class cl : clients) {
			Processor processor = (Processor) cl.getAnnotation(Processor.class);
			if (processor == null) {
				if (cl.isMemberClass()) {
					Class cl2 = cl.getDeclaringClass();
					if (cl2 != null) {
						processor = (Processor) cl2.getAnnotation(Processor.class);
					}
				}
			}
			if (processor == null) {
				logger.error("class processor is null,class:" + cl.getName());
				throw new Exception("class processor is null,class:" + cl.getName());
			}
			if (processor.server().length() == 0) {
				logger.error("class processor server name is null,class:" + cl.getName());
				throw new Exception("class processor server name is null");
			}
			if (processor.service().length() == 0) {
				logger.error("class processor service name is null,class:" + cl.getName());
				throw new Exception("class processor service name is null");
			}
			list.add(new ClientProcessorInfo(processor.server(), processor.service(), cl));
		}

		return list;

	}

}
