package org.mark.demo.multithread;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CyclicBarrier;

/**
 * @FileName TestSyncMethods
 * @Description TODO
 * @Author markt
 * @Date 2019-04-11
 * @Version 1.0
 */
@Slf4j
public class TestSyncMethods {


//2021-05-10 14:27:23.719 DEBUG: main org.mark.demo.multithread.Template - 耗时：97,统计值：500000
//2021-05-10 14:27:23.784 DEBUG: main org.mark.demo.multithread.Template - 耗时：62,统计值：2000000
//2021-05-10 14:27:23.913 DEBUG: main org.mark.demo.multithread.Template - 耗时：129,统计值：4500000
//2021-05-10 14:27:24.150 DEBUG: main org.mark.demo.multithread.Template - 耗时：237,统计值：8000000
//2021-05-10 14:27:24.515 DEBUG: main org.mark.demo.multithread.Template - 耗时：365,统计值：12500000
@Test
public void testSync() {
	for (int i = 1; i <= 5; i++) {
		int round = 100000 * i;
		int threadNum = 5 * i;
		CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum * 2 + 1);
		new SyncTest("Sync", round, threadNum, cyclicBarrier).testTime();
	}
}

	//2021-05-10 14:26:49.459 DEBUG: main org.mark.demo.multithread.Template - 耗时：78,统计值：500000
//2021-05-10 14:26:49.514 DEBUG: main org.mark.demo.multithread.Template - 耗时：51,统计值：2000000
//2021-05-10 14:26:49.632 DEBUG: main org.mark.demo.multithread.Template - 耗时：118,统计值：4500000
//2021-05-10 14:26:49.832 DEBUG: main org.mark.demo.multithread.Template - 耗时：200,统计值：8000000
//2021-05-10 14:26:50.146 DEBUG: main org.mark.demo.multithread.Template - 耗时：314,统计值：12500000
	@Test
	public void testLock() {
		for (int i = 1; i <= 5; i++) {
			int round = 100000 * i;
			int threadNum = 5 * i;
			CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum * 2 + 1);
			new LockTest("Lock", round, threadNum, cyclicBarrier).testTime();
		}
	}

	//2021-05-10 14:28:13.017 DEBUG: main org.mark.demo.multithread.Template - 耗时：65,统计值：500000
//2021-05-10 14:28:13.047 DEBUG: main org.mark.demo.multithread.Template - 耗时：27,统计值：2000000
//2021-05-10 14:28:13.126 DEBUG: main org.mark.demo.multithread.Template - 耗时：79,统计值：4500000
//2021-05-10 14:28:13.286 DEBUG: main org.mark.demo.multithread.Template - 耗时：160,统计值：8000000
//2021-05-10 14:28:13.454 DEBUG: main org.mark.demo.multithread.Template - 耗时：168,统计值：12500000
	@Test
	public void testAtomic() {
		for (int i = 1; i <= 5; i++) {
			int round = 100000 * i;
			int threadNum = 5 * i;
			CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum * 2 + 1);
			new AtomicTest("Atom", round, threadNum, cyclicBarrier).testTime();
		}
	}
}