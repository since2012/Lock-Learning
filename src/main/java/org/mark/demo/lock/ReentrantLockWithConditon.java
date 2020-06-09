package org.mark.demo.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName ReentrantLockWithConditon
 * @Description 通过条件来与线程通信，控制线程的暂停与运行
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class ReentrantLockWithConditon implements Runnable {

	public static ReentrantLock lock = new ReentrantLock(true);
	public static Condition condition = lock.newCondition();

	public static void main(String[] args) throws InterruptedException {
		ReentrantLockWithConditon test = new ReentrantLockWithConditon();
		new Thread(test).start();
		Thread.sleep(1000);
		log.debug("过了1秒后...");
		log.debug("当前线程是否持有锁：{}", lock.isHeldByCurrentThread());
		lock.lock();
		log.debug("当前线程是否持有锁：{}", lock.isHeldByCurrentThread());
		// 调用该方法前需要获取到创建该对象的锁否则会产生java.lang.IllegalMonitorStateException异常
		condition.signal();
		lock.unlock();
	}

	@Override
	public void run() {
		try {
			lock.lock();
			log.debug("-线程开始等待...");
			condition.await();
			log.debug("-线程继续进行了");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
