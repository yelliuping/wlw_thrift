package server;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TProcessor;

import com.wlw.thrift.server.ServerManager;

import service.HelloWorldService;

public class Server {
	public static void main(String[] args){
		List<TProcessor> processors = new ArrayList<TProcessor>();
		processors.add(new HelloWorldService.Processor<HelloWorldServiceImpl>(new HelloWorldServiceImpl()));
		ServerManager.start(processors);
	}
}
