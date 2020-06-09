package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName LockDemos
 * @Description TODO
 * @Author markt
 * @Date 2020-06-09
 * @Version 1.0
 */
@Slf4j
public class LockDemos implements Runnable {

	ReentrantLock readLock = new ReentrantLock();

	public void get() {
		readLock.lock();
		log.debug("get data");
		set();
		readLock.unlock();
	}

	public void set() {
		readLock.lock();
		log.debug("set data");
		readLock.unlock();
	}

	@Override
	public void run() {
		get();
	}

	public static void main(String[] args) {
		LockDemos ss = new LockDemos();
		new Thread(ss).start();
		new Thread(ss).start();
		new Thread(ss).start();
	}
}