package com.wlw.thrift;

import java.util.ArrayList;
import java.util.List;

import com.wlw.thrift.consts.ServerConst;
import com.wlw.thrift.entity.TProcessorInfo;
import com.wlw.thrift.server.ServerManager;
import com.wlw.thrift.zookeeper.server.ZkManager;

public class ServerMain {
	
	public static void main(String[] args) throws Exception {
		ServerConst.init();
		ZkManager.start();
		List<TProcessorInfo> processors =new ArrayList<>();
		ServerManager.start(processors);
		
	}

}
