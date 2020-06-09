package org.mark.demo.threadlocal;

import lombok.extern.slf4j.Slf4j;

/**
 * @FileName ThreadLocalDemo
 * @Description 可继承ThreadLocal支持子线程访问父线程的数据
 * @Author markt
 * @Date 2019-04-11
 * @Version 1.0
 */
@Slf4j
public class InheritableThreadLocalDemo {

	public static ThreadLocal<Integer> threadLocal = new InheritableThreadLocal<Integer>();

	public static void main(String args[]) {
		threadLocal.set(new Integer(123));

		new MyThread().start();

		log.debug("{}", threadLocal.get());
	}

	static class MyThread extends Thread {
		@Override
		public void run() {
			log.debug("{}", threadLocal.get());
		}
	}

}