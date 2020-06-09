package com.lock.readwrite;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁测试
 * 读写锁的特点：读锁为共享锁，写锁为互斥锁
 * 写锁的优先级更高
 *
 * @author onlyone
 */
@Slf4j
public class ReentrantReadWriteLockTest {

	public static void main(String[] args) throws InterruptedException {

		CountDownLatch countDownLatch = new CountDownLatch(1);
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		new Thread(new WriteTask(countDownLatch, lock.writeLock())).start();

		// 启动20个读任务
		for (int i = 1; i <= 30; i++) {
			new Thread(new ReadTask(countDownLatch, lock.readLock())).start();
		}

		// 启动20个写任务
		for (int i = 1; i <= 10; i++) {
			new Thread(new WriteTask(countDownLatch, lock.writeLock())).start();
		}

		//减至0时释放所有等待的线程
		countDownLatch.countDown();

		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);
	}

}
