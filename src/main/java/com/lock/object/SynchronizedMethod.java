package com.lock.object;

import lombok.extern.slf4j.Slf4j;

/**
 * synchronized修饰非静态方法
 * 不同的线程调用同一对象的不同同步方法
 * 因为锁只有一把，造成事实上的串行化（8个线程）
 *
 * @author onlyone
 */
@Slf4j
public class SynchronizedMethod {

	public synchronized void m1() {
		log.debug("m1方法获得锁");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("m1方法释放锁");
	}

	public synchronized void m2() {
		log.debug("m2方法获得锁");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("m2方法释放锁");
	}

	static class Task1 implements Runnable {

		private SynchronizedMethod synchronizedMethod;

		public Task1(SynchronizedMethod synchronizedMethod) {
			this.synchronizedMethod = synchronizedMethod;
		}

		@Override
		public void run() {
			synchronizedMethod.m1();
		}
	}

	static class Task2 implements Runnable {

		private SynchronizedMethod synchronizedMethod;

		public Task2(SynchronizedMethod synchronizedMethod) {
			this.synchronizedMethod = synchronizedMethod;
		}

		@Override
		public void run() {
			synchronizedMethod.m2();
		}
	}

//执行顺序与线程创建顺序无关，串行执行
//2020-06-08 13:23:31.205DEBUG:Thread-0com.lock.object.SynchronizedMethod -m1方法获得锁
//2020-06-08 13:23:32.211DEBUG:Thread-0com.lock.object.SynchronizedMethod -m1方法释放锁
//2020-06-08 13:23:32.212DEBUG:Thread-7com.lock.object.SynchronizedMethod -m1方法获得锁
//2020-06-08 13:23:33.213DEBUG:Thread-7com.lock.object.SynchronizedMethod -m1方法释放锁
//2020-06-08 13:23:33.213DEBUG:Thread-5com.lock.object.SynchronizedMethod -m2方法获得锁
//2020-06-08 13:23:34.215DEBUG:Thread-5com.lock.object.SynchronizedMethod -m2方法释放锁
//2020-06-08 13:23:34.215DEBUG:Thread-6com.lock.object.SynchronizedMethod -m2方法获得锁
//2020-06-08 13:23:35.216DEBUG:Thread-6com.lock.object.SynchronizedMethod -m2方法释放锁
//2020-06-08 13:23:35.216DEBUG:Thread-4com.lock.object.SynchronizedMethod -m1方法获得锁
//2020-06-08 13:23:36.218DEBUG:Thread-4com.lock.object.SynchronizedMethod -m1方法释放锁
//2020-06-08 13:23:36.219DEBUG:Thread-3com.lock.object.SynchronizedMethod -m1方法获得锁
//2020-06-08 13:23:37.219DEBUG:Thread-3com.lock.object.SynchronizedMethod -m1方法释放锁
//2020-06-08 13:23:37.219DEBUG:Thread-2com.lock.object.SynchronizedMethod -m2方法获得锁
//2020-06-08 13:23:38.221DEBUG:Thread-2com.lock.object.SynchronizedMethod -m2方法释放锁
//2020-06-08 13:23:38.222DEBUG:Thread-1com.lock.object.SynchronizedMethod -m2方法获得锁
//2020-06-08 13:23:39.223DEBUG:Thread-1com.lock.object.SynchronizedMethod -m2方法释放锁

	public static void main(String[] args) throws InterruptedException {
		SynchronizedMethod syn = new SynchronizedMethod();

		//两个线程再调用同一个对象加锁方法
		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task1(syn)).start();

		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task1(syn)).start();

		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);
	}
}
