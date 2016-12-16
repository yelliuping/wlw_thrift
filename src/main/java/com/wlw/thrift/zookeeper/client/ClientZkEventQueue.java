package com.wlw.thrift.zookeeper.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.wlw.thrift.util.Logger;


public class ClientZkEventQueue {
	private static final Logger logger = Logger.getLogger(ClientZkEventQueue.class);
	
	private static BlockingQueue<ClientZkEvent>  queue = new LinkedBlockingQueue<ClientZkEvent>(10000);

	public static void put(ClientZkEvent event) {
		// 即使阻塞也要放进去
		while (true) {
			try {
				queue.put(event);
				return;
			} catch (InterruptedException e) {
			}
		}
	}

	public static ClientZkEvent take() {
		ClientZkEvent event = null;
		// 不拿到event不罢休
		while (null == event) {
			try {
				event = queue.take();
			} catch (InterruptedException e) {

			}
		}
		return event;
	}
}
