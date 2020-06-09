package com.lock.object;

import lombok.extern.slf4j.Slf4j;

/**
 * synchronized (非this对象)
 * 不同线程需要加锁的对象不一致
 * 即同一实例的所有同步方法共用一把锁
 * 加锁对象某些情况可以理解为令牌
 *
 * @author onlyone
 */
@Slf4j
public class SynchronizedObject {

	private Object lock1 = new Object();
	private Object lock2 = new Object();

	public void m1() {
		synchronized (lock1) {
			log.debug("m1方法获得锁");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.debug("m1方法释放锁");
		}
	}

	public void m2() {
		synchronized (lock2) {
			log.debug("m2方法获得锁");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.debug("m2方法释放锁");

		}
	}

	static class Task1 implements Runnable {

		private SynchronizedObject synchronizedObject;

		public Task1(SynchronizedObject synchronizedObject) {
			this.synchronizedObject = synchronizedObject;
		}

		@Override
		public void run() {
			synchronizedObject.m1();
		}
	}

	static class Task2 implements Runnable {

		private SynchronizedObject synchronizedObject;

		public Task2(SynchronizedObject synchronizedObject) {
			this.synchronizedObject = synchronizedObject;
		}

		@Override
		public void run() {
			synchronizedObject.m2();
		}
	}

//不一致时能够并行执行，加锁为同一对象时串行执行
//2020-06-08 13:29:39.415DEBUG:Thread-0com.lock.object.SynchronizedObject -m1方法获得锁
//2020-06-08 13:29:39.415DEBUG:Thread-1com.lock.object.SynchronizedObject -m2方法获得锁
//2020-06-08 13:29:40.418DEBUG:Thread-0com.lock.object.SynchronizedObject -m1方法释放锁
//2020-06-08 13:29:40.418DEBUG:Thread-1com.lock.object.SynchronizedObject -m2方法释放锁
//2020-06-08 13:29:40.418DEBUG:Thread-6com.lock.object.SynchronizedObject -m2方法获得锁
//2020-06-08 13:29:40.418DEBUG:Thread-7com.lock.object.SynchronizedObject -m1方法获得锁
//2020-06-08 13:29:41.418DEBUG:Thread-6com.lock.object.SynchronizedObject -m2方法释放锁
//2020-06-08 13:29:41.418DEBUG:Thread-7com.lock.object.SynchronizedObject -m1方法释放锁
//2020-06-08 13:29:41.418DEBUG:Thread-4com.lock.object.SynchronizedObject -m1方法获得锁
//2020-06-08 13:29:41.418DEBUG:Thread-5com.lock.object.SynchronizedObject -m2方法获得锁
//2020-06-08 13:29:42.419DEBUG:Thread-4com.lock.object.SynchronizedObject -m1方法释放锁
//2020-06-08 13:29:42.419DEBUG:Thread-5com.lock.object.SynchronizedObject -m2方法释放锁
//2020-06-08 13:29:42.419DEBUG:Thread-3com.lock.object.SynchronizedObject -m1方法获得锁
//2020-06-08 13:29:42.420DEBUG:Thread-2com.lock.object.SynchronizedObject -m2方法获得锁
//2020-06-08 13:29:43.421DEBUG:Thread-3com.lock.object.SynchronizedObject -m1方法释放锁
//2020-06-08 13:29:43.421DEBUG:Thread-2com.lock.object.SynchronizedObject -m2方法释放锁

	public static void main(String[] args) throws InterruptedException {
		SynchronizedObject syn = new SynchronizedObject();
		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task1(syn)).start();
		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task1(syn)).start();
		//结论：加锁和需要解锁的对象是关键，所需要解锁的对象不同不阻塞

		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);
	}

}
