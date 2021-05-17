package org.mark.demo.safe;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @FileName LinkedBlockingQueueTest
 * @Description TODO
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class LinkedBlockingQueueTest {

	public static void main(String[] args) throws InterruptedException {

		LinkedBlockingQueue<Apple> queue = new LinkedBlockingQueue<Apple>();

		CountDownLatch countDownLatch = new CountDownLatch(1);

		//启动异步线程，一直添加元素
		ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 2, TimeUnit.MINUTES,
				new LinkedBlockingQueue<>(20), new ThreadPoolExecutor.AbortPolicy());


		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.debug("生产者就绪");

				try {
					log.debug("生产红苹果");
					queue.put(new Apple("红色"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}

		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.debug("消费者就绪");
				Apple apple = null;
				try {
					apple = queue.take();
					log.debug("消费" + apple.getColor() + "的苹果");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}

		countDownLatch.countDown();
		Thread.sleep(10000);
	}

}