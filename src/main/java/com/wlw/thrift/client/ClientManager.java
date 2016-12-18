package com.wlw.thrift.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TServiceClient;

import com.wlw.thrift.annotation.Processor;
import com.wlw.thrift.client.clientLocal.ClientThreadLocal;
import com.wlw.thrift.client.entity.ClientProcessorInfo;
import com.wlw.thrift.client.entity.ServiceClientInfo;
import com.wlw.thrift.client.serviceClient.MutiServiceClientPool;
import com.wlw.thrift.util.Logger;

public class ClientManager {
	private static final Logger logger = Logger.getLogger(ClientManager.class);
	
	private  ClientThreadLocal<ServiceClientInfo<? extends TServiceClient>> local = new ClientThreadLocal<>();
	
	private MutiServiceClientPool<? extends TServiceClient> mutiPool;
	public  void  start(List<Class<? extends TServiceClient>> clients) {
		//先获取第一个配置
		try {
			List<ClientProcessorInfo> clientInfos= getClientProcessorInfo(clients);
			mutiPool=new MutiServiceClientPool<>(null);
			mutiPool.addByClients(clientInfos);
		} catch (Exception e) {
			logger.error("ClientManager start error",e);
		} 
		
		    

	}
	
	 List<ClientProcessorInfo> getClientProcessorInfo(List<Class<? extends TServiceClient>> clients) throws Exception{
		 List<ClientProcessorInfo> list = new ArrayList<>();
		 for(Class cl:clients){
			 Processor processor= (Processor) cl.getAnnotation(Processor.class);
			 if(processor==null){
				if( cl.isMemberClass()){
					Class cl2=cl.getDeclaringClass();
					if(cl2!=null){
						processor= (Processor) cl2.getAnnotation(Processor.class);
					}
				}
			 }
			 if(processor==null){
				 logger.info("class processor is null,class:"+cl.getName());
				 throw new Exception("class processor is null,class:"+cl.getName());
			 }
			 if(processor.server().length()==0){
				 logger.info("class processor server name is null,class:"+cl.getName());
				 throw new Exception("class processor server name is null");
			 }
			 if(processor.service().length()==0){
				 logger.info("class processor service name is null,class:"+cl.getName());
				 throw new Exception("class processor service name is null");
			 }
			 list.add(new ClientProcessorInfo(processor.server(), processor.service(), cl));
		 }
		 
		 return list;
		 
	}
	
	

}
