package com.lock.class1;

import lombok.extern.slf4j.Slf4j;

/**
 * synchronized修饰静态方法
 * 静态方法为类的方法，锁定静态方法等效于锁定类
 * 即同一类的所有同步静态方法公用一把锁
 *
 * @author onlyone
 */
@Slf4j
public class SynchronizedStaticMethod {

	public synchronized static void m1() {
		log.debug("m1方法获得锁");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("m1方法释放锁");
	}

	public synchronized static void m2() {
		log.debug("m2方法获得锁");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("m2方法释放锁");
	}

	static class Task1 implements Runnable {

		@Override
		public void run() {
			SynchronizedStaticMethod.m1();
		}
	}

	static class Task2 implements Runnable {

		@Override
		public void run() {
			SynchronizedStaticMethod.m2();
		}
	}

//2020-06-08 13:42:46.362DEBUG:Thread-0com.lock.class1.SynchronizedStaticMethod -m1方法获得锁
//2020-06-08 13:42:47.364DEBUG:Thread-0com.lock.class1.SynchronizedStaticMethod -m1方法释放锁
//2020-06-08 13:42:47.365DEBUG:Thread-5com.lock.class1.SynchronizedStaticMethod -m2方法获得锁
//2020-06-08 13:42:48.365DEBUG:Thread-5com.lock.class1.SynchronizedStaticMethod -m2方法释放锁
//2020-06-08 13:42:48.365DEBUG:Thread-4com.lock.class1.SynchronizedStaticMethod -m1方法获得锁
//2020-06-08 13:42:49.367DEBUG:Thread-4com.lock.class1.SynchronizedStaticMethod -m1方法释放锁
//2020-06-08 13:42:49.367DEBUG:Thread-3com.lock.class1.SynchronizedStaticMethod -m2方法获得锁
//2020-06-08 13:42:50.369DEBUG:Thread-3com.lock.class1.SynchronizedStaticMethod -m2方法释放锁
//2020-06-08 13:42:50.370DEBUG:Thread-2com.lock.class1.SynchronizedStaticMethod -m1方法获得锁
//2020-06-08 13:42:51.371DEBUG:Thread-2com.lock.class1.SynchronizedStaticMethod -m1方法释放锁
//2020-06-08 13:42:51.371DEBUG:Thread-1com.lock.class1.SynchronizedStaticMethod -m2方法获得锁
//2020-06-08 13:42:52.372DEBUG:Thread-1com.lock.class1.SynchronizedStaticMethod -m2方法释放锁

	public static void main(String[] args) throws InterruptedException {

		new Thread(new Task1()).start();
		new Thread(new Task2()).start();
		new Thread(new Task1()).start();
		new Thread(new Task2()).start();
		new Thread(new Task1()).start();
		new Thread(new Task2()).start();

		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);
	}

}
