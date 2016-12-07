package com.wlw.thrift.client;

import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;

import com.wlw.thrift.client.socket.TSocketPool;


public class ClientSideTMultiplexedProtocol extends TMultiplexedProtocol {
	private TSocketPool poolRef;

	public ClientSideTMultiplexedProtocol(TProtocol protocol, String serviceName) {
		super(protocol, serviceName);
	}

	public TSocketPool getPoolRef() {
		return poolRef;
	}

	public void setPoolRef(TSocketPool poolRef) {
		this.poolRef = poolRef;
	}

}
