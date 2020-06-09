package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

/**
 * @FileName VolatileTest
 * @Description 引入问题：累计数据总数错误
 * 自加操作粗略分为：读取，计算，写入
 * 在一个线程写入之前已被另一个线程改写了值，导致累计值丢失
 * @Author markt
 * @Date 2019-04-12
 * @Version 1.0
 */
@Slf4j
public class VolatileTest {

	private static final int threadCount = 10;
	private static volatile int total = 0;

	public static void increase() {
		total++;
	}

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				for (int j = 0; j < 2000; j++) {
					increase();
				}
			}).start();
		}

		// 主线程阻塞，防止jvm提早退出
		Thread.sleep(20000);
		log.debug("最终计算结果{}", total);
	}
}
