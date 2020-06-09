package com.lock.reentrant;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 * 可重入锁
 *
 * @author onlyone
 * </pre>
 */
@Slf4j
public class ReentrantTest implements Runnable {

	/**
	 * <pre>
	 * <li/>如果锁是具有可重入性的话，那么该线程在调用 m2 时并不需要再次获得当前对象的锁，可以直接进入 m2 方法进行操作。
	 * <li/>如果锁是不具有可重入性的话，那么该线程在调用 m2 前会等待当前对象锁的释放，实际上该对象锁已被当前线程所持有，不可能再次获得。
	 * <li/>如果锁是不具有可重入性特点的话，那么线程在调用同步方法、含有锁的方法时就会产生死锁。
	 * </pre>
	 */
	public synchronized void m1() {
		// 模拟执行业务
		try {
			log.debug("执行方法 m1 的业务！");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		m2();
	}

	public synchronized void m2() {
		try {
			log.debug("执行方法 m2 的业务！");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private CountDownLatch countDownLatch;

	@Override
	public void run() {
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		m1();
	}

	public ReentrantTest(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		ReentrantTest lockTest = new ReentrantTest(countDownLatch);

		// 同时启动5个任务
		for (int i = 1; i <= 5; i++) {
			new Thread(lockTest).start();
		}

		countDownLatch.countDown();

		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);
	}

}
