package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;

/**
 * @FileName AtomicTest
 * @Description TODO
 * @Author markt
 * @Date 2019-04-11
 * @Version 1.0
 */
@Slf4j
public class AtomicTest extends Template {

	public AtomicTest(String _id, int _round, int _threadNum, CyclicBarrier _cb) {
		super(_id, _round, _threadNum, _cb);
	}

	@Override
	/**
	 * synchronized关键字不在方法签名里面，所以不涉及重载问题
	 */
	long getTotal() {
		return super.totalAtmoic.get();
	}

	@Override
	void sum() {
		super.totalAtmoic.getAndIncrement();
	}
}