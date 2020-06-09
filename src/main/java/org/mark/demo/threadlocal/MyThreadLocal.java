package org.mark.demo.threadlocal;

import lombok.extern.slf4j.Slf4j;

/**
 * @FileName MyThreadLocal
 * @Description 测试set，get,remove方法；各个线程实际操作的对象是不一样的，不无需考虑一致性问题
 * @Author markt
 * @Date 2019-04-10
 * @Version 1.0
 */
@Slf4j
public class MyThreadLocal {
	private static final ThreadLocal<Object> threadLocal = new ThreadLocal<Object>() {
		/**
		 * ThreadLocal没有被当前线程赋值时或当前线程刚调用remove方法后调用get方法，返回此方法值
		 */
		@Override
		protected Object initialValue() {
			log.debug("调用get方法时，当前线程共享变量没有设置，调用initialValue获取默认值！");
			return null;
		}
	};

	public static void main(String[] args) {
		new Thread(new MyIntegerTask()).start();
		new Thread(new MyStringTask()).start();
		new Thread(new MyIntegerTask()).start();
		new Thread(new MyStringTask()).start();
	}

	public static class MyIntegerTask implements Runnable {

		@Override
		public void run() {
			for (int i = 0; i < 5; i++) {
				// ThreadLocal.get方法获取线程变量
				if (null == threadLocal.get()) {
					// ThreadLocal.et方法设置线程变量
					threadLocal.set(0);
					log.debug("线程初始化: 0");
				} else {
					int num = (Integer) threadLocal.get();
					threadLocal.set(num + 1);
					log.debug("线程变更后: " + threadLocal.get());
					//为3时清空数据
					if (i == 3) {
						threadLocal.remove();
					}
				}


				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static class MyStringTask implements Runnable {

		@Override
		public void run() {
			for (int i = 0; i < 5; i++) {
				if (null == threadLocal.get()) {
					threadLocal.set("a");
					log.debug("线程初始化: a");
				} else {
					String str = (String) threadLocal.get();
					threadLocal.set(str + "a");
					log.debug("线程变更后: " + threadLocal.get());
				}
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}