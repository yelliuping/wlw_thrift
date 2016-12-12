package com.wlw.thrift.zookeeper.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.wlw.thrift.util.Logger;


public class ClientSideZkEventQueue {
	// http://blog.csdn.net/z69183787/article/details/46986823
	private static BlockingQueue<ClientSideZkEvent> queue = null;
	static {
		queue = new LinkedBlockingQueue<ClientSideZkEvent>(Integer.MAX_VALUE);// 最大
	}

	public static void put(ClientSideZkEvent event) {
		// 即使阻塞也要放进去
		while (true) {
			try {
				queue.put(event);
				return;
			} catch (InterruptedException e) {
			}
		}
	}

	public static ClientSideZkEvent take() {
		ClientSideZkEvent event = null;
		// 不拿到event不罢休
		while (null == event) {
			try {
				event = queue.take();
			} catch (InterruptedException e) {

			}
		}
		return event;
	}

	private static final Logger logger = Logger.getLogger(ClientSideZkEventQueue.class);

	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {					
					ClientSideZkEventQueue.put(new ClientSideZkEvent(null, null, null));
					logger.info("put succeed");
				}
			}
		}).start();
		new Thread(new Runnable() {
			long begin = System.currentTimeMillis();
			int count = 0;

			@Override
			public void run() {
				while (true) {
					Object obj = ClientSideZkEventQueue.take();
					count++;
					if (System.currentTimeMillis() - begin >= 1000) {
						System.out.println("count---" + count);
						count = 0;
						begin = System.currentTimeMillis();
					}
				}
			}
		}).start();

	}
}
