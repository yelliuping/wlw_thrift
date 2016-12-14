package com.wlw.thrift.client.serviceClient;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;

/**
 * 协议通讯通道操作命令
 * 
 * @author yelp
 *
 */
public class TProtocolCommad {

	public static void flush(TProtocol protocol) throws TTransportException{
		if(protocol!=null){
			if(protocol.getTransport()!=null){
				if(protocol.getTransport().isOpen()){
					protocol.getTransport().flush();
				}
			}
		}
	}
	
	public static boolean isActive(TProtocol protocol){
		boolean isActive=false;
		if(protocol!=null){
			if(protocol.getTransport()!=null){
				isActive=protocol.getTransport().isOpen();
			}
		}
		return isActive;
	}
	public static void close(TProtocol protocol){
		if(protocol!=null){
			if(protocol.getTransport()!=null){
				if(protocol.getTransport().isOpen()){
					protocol.getTransport().close();
				}
			}
		}
	}
}
