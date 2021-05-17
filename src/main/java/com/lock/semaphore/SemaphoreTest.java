package com.lock.semaphore;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * 信号量锁
 *
 * @author onlyone
 */
@Slf4j
public class SemaphoreTest implements Runnable {

	private ResourceManage resourceManage;
	private int userId;
	private CountDownLatch countDownLatch;

	public SemaphoreTest(ResourceManage resourceManage, int userId, CountDownLatch countDownLatch) {
		this.resourceManage = resourceManage;
		this.userId = userId;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.debug("userId:{}准备使用资源...", userId);
		resourceManage.useResource(userId);
	}

	public static void main(String[] args) throws InterruptedException {
		ResourceManage resourceManage = new ResourceManage();
		CountDownLatch countDownLatch = new CountDownLatch(1);
		for (int i = 1; i <= 30; i++) {
			new Thread(new SemaphoreTest(resourceManage, i, countDownLatch)).start(); // 创建多个资源使用者
		}

		countDownLatch.countDown();

		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);

	}
}
