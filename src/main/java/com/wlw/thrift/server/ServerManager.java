package com.wlw.thrift.server;

import java.util.List;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

import com.wlw.thrift.consts.ServerProperties;
import com.wlw.thrift.entity.TProcessorInfo;
import com.wlw.thrift.util.Logger;


public class ServerManager {
	private static final Logger logger = Logger.getLogger(ServerManager.class);
	TServer server=null;
	
	public void start(List<TProcessorInfo> processors) {
		ServerProperties property = ServerProperties.getInstance();
		try {
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(property.getPort());
			TFramedTransport.Factory transportFactory = new TFramedTransport.Factory();
			TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
			TMultiplexedProcessor processor = new ServerSideExtendedTMultiplexedProcessor();
			TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
			tArgs.transportFactory(transportFactory);
			tArgs.protocolFactory(protocolFactory);
			
			for(TProcessorInfo p:processors){
				processor.registerProcessor(p.getServiceName(), p.getProcessor());
			}
			
			tArgs.processor(processor);
			tArgs.selectorThreads(property.getSelectorThreads());
			tArgs.acceptQueueSizePerThread(property.getQueueSize());
			tArgs.workerThreads(property.getWorkerThreads());
			
			logger.info("***************");
			logger.info("setting of thrift server is as follows:");
			logger.info("selector threads: " + tArgs.getSelectorThreads());
			logger.info("queue size: " + tArgs.getAcceptQueueSizePerThread());
			logger.info("worker threads: " + tArgs.getWorkerThreads());
			logger.info("***************");
			TServer server = new ServerSideThreadedSelectorServer(tArgs);
			server.serve();
			
		} catch (Exception e) {
			logger.error("exception as :" + e.toString());
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
			
		}
		
	}

}
