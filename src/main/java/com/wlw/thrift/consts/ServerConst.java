package com.wlw.thrift.consts;

import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import com.wlw.thrift.entity.ThriftConfigInfo;
import com.wlw.thrift.entity.ZkConfigInfo;
import com.wlw.thrift.util.Logger;

/**
 * 服务端常量
 * 
 * @author yelp
 *
 */
public class ServerConst {
	
	private static final Logger logger = Logger.getLogger(ServerConst.class);
	
	public static LinkedHashMap<String, ThriftConfigInfo> serversMap=new LinkedHashMap<>();
	public static ZkConfigInfo zkInfo=null;
	
	
	
	
	
	public static void init() {// 静态块里，只加载一次
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(KEYS.config_name);
			
			//get thrift properties
			String serversStr=bundle.getString(KEYS.servers);
			int    acceptThreads	 = Integer.parseInt(bundle.getString(KEYS.server_acceptThreads));
			int    selectThreads	 = Integer.parseInt(bundle.getString(KEYS.server_selectThreads));
			int    acceptedQueueSize = Integer.parseInt(bundle.getString(KEYS.server_acceptedQueueSize));
			int    workThreads		 = Integer.parseInt(bundle.getString(KEYS.server_workThreads));
			if(serversStr!=null&&serversStr.length()>0){
				int index=1;
				String [] serversStrs=serversStr.split(",");
				for(String temServer:serversStrs){
					if(temServer!=null&&temServer.length()>0){
						String ipKey  = String.format(KEYS.server_ip, temServer);
						String portKey= String.format(KEYS.server_port, temServer);
						String temIp =bundle.getString(ipKey);
						int temPort =Integer.parseInt(bundle.getString(portKey));
						ThriftConfigInfo serverInfo = new ThriftConfigInfo(temServer,temIp,temPort,acceptThreads,selectThreads,acceptedQueueSize,workThreads);
						serversMap.put(temServer, serverInfo);
						logger.info("get thrift properties from config_name="+KEYS.config_name+",server"+index+",serverInfo:"+serverInfo.toString());
						index++;
					}
				}
			}
			
			// get zookeeper properties
			String zookeeperAddress				=bundle.getString(KEYS.zookeeper_address);
			int    zookeeperSessionTimeoutMs	=Integer.parseInt(bundle.getString(KEYS.zookeeper_sessionTimeoutMs));
			int    zookeeperConnectionTimeoutMs	=Integer.parseInt(bundle.getString(KEYS.zookeeper_connectionTimeoutMs));
			zkInfo=new ZkConfigInfo(zookeeperAddress, zookeeperSessionTimeoutMs, zookeeperConnectionTimeoutMs);
			logger.info("get zookeeper properties from config_name="+KEYS.config_name+",zookeeperInfo:"+zkInfo.toString());
		} catch (Exception e) {
			serversMap.clear();
			logger.error("ResourceBundle.getBundle error config_name="+KEYS.config_name,e);
		}
	}
	

   public static class KEYS{
	   public static String config_name 	= "thrift-server";
		public static String servers 		= "thrift.servers";
		public static String server_ip 		= "thrift.%s.ip";
		public static String server_port    = "thrift.%s.port";
		public static String server_acceptThreads    = "thrift.acceptThreads";
		public static String server_selectThreads    = "thrift.selectThreads";
		public static String server_acceptedQueueSize= "thrift.acceptedQueueSize";
		public static String server_workThreads      = "thrift.workThreads";
		public static String zookeeper_address      = "zookeeper.address";
		public static String zookeeper_sessionTimeoutMs      = "zookeeper.sessionTimeoutMs";
		public static String zookeeper_connectionTimeoutMs      = "zookeeper.connectionTimeoutMs";
   }
}
