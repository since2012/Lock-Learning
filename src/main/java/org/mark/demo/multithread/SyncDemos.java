package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;

/**
 * @FileName SyncDemos
 * @Description TODO
 * @Author markt
 * @Date 2020-06-09
 * @Version 1.0
 */
@Slf4j
public class SyncDemos implements Runnable {

	public synchronized void get() {
		log.debug("get data");
		set();
	}

	public synchronized void set() {
		log.debug("set data");
	}

	@Override
	public void run() {
		get();
	}

	public static void main(String[] args) {
		SyncDemos ss = new SyncDemos();
		new Thread(ss).start();
		new Thread(ss).start();
		new Thread(ss).start();
	}
}