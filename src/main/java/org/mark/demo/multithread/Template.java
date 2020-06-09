package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @FileName Template
 * @Description TODO
 * @Author markt
 * @Date 2019-04-11
 * @Version 1.0
 */
@Slf4j
public abstract class Template {

	private String id;

	protected int round;
	private int threadNum;
	protected long total;

	protected AtomicLong totalAtmoic = new AtomicLong(0);

	//任务栅栏，同批任务，先到达wait的任务挂起，一直等到全部任务到达制定的wait地点后，才能全部唤醒，继续执行
	private CyclicBarrier barrier;

	public Template(String id, int round, int threadNum, CyclicBarrier cb) {
		this.id = id;
		this.round = round;
		this.threadNum = threadNum;
		barrier = cb;
	}

	abstract void sum();

	/*
	 * 对long的操作是非原子的，原子操作只针对32位
	 * long是64位，底层操作的时候分2个32位读写，因此不是线程安全
	 */
	abstract long getTotal();

	public void testTime() {
		ExecutorService se = Executors.newCachedThreadPool();
		long start = System.nanoTime();
		//同时开启2*ThreadNum个数的读写线程
		for (int i = 0; i < threadNum; i++) {
			se.execute(() -> {
						for (int j = 0; j < round; j++) {
							sum();
						}

						try {
							//每个线程执行完同步方法后就等待
							barrier.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (BrokenBarrierException e) {
							e.printStackTrace();
						}

					}
			);
			se.execute(() -> {
						getTotal();
						try {
							//每个线程执行完同步方法后就等待
							barrier.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (BrokenBarrierException e) {
							e.printStackTrace();
						}

					}
			);
		}

		try {
			//当前统计线程也wait,所以CyclicBarrier的初始值是threadNum*2+1
			barrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		//所有线程执行完成之后，才会跑到这一步
		long duration = System.nanoTime() - start;
//		log.debug("round total:{}； thread total:{},", round, threadNum);
//		log.debug("耗时：{},统计值：{}", duration, total);
		log.debug("耗时：{},统计值：{}", duration, totalAtmoic.get());

	}
}