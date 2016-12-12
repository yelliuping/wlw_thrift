package com.wlw.thrift.zookeeper.client;

public class ClientSideZkEvent {
	private String service;
	private ClientSideZkEventType type;
	private String data;

	public ClientSideZkEvent(String s, ClientSideZkEventType t, String d) {
		service = s;
		type = t;
		data = d;
	}

	@Override
	public String toString() {
		return "servie: " + service + " type: " + type + " data: " + data;
	}

	public ClientSideZkEventType getType() {
		return type;
	}

	public void setType(ClientSideZkEventType type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
