package org.mark.demo.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName FairLockTest
 * @Description 公平锁，可以统计日志验证
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class FairLockTest implements Runnable {
	public static ReentrantLock lock = new ReentrantLock(true);

	public static void main(String[] args) throws InterruptedException {
		FairLockTest test = new FairLockTest();
		new Thread(test).start();
		new Thread(test).start();
		new Thread(test).start();
		new Thread(test).start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				lock.lock();
				log.debug("获取到了锁！");
			} finally {
				lock.unlock();
			}
		}
	}
}