package com.lock.condition;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 条件变量Condition锁
 *
 * @author onlyone
 */
@Slf4j
public class ConditionDemo {

	private ReentrantLock lock = new ReentrantLock();
	private Condition take = lock.newCondition();
	private Condition put = lock.newCondition();

	private BlockingQueue queue = new ArrayBlockingQueue<>(16);
	// 队列的最大容量
	private int capacity = 5;
	// 初始值
	private int i = 1;

	/**
	 * 往队列里面插入数据
	 */
	public void put() {
		log.debug(String.format("【put】total=%d 获取锁!!!", i));
		lock.lock();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			while (queue.size() == capacity) {
				log.debug(String.format("【put】插入 %d 时队列满，执行put.await！队列值：【%s】", i,
						objectMapper.writeValueAsString(queue.toArray())));
				put.await();
			}

			queue.put(i);
			log.debug(String.format("【put】插入 %d 到队列", i));
			i++;

			// 读操作唤醒
			take.signal();
		} catch (Exception e) {
		} finally {
			lock.unlock();
			log.debug(String.format("【put】total=%d 释放锁!!!", i - 1));
		}
	}

	/**
	 * 从队列读数据
	 */
	public void take() {
		log.debug("【take】获取锁！");

		int data = 0;
		lock.lock();
		try {
			while (queue.size() == 0) {
				log.debug("【take】执行take.await！");
				take.await();
			}
			data = (int) queue.take();
			ObjectMapper objectMapper = new ObjectMapper();
			String s = objectMapper.writeValueAsString(queue);
			log.debug(String.format("【take】从队列读取值 %d ,队列值：【%s】", data, s));

			// 写操作唤醒
			put.signal();
		} catch (Exception e) {
		} finally {
			lock.unlock();
			log.debug(String.format("【take】total=%d 释放锁!!!", data));
		}
	}
}
