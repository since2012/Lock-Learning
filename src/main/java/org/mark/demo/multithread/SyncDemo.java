package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @FileName SyncDemo
 * @Description ReentrantLock比synchronized快的主要原因还是：ReentrantLock有利用CAS自旋操作来实现锁，
 * 在大并发的情况下synchronized线程间切换会有很大的资源消耗。
 * @Author markt
 * @Date 2020-06-09
 * @Version 1.0
 */
@Slf4j
public class SyncDemo implements Runnable {

	private HashMap<String, Integer> x;

	private CountDownLatch latch;

	SyncDemo(HashMap<String, Integer> x, CountDownLatch latch) {
		this.x = x;
		this.latch = latch;
	}

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(1000);
		CountDownLatch end = new CountDownLatch(1000);
		HashMap<String, Integer> x = new HashMap<String, Integer>();
		x.put("1", 0);
		long start = System.currentTimeMillis();
		SyncDemo test = new SyncDemo(x, latch);
		for (int i = 0; i < 1000; i++) {
			Thread t = new Thread(test);
			t.start();
			latch.countDown();
		}
		try {
			end.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.debug("{}", System.currentTimeMillis() - start);
		log.debug("{}", x.get("1"));
	}

	@Override
	public void run() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 100000; i++) {
			add();
		}

	}

	synchronized void add() {
		Integer i = x.get("1");
		x.put("1", ++i);
	}

}
