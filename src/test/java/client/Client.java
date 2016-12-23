package client;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

import com.wlw.thrift.client.ClientManager;
import com.wlw.thrift.client.entity.ServiceClientInfo;

import service.HelloWorldService;

public class Client {
	
	public static void main(String[] args) {
		List<Class<? extends TServiceClient>> clients = new ArrayList<>();
		clients.add(HelloWorldService.Client.class);
		ClientManager.initClients(clients);
		ServiceClientInfo<? extends TServiceClient> clientInfo =ClientManager.getServiceClientInfo(HelloWorldService.Client.class);
		//client.getServiceClient().
		HelloWorldService.Client client=(HelloWorldService.Client)clientInfo.getServiceClient();
		try {
			for(int i=0;i<100;i++){
				String str=client.helloWorldString("hello world abcdefg i:"+i);
				System.out.println(str);
			}
			client.helloWorldString("hello world");
		} catch (TException e) {
			e.printStackTrace();
		}
	}

}
