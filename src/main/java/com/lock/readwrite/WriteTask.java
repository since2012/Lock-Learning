package com.lock.readwrite;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 写任务
 *
 * @author onlyone
 */
@Slf4j
public class WriteTask implements Runnable {

	private CountDownLatch countDownLatch;

	private WriteLock writeLock;

	public WriteTask(CountDownLatch countDownLatch, WriteLock lock) {
		this.countDownLatch = countDownLatch;
		this.writeLock = lock;
	}

	@Override
	public void run() {

		try {
			//将当前线程加入等待队列
			countDownLatch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log.debug(" 尝试请求write锁,,,,,,,");
		writeLock.lock();

		log.debug(" 已拿到write锁，开始处理业务");

		// 模拟业务处理
		try {
			Thread.sleep(new Random().nextInt(500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.debug(" 释放write锁！！！！！！！！！！！！");
		writeLock.unlock();

	}

}
