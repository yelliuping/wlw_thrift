package com.wlw.thrift.zookeeper.server;

import java.net.URLEncoder;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;

import com.wlw.thrift.consts.CommonConst;
import com.wlw.thrift.entity.ProtocolModel;
import com.wlw.thrift.entity.ThriftConfigInfo;
import com.wlw.thrift.util.Logger;
import com.wlw.thrift.util.NetAddressUtils;


public class ServerZkServiceRegisterRunnable implements Runnable {

	private static final Logger logger = Logger.getLogger(ServerZkServiceRegisterRunnable.class);
	private String service = "";
	private String uri="";
	CuratorFramework client;

	public ServerZkServiceRegisterRunnable(CuratorFramework c, ThriftConfigInfo configInfo) {
		this.client = c;
		this.service = "/"+CommonConst.zk_server_root+"/"+configInfo.getServer()+"/"+CommonConst.zk_server_provider;
		ProtocolModel model=new ProtocolModel();
		try {
			model.setHost(NetAddressUtils.getRealIp());
		} catch (Exception e) {
			logger.error("NetAddressUtils.getRealIp error: ",e);
		}
		model.setPort(configInfo.getPort());
		model.setServer(configInfo.getServer());
		model.setServices(configInfo.getServices().toString());
		this.uri = model.getUri();
	}

	@Override
	public void run() {
		boolean registerSucceed = false;
		int count = 0;
		while (false == registerSucceed) {
			logger.info("try to register service: " + service);
			try {
				// 先保证此永久节点存在
				EnsurePath ensurePath = new EnsurePath(service);
				ensurePath.ensure(client.getZookeeperClient());
				// 构造临时节点
				uri=URLEncoder.encode(uri, "UTF-8");
				client.create().withMode(CreateMode.EPHEMERAL).forPath(service+"/"+uri, null);
				// 注册成功，就跳出循环，表示此runnable结束
				registerSucceed = true;
				// 注册成功
				logger.info("succeed to register service: " + service);
				return;
			} catch (Exception e) {
				logger.error("failt to register service: " + service,e);
				// 假设存在就退出的话,会可能导致永远注册不上
				// 但是，存在也不退出的话，又会导致一直在重复注册
				// 所以，尝试6个会话失效周期后，如果节点还存在，那就是真的存在了，就不需要再继续注册了
				count++;
				if (count >= 7) {
					registerSucceed = true;
					break;
				}
				registerSucceed = false;
			} 
		}
		// 运行结束
	}

}
