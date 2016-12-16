package com.wlw.thrift.zookeeper.client;

/**
 * zk客户端监听到的事件信息
 * 
 * @author yelp
 *
 */
public class ClientZkEvent {
	
	private ClientZkEventType type;
	String root;//根目录
	String server;//服务节点
	String serverType;//服务类型 提供者或消费者
	String regNode;//注册节点
	
	public ClientZkEvent(ClientZkEventType type, String root, String server, String serverType, String regNode) {
		super();
		this.type = type;
		this.root = root;
		this.server = server;
		this.serverType = serverType;
		this.regNode = regNode;
	}
	public ClientZkEventType getType() {
		return type;
	}
	public void setType(ClientZkEventType type) {
		this.type = type;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getRegNode() {
		return regNode;
	}
	public void setRegNode(String regNode) {
		this.regNode = regNode;
	}
	
	@Override
	public String toString() {
		return "ClientZkEvent [type=" + type + ", root=" + root + ", server=" + server + ", serverType=" + serverType
				+ ", regNode=" + regNode + "]";
	}

}
