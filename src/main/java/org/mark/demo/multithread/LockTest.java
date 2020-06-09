package org.mark.demo.multithread;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @FileName LockTest
 * @Description TODO
 * @Author markt
 * @Date 2019-04-11
 * @Version 1.0
 */
class LockTest extends Template {
	ReentrantLock lock = new ReentrantLock();

	public LockTest(String _id, int _round, int _threadNum, CyclicBarrier _cb) {
		super(_id, _round, _threadNum, _cb);
	}

	/**
	 * synchronized关键字不在方法签名里面，所以不涉及重载问题
	 */
	@Override
	long getTotal() {
		try {
			lock.lock();
			return super.total;
		} finally {
			lock.unlock();
		}
	}

	@Override
	void sum() {
		try {
			lock.lock();
			super.total++;
		} finally {
			lock.unlock();
		}
	}
}