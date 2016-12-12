package com.wlw.thrift;

import java.net.MalformedURLException;
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
	
	
	
	private static void url() throws MalformedURLException {
		
	   /* //ThriftUri.getProtocol("thrift://www.runoob.com/html/html-tutorial.html");
		URL url = new URL("thrift://www.runoob.com/html/html-tutorial.html");
		System.out.println("URL 是 " + url.toString());
		System.out.println("协议是 " + url.getProtocol());
		System.out.println("文件名是 " + url.getFile());
		System.out.println("主机是 " + url.getHost());
		System.out.println("路径是 " + url.getPath());
		System.out.println("端口号是 " + url.getPort());
		System.out.println("默认端口号是 "
		+ url.getDefaultPort());*/

	}

}
