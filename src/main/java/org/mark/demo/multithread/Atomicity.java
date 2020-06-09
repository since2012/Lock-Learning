package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

/**
 * @FileName Atomicity
 * @Description TODO
 * @Author markt
 * @Date 2019-05-21
 * @Version 1.0
 */
@Slf4j
public class Atomicity {
	//静态变量t
	public static long value = 0;

	//静态变量t的get方法,同步方法
	public synchronized static long getValue() {
		return value;
	}

	//静态变量t的set方法，同步方法
	public synchronized static void setValue(long value) {
		Atomicity.value = value;
	}

	//改变变量t的线程
	public static class ChangeValue implements Runnable {
		private long to;

		public ChangeValue(long to) {
			this.to = to;
		}

		@Override
		public void run() {
			//不断的将long变量设值到 t中
			while (true) {
				Atomicity.setValue(to);
//				log.debug("设置为{}", to);
				//将当前线程的执行时间片段让出去，以便由线程调度机制重新决定哪个线程可以执行
				Thread.yield();
			}
		}
	}

	//读取变量t的线程，若读取的值和设置的值不一致，说明变量t的数据被破坏了，即线程不安全
	public static class ReadValue implements Runnable {

		@Override
		public void run() {
			//不断的读取Atomicity的t的值
			while (true) {
				long tmp = Atomicity.getValue();
//				log.debug("当前值为：", tmp);
				//比较是否是自己设值的其中一个
				if (tmp != 100L && tmp != 200L && tmp != -300L && tmp != -400L) {
					//程序若执行到这里，说明long类型变量t，其数据已经被破坏了
					log.error("数据出现破坏：{}", tmp);
				}
				////将当前线程的执行时间片段让出去，以便由线程调度机制重新决定哪个线程可以执行
				Thread.yield();
			}
		}
	}

	public static void main(String[] args) {
		new Thread(new ChangeValue(100L)).start();
		new Thread(new ChangeValue(200L)).start();
		new Thread(new ChangeValue(-300L)).start();
		new Thread(new ChangeValue(-400L)).start();
		new Thread(new ReadValue()).start();
	}
}
