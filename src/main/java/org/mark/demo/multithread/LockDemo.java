package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName LockDemo
 * @Description TODO
 * @Author markt
 * @Date 2020-06-09
 * @Version 1.0
 */
@Slf4j
public class LockDemo implements Runnable {

	private HashMap<String, Integer> map;

	private CountDownLatch latch;

	private CountDownLatch end;

	private ReentrantLock lock;

	LockDemo(HashMap<String, Integer> map, CountDownLatch latch, ReentrantLock lock, CountDownLatch end) {
		this.map = map;
		this.latch = latch;
		this.lock = lock;
		this.end = end;
	}

	public static void main(String[] args) {
		ReentrantLock lock = new ReentrantLock();
		CountDownLatch latch = new CountDownLatch(1000);
		CountDownLatch end = new CountDownLatch(1000);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", 0);
		long start = System.currentTimeMillis();
		LockDemo test = new LockDemo(map, latch, lock, end);
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
		log.debug("{}", map.get("1"));
	}

	@Override
	public void run() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 100000; i++) {
			lock.lock();
			try {
				Integer z = map.get("1");
				map.put("1", ++z);
			} finally {
				lock.unlock();
			}
		}
		end.countDown();
	}

}
