package com.wlw.thrift.entity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 
 * 发布者或订阅者在zk的协议模型
 * 
 * 例子：thrift://127.0.0.1:8080/serverName?isMutil=true&services=helloService,helloService&state=1
 * 
 * @author yelp
 *
 */
public class ProtocolModel {
	
	private   String 	 scheme="thrift";//现在只支持 "thrift"
	private   String     host;
	private   int    	 port;
	private   String     serverName;     //thrift服务启动的名称
	private   boolean    isMutil=true;   //是否是多service
	private   String     services;       //服务名称用逗号隔开，如果isMutil=false那么services=""
	private   byte 		 state=1;        //1 启用或2 禁用
	  
	  
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public byte getState() {
		return state;
	}
	public void setState(byte state) {
		this.state = state;
	}
	public boolean isMutil() {
		return isMutil;
	}
	public void setMutil(boolean isMutil) {
		this.isMutil = isMutil;
	}
	public String getServices() {
		return services;
	}
	public void setServices(String services) {
		this.services = services;
	}
	
	public String getUri(){
		String uri=scheme+"/"+host+":"+port+"/"+serverName+"?isMutil="+isMutil+"&state="+state+"&services="+services;
		return uri;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String url="thrift://127.0.0.1:8080/serverName?isMutil=true&services=helloService,helloService&state=1";
	    String decode=	URLEncoder.encode(url, "UTF-8");
	    System.out.println(decode);
	}
	  
}
