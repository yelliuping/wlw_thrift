package com.wlw.thrift.client.serverData;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TServiceClient;

import com.wlw.thrift.entity.ClientClassInfo;
import com.wlw.thrift.entity.ProtocolModel;

public class ClientData {
	
	/**
	 * 提供者注册信息
	 */
	private static ConcurrentHashMap<String,ProtocolModel> providersMap=new ConcurrentHashMap<>();
	
	/**
	 * 客户端调用信息
	 */
	private static ConcurrentHashMap<String,Set<ClientClassInfo>> clientClassesMap=new ConcurrentHashMap<>();
	
	public static void add(ProtocolModel model){
		providersMap.put(model.getServer(), model);
	}
	
	public static void del(ProtocolModel model){
		providersMap.remove(model.getServer());
	}
	
	public static ProtocolModel get(String serverName){
		return providersMap.get(serverName);
	}
	
	public static void add(String server,String service,Class<? extends TServiceClient> clientClass){
		Set<ClientClassInfo> set=clientClassesMap.get(server);
		if(set==null){
			set=new HashSet<>();
			clientClassesMap.put(server, set);
		}
		ClientClassInfo cInfo = new ClientClassInfo();
		cInfo.setServer(server);
        cInfo.setService(service);
        cInfo.setClientClass(clientClass);
		set.add(cInfo);
	}
	
	public static Set<ClientClassInfo> getClientClasseInfo(String server){
		return clientClassesMap.get(server);
	}
	

}
