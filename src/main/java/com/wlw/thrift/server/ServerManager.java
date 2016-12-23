package com.wlw.thrift.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

import com.wlw.thrift.annotation.Processor;
import com.wlw.thrift.consts.ServerConst;
import com.wlw.thrift.entity.TProcessorInfo;
import com.wlw.thrift.entity.ThriftConfigInfo;
import com.wlw.thrift.util.Logger;
import com.wlw.thrift.zookeeper.server.ZkManager;

public class ServerManager {
	private static final Logger logger = Logger.getLogger(ServerManager.class);
	public static TServer server = null;

	public static ThriftConfigInfo thriftConfog = (ThriftConfigInfo) ServerConst.serversMap.values().toArray()[0];

	public static void start(List<TProcessor> processors) {
		try {
			//初始化配置文件
			ServerConst.init();
			//初始化zk
			ZkManager.start();
			//获取TProcessorInfo
			List<TProcessorInfo> list=getTProcessorInfos(processors);
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(thriftConfog.getPort());
			TFramedTransport.Factory transportFactory = new TFramedTransport.Factory();
			TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
			TMultiplexedProcessor processor = new TMultiplexedProcessor();
			TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
			tArgs.transportFactory(transportFactory);
			tArgs.protocolFactory(protocolFactory);

			for (TProcessorInfo p : list) {
				processor.registerProcessor(p.getServiceName(), p.getProcessor());
				thriftConfog.getServices().add(p.getServiceName());
				logger.info("registerProcessor，serviceName:"+p.getServiceName()+",processor:"+p.getProcessor().getClass().getName());
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
			TServer server = new TThreadedSelectorServer(tArgs);
			server.serve();
		} catch (Exception e) {
			logger.error("ServerManager start error", e);
		} finally {
			logger.info("thrift server exit");
			System.exit(0);
		}

	}

	public void stop() {
		try {
			if (server != null) {
				server.stop();
			}
		} catch (Exception e) {
			logger.error("ServerManager stop error", e);
		}

	}

	public static List<TProcessorInfo> getTProcessorInfos(List<TProcessor> processors) throws Exception {
		List<TProcessorInfo> list = new ArrayList<>();
		for (TProcessor pro : processors) {
			Class cl = pro.getClass();
			Processor processor = (Processor) cl.getAnnotation(Processor.class);
			if (processor == null) {
				if (cl.isMemberClass()) {
					Class cl2 = cl.getDeclaringClass();
					if (cl2 != null) {
						processor = (Processor) cl2.getAnnotation(Processor.class);
					}
				}
			}
			if (processor == null) {
				logger.error("class processor is null,class:" + cl.getName());
				throw new Exception("class processor is null,class:" + cl.getName());
			}
			if (processor.server().length() == 0) {
				logger.error("class processor server name is null,class:" + cl.getName());
				throw new Exception("class processor server name is null");
			}
			if (processor.service().length() == 0) {
				logger.error("class processor service name is null,class:" + cl.getName());
				throw new Exception("class processor service name is null");
			}
			list.add(new TProcessorInfo(processor.server(), processor.service(), pro));
		}
		return list;
	}

}
