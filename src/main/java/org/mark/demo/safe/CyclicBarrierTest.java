package org.mark.demo.safe;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @FileName CyclicBarrierTest
 * @Description 满足条件后才开始执行相关代码
 * @Author markt
 * @Date 2019-07-31
 * @Version 1.0
 */
@Slf4j
public class CyclicBarrierTest {

	private static final int THREAD_NUM = 4;


	public static void main(String[] args) {
		/**
		 * 凑满parties则放行（该值大于corePoolSize则会出现不再执行）
		 */
		CyclicBarrier cb = new CyclicBarrier(THREAD_NUM, new Runnable() {

			// 当所有线程到达barrier时执行
			@Override
			public void run() {
				log.debug("init");
			}
		});

		ExecutorService fixedThreadPool = new ThreadPoolExecutor(4, 8,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		for (int i = 0; i < 20; i++) {
			fixedThreadPool.execute(() -> {
				try {
					log.debug(" waiting");
					// 线程在这里等待，直到所有线程都到达barrier。
					cb.await();
					log.debug(" Working");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

}