package com.wlw.thrift.server;

import java.util.List;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

import com.wlw.thrift.consts.ServerConst;
import com.wlw.thrift.entity.TProcessorInfo;
import com.wlw.thrift.entity.ThriftConfigInfo;
import com.wlw.thrift.util.Logger;


public class ServerManager {
	private static final Logger logger = Logger.getLogger(ServerManager.class);
	public static TServer server=null;
	
	public static ThriftConfigInfo thriftConfog=(ThriftConfigInfo)ServerConst.serversMap.values().toArray()[0];
	
	public static void  start(List<TProcessorInfo> processors) {
		//ServerProperties property = ServerProperties.getInstance();
		//先获取第一个配置
		try {
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(thriftConfog.getPort());
			TFramedTransport.Factory transportFactory = new TFramedTransport.Factory();
			TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
			TMultiplexedProcessor processor = new ServerSideExtendedTMultiplexedProcessor();
			TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
			tArgs.transportFactory(transportFactory);
			tArgs.protocolFactory(protocolFactory);
			
			for(TProcessorInfo p:processors){
				processor.registerProcessor(p.getServiceName(), p.getProcessor());
				thriftConfog.getServices().add(p.getServiceName());
			}
			
			tArgs.processor(processor);
			tArgs.selectorThreads(thriftConfog.getSelectThreads());
			tArgs.acceptQueueSizePerThread(thriftConfog.getAcceptedQueueSize());
			tArgs.workerThreads(thriftConfog.getWorkThreads());
			
			
			logger.info("***************");
			logger.info("setting of thrift server is as follows:");
			logger.info("selector threads: " + tArgs.getSelectorThreads());
			logger.info("queue size: " + tArgs.getAcceptQueueSizePerThread());
			logger.info("worker threads: " + tArgs.getWorkerThreads());
			logger.info("***************");
			TServer server = new ServerSideThreadedSelectorServer(tArgs);
			server.serve();
			
		} catch (Exception e) {
			logger.error("ServerManager start error",e);
		} finally {
			logger.info("thrift server exit... @" + System.currentTimeMillis());
			System.exit(0);
		}

	}
	
	public void stop(){
		try {
			if(server!=null){
				server.stop();
			}
		} catch (Exception e) {
			logger.error("ServerManager stop error",e);	
		}
		
	}

}

