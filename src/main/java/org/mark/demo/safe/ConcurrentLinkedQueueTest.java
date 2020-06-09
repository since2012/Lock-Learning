package org.mark.demo.safe;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @FileName ConcurrentLinkedQueueTest
 * @Description TODO
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class ConcurrentLinkedQueueTest {

	public static int threadCount = 30000;
	public static CountDownLatch countDownLatch = new CountDownLatch(1);
	public static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	public static void main(String[] agrs) {

		//启动异步线程，一直添加元素
		ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 2, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>());


		for (int x = 0; x < threadCount; x++) {
			executor.execute(() -> {
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String ele = new Random().nextInt(Integer.MAX_VALUE) + "";
				log.debug("入队元素为" + ele);
				queue.offer(ele);
			});
			executor.execute(() -> {
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (queue.isEmpty()) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String ele = queue.poll();
				log.debug("出队元素为" + ele);
			});
		}
		countDownLatch.countDown();
	}

}
