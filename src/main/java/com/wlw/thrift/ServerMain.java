package com.wlw.thrift;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;

import com.sun.jndi.toolkit.url.Uri;
import com.wlw.thrift.consts.ServerConst;
import com.wlw.thrift.util.ThriftUri;
import com.wlw.thrift.zookeeper.server.ZkManager;

public class ServerMain {
	
	public static void main(String[] args) throws Exception {
		ServerConst.init();
		ZkManager.start();
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
