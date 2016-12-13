package com.wlw.thrift;

import com.wlw.thrift.zookeeper.client.ClientManager;

public class ClientMain {
	
	public static void main(String[] args) throws Exception {
		System.out.println("ClientMain starting .....");
		ClientManager.start();
		
		System.out.println("ClientMain started!!!!!!!!!!");
		
		while(true){
			try {
				
				Thread.sleep(6000);
			} catch (Exception e) {
				e.printStackTrace();
				Thread.interrupted();
			}
		}
		
	}

}
