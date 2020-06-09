package com.lock.readwrite;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

/**
 * 读任务
 *
 * @author onlyone
 */
@Slf4j
public class ReadTask implements Runnable {

	private CountDownLatch countDownLatch;

	private ReadLock readLock;

	public ReadTask(CountDownLatch countDownLatch, ReadLock readLock) {
		this.countDownLatch = countDownLatch;
		this.readLock = readLock;
	}

	@Override
	public void run() {

		try {
			countDownLatch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log.debug(" 尝试请求read锁,,,,,,,");
		readLock.lock();

		log.debug(" 已拿到read锁，开始处理业务");

		// 模拟业务处理
		try {
			Thread.sleep(new Random().nextInt(500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.debug(" 释放read锁！！！！！！！！！！！！");
		readLock.unlock();

	}
}
