package com.lock.object;

import lombok.extern.slf4j.Slf4j;

/**
 * 同步代码块的synchronized (this)
 *
 * @author onlyone
 */
@Slf4j
public class SynchronizedThis {

	public void m1() {
		synchronized (this) {
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
		synchronized (this) {
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

		private SynchronizedThis synchronizedThis;

		public Task1(SynchronizedThis synchronizedThis) {
			this.synchronizedThis = synchronizedThis;
		}

		@Override
		public void run() {
			synchronizedThis.m1();
		}
	}

	static class Task2 implements Runnable {

		private SynchronizedThis synchronizedThis;

		public Task2(SynchronizedThis synchronizedThis) {
			this.synchronizedThis = synchronizedThis;
		}

		@Override
		public void run() {
			synchronizedThis.m2();
		}
	}

	static class Task3 implements Runnable {

		private SynchronizedThis synchronizedThis;

		public Task3(SynchronizedThis synchronizedThis) {
			this.synchronizedThis = synchronizedThis;
		}

		@Override
		public void run() {
			synchronizedThis.m1();
		}
	}

//等效于同步方法锁，串行执行（只有一个锁，或者令牌）
//2020-06-08 13:37:13.369DEBUG:Thread-0com.lock.object.SynchronizedThis -m1方法获得锁
//2020-06-08 13:37:14.371DEBUG:Thread-0com.lock.object.SynchronizedThis -m1方法释放锁
//2020-06-08 13:37:14.371DEBUG:Thread-5com.lock.object.SynchronizedThis -m1方法获得锁
//2020-06-08 13:37:15.373DEBUG:Thread-5com.lock.object.SynchronizedThis -m1方法释放锁
//2020-06-08 13:37:15.374DEBUG:Thread-4com.lock.object.SynchronizedThis -m2方法获得锁
//2020-06-08 13:37:16.375DEBUG:Thread-4com.lock.object.SynchronizedThis -m2方法释放锁
//2020-06-08 13:37:16.375DEBUG:Thread-3com.lock.object.SynchronizedThis -m1方法获得锁
//2020-06-08 13:37:17.375DEBUG:Thread-3com.lock.object.SynchronizedThis -m1方法释放锁
//2020-06-08 13:37:17.375DEBUG:Thread-2com.lock.object.SynchronizedThis -m1方法获得锁
//2020-06-08 13:37:18.376DEBUG:Thread-2com.lock.object.SynchronizedThis -m1方法释放锁
//2020-06-08 13:37:18.377DEBUG:Thread-1com.lock.object.SynchronizedThis -m2方法获得锁
//2020-06-08 13:37:19.378DEBUG:Thread-1com.lock.object.SynchronizedThis -m2方法释放锁

	public static void main(String[] args) throws InterruptedException {
		SynchronizedThis syn = new SynchronizedThis();
		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task3(syn)).start();
		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task3(syn)).start();
		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);
	}

}
