package org.mark.demo.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName KillDeadlock
 * @Description 解决死锁(通过外部中断)
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class KillDeadlock implements Runnable {

	public static ReentrantLock lock1 = new ReentrantLock();
	public static ReentrantLock lock2 = new ReentrantLock();
	int lock;

	public KillDeadlock(int lock) {
		this.lock = lock;
	}

	public static void main(String[] args) throws InterruptedException {
		KillDeadlock deadLock1 = new KillDeadlock(1);
		KillDeadlock deadLock2 = new KillDeadlock(2);
		Thread t1 = new Thread(deadLock1);
		Thread t2 = new Thread(deadLock2);
		t1.start();
		t2.start();
		Thread.sleep(1000);
		t2.interrupt(); // ③
	}

	@Override
	public void run() {
		try {
			if (lock == 1) {
				//若无中断则一直持有锁
				lock1.lockInterruptibly();  // 以可以响应中断的方式加锁
				log.debug("1加锁");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				lock2.lockInterruptibly();
				log.debug("2加锁");
			} else {
				lock2.lockInterruptibly();  // 以可以响应中断的方式加锁
				log.debug("2加锁");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				lock1.lockInterruptibly();
				log.debug("1加锁");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//如果被当前线程所持有
			if (lock1.isHeldByCurrentThread()) {
				lock1.unlock();  // 注意判断方式
				log.debug("1解锁");
			}
			if (lock2.isHeldByCurrentThread()) {
				lock2.unlock();
				log.debug("2解锁");
			}
			log.debug("退出！");
		}
	}
}
