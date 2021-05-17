package org.mark.demo.threadlocal;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @FileName ThreadLocalDemo
 * @Description ThreadLocal用法：共用一个实例，每一个线程有其对应的数据互不干扰
 * @Author markt
 * @Date 2019-04-11
 * @Version 1.0
 */
@Slf4j
public class ThreadLocalDemo {

	private static ThreadLocal<StringBuilder> THREAD_LOCAL_DATA = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder();
		}
	};

	public static void main(String[] args) throws InterruptedException {

		int threads = 3;
		CountDownLatch countDownLatch = new CountDownLatch(threads);
		for (int i = 1; i <= threads; i++) {
			new Thread(() -> {
				for (int j = 0; j < 4; j++) {
					add("" + String.valueOf(j));
				}
				set(new StringBuilder("hello world"));
				countDownLatch.countDown();
			}).start();
		}
		countDownLatch.await();

	}

	public static void add(String newStr) {
		StringBuilder str = THREAD_LOCAL_DATA.get();
		THREAD_LOCAL_DATA.set(str.append(newStr));
		print();
	}

	public static void set(StringBuilder words) {
		THREAD_LOCAL_DATA.set(words);
		print();
	}

	public static void print() {
		log.debug("ThreadLocal hashcode:{}, Instance hashcode:{}, Value:{}",
				THREAD_LOCAL_DATA.hashCode(),
				THREAD_LOCAL_DATA.get().hashCode(),
				THREAD_LOCAL_DATA.get().toString());
	}
}