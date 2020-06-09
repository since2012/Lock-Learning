package org.mark.demo.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName TryLockTest
 * @Description 尝试在限定时间内获取令牌，超时则结束尝试
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class TryLockTest implements Runnable {
	public static ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) throws InterruptedException {
		TryLockTest test = new TryLockTest();
		Thread t1 = new Thread(test);
		Thread t2 = new Thread(test);
		t1.start();
		t2.start();
//		Thread.sleep(5000);
	}

	@Override
	public void run() {
		try {
			// 等待1秒
			if (lock.tryLock(1, TimeUnit.SECONDS)) {
				log.debug("加锁成功");
				Thread.sleep(2000);
			} else {
				log.debug("获取锁失败！");
			}
		} catch (Exception e) {
			log.error("sleep异常");
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
				log.debug("解锁成功");
			}

		}
	}
}