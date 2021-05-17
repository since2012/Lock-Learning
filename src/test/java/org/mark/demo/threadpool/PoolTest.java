package org.mark.demo.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * @FileName PoolTest
 * @Description 线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor的方法，这样的处理可以更加明白线程池的运行规则，规避
 * 资源耗尽的风险，这几个方法的弊端
 * 1）newFixedThreadPool和newSingleThreadExecutor:堆积的请求处理队列可能会消耗非常大的内存，甚至OOM
 * 2）newCachedThreadPool和newScheduledThreadPool:线程最大数时Integer.MAX_VALUE,可能创建数量非常多的线程，甚至OOM
 * 总结下来：线程过少导致处理队列过长；线程过多也会出现问题
 * 解决方案：根据情况灵活地配置参数，即直接手动生成ThreadPoolExecutor
 * @Author markt
 * @Date 2019-04-15
 * @Version 1.0
 */
@Slf4j
public class PoolTest {

	/**
	 * 可缓存线程池(对比注释前后的线程数量)
	 */
	@Test
	public void testCache() {

		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		for (int i = 0; i < 1000; i++) {
			final int index = i;

			cachedThreadPool.execute(() -> {
				log.debug("{}", index);
			});
		}

		try {
			new CountDownLatch(1).await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCacheLatch() {

		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		CountDownLatch countDownLatch = new CountDownLatch(1);
		for (int i = 0; i < 1000; i++) {
			final int index = i;

			cachedThreadPool.execute(() -> {
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.debug("{}", index);
			});
		}

		countDownLatch.countDown();
		try {
			new CountDownLatch(1).await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 定长线程池
	 */
	@Test
	public void testFixed() {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(6);
		for (int i = 0; i < 10; i++) {
			final int index = i;
			fixedThreadPool.execute(() -> {
				try {
					log.debug("{}", index);
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
		while (Thread.activeCount() > 1) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 单线程
	 */
	@Test
	public void testSingle() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (int i = 0; i < 10; i++) {
			singleThreadExecutor.execute(() -> {
				try {
					log.debug(LocalDateTime.now().format(formatter));
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}

		while (Thread.activeCount() > 1) {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 测试延时任务
	 */
	@Test
	public void testDelay() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		log.debug(LocalDateTime.now().format(formatter));

		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(4);
		for (int i = 0; i < 10; i++) {
			scheduledThreadPool.schedule(() -> {
				log.debug(LocalDateTime.now().format(formatter));
			}, 3, TimeUnit.SECONDS);
		}
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试定时周期任务
	 * corePoolSize小于所需线程数时，正在执行的数量以corePoolSize为准
	 */
	@Test
	public void testSchedule() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(4);
		for (int i = 0; i < 10; i++) {
			scheduledThreadPool.scheduleAtFixedRate(() -> {
				log.debug(LocalDateTime.now().format(formatter));
			}, 1, 1, TimeUnit.SECONDS);
		}
		try {
			Thread.sleep(18000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
