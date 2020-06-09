package org.mark.demo.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName ReentrantLockTest
 * @Description 显式加锁，灵活性更好，同一线程支持加锁多次
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class ReentrantLockTest implements Runnable {

	public static ReentrantLock lock = new ReentrantLock();
	public static int total = 0;

	public static void main(String[] args) throws InterruptedException {
		ReentrantLockTest test = new ReentrantLockTest();
		Thread t1 = new Thread(test);
		Thread t2 = new Thread(test);
		t1.start();
		t2.start();
		t1.join();
		t2.join(); // main线程会等待t1和t2都运行完再执行以后的流程
		log.debug("累计值：{}", total);
	}

	@Override
	public void run() {
		for (int j = 0; j < 10000; j++) {
			lock.lock();  // 看这里就可以
//			lock.lock();
			try {
				total++;
			} finally {
				lock.unlock(); // 看这里就可以
//				lock.unlock();
				log.debug("当前值：{}", total);
			}
		}
	}
}