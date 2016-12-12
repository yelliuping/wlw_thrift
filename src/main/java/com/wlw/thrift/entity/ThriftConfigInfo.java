package com.wlw.thrift.entity;

import java.util.ArrayList;
import java.util.List;

public class ThriftConfigInfo {
	
	private String server;
	private String ip;
	private int    port;
	
	private int acceptThreads;
	private int selectThreads;
	private int acceptedQueueSize;
	private int workThreads;
	
	private List<String> services = new ArrayList<>();
	
	public ThriftConfigInfo() {}
	
	
	public ThriftConfigInfo(String server, String ip, int port, int acceptThreads, int selectThreads, int acceptedQueueSize,
			int workThreads) {
		super();
		this.server = server;
		this.ip = ip;
		this.port = port;
		this.acceptThreads = acceptThreads;
		this.selectThreads = selectThreads;
		this.acceptedQueueSize = acceptedQueueSize;
		this.workThreads = workThreads;
	}


	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
	

	public int getAcceptThreads() {
		return acceptThreads;
	}


	public void setAcceptThreads(int acceptThreads) {
		this.acceptThreads = acceptThreads;
	}


	public int getSelectThreads() {
		return selectThreads;
	}


	public void setSelectThreads(int selectThreads) {
		this.selectThreads = selectThreads;
	}


	public int getAcceptedQueueSize() {
		return acceptedQueueSize;
	}


	public void setAcceptedQueueSize(int acceptedQueueSize) {
		this.acceptedQueueSize = acceptedQueueSize;
	}


	public int getWorkThreads() {
		return workThreads;
	}


	public void setWorkThreads(int workThreads) {
		this.workThreads = workThreads;
	}

	

	public List<String> getServices() {
		return services;
	}


	public void setServices(List<String> services) {
		this.services = services;
	}


	@Override
	public String toString() {
		return "ServerInfo [server=" + server + ", ip=" + ip + ", port=" + port + ", acceptThreads=" + acceptThreads
				+ ", selectThreads=" + selectThreads + ", acceptedQueueSize=" + acceptedQueueSize + ", workThreads="
				+ workThreads + "]";
	}
}
