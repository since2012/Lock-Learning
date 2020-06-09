package org.mark.demo.multithread;

import java.util.concurrent.CyclicBarrier;

/**
 * @FileName SyncTest
 * @Description synchronized测试
 * @Author markt
 * @Date 2019-04-11
 * @Version 1.0
 */
class SyncTest extends Template {
	public SyncTest(String id, int round, int threadNum, CyclicBarrier barrier) {
		super(id, round, threadNum, barrier);
	}

	/**
	 * synchronized关键字不在方法签名里面，所以不涉及重载问题
	 */
	@Override
	synchronized long getTotal() {
		return super.total;
	}

	@Override
	synchronized void sum() {
		super.total++;
	}
}