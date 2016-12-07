package com.wlw.thrift.client.socket;

import java.net.Socket;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TFramedTransport;

import com.wlw.thrift.client.ClientSideTMultiplexedProtocol;
import com.wlw.thrift.util.Logger;



/**
 * MyTSocket池对象工厂
 * 
 * @author liuzq 2016年11月2日
 * @QQ 837500869
 */
public class TSocketFactory extends BasePooledObjectFactory<TMultiplexedProtocol> {

	private static final Logger logger = Logger.getLogger(TSocketFactory.class);
	private String ip;
	private int port;

	public TSocketFactory(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public TMultiplexedProtocol create() throws Exception {
		// 原生
		MyTSocket transport = new MyTSocket(ip, port);
		transport.open();
		
		// 封装
		TMultiplexedProtocol tMultiProtocol = new ClientSideTMultiplexedProtocol(new TBinaryProtocol(new TFramedTransport(transport)), null);
		// 返回
		return tMultiProtocol;
	}

	@Override
	public PooledObject<TMultiplexedProtocol> wrap(TMultiplexedProtocol tMultiProtocol) {
		return new DefaultPooledObject<TMultiplexedProtocol>(tMultiProtocol);
	}

	@Override
	public void destroyObject(PooledObject<TMultiplexedProtocol> p) throws Exception {
		// 拿到transport
		TFramedTransport transport = (TFramedTransport) p.getObject().getTransport();
		// 借助于反射拿到字段的值
		MyTSocket myTocket = (MyTSocket) TFramedTransportFieldHelper.get(transport);
		// 关闭物理连接
		myTocket.close();
		// 告诉连接池销毁此对象
		super.destroyObject(p);
	}

	@Override
	public boolean validateObject(PooledObject<TMultiplexedProtocol> p) {
		// 取出MyTSocket对象
		MyTSocket myTSocket = null;
		try {
			myTSocket = (MyTSocket) TFramedTransportFieldHelper.get((TFramedTransport) (p.getObject().getTransport()));
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
		// 再取出普通socket
		Socket s = myTSocket.getSocket();
		// 取出状态1
		boolean closed = s.isClosed();
		boolean connected = s.isConnected();
		boolean outputShutdown = s.isOutputShutdown();
		boolean inputShutdown = s.isInputShutdown();
		// 增加一个状态位2
		boolean urgentFlag = false;
		try {
			s.sendUrgentData(0xFF);
			urgentFlag = true;
		} catch (Exception e) {

		}
		// 所有条件一起判断
		return urgentFlag && connected && !closed && !inputShutdown && !outputShutdown && myTSocket.isAlive();
	}

}
