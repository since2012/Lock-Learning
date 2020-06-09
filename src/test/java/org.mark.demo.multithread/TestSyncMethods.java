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


	//2020-06-08 17:54:38.263DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：79656646,统计值：500000
//2020-06-08 17:54:38.327DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：61975615,统计值：2000000
//2020-06-08 17:54:38.465DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：137418728,统计值：4500000
//2020-06-08 17:54:38.703DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：237840709,统计值：8000000
//2020-06-08 17:54:39.044DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：339738749,统计值：12500000
	@Test
	public void testSync() {
		for (int i = 1; i <= 5; i++) {
			int round = 100000 * i;
			int threadNum = 5 * i;
			CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum * 2 + 1);
			new SyncTest("Sync", round, threadNum, cyclicBarrier).testTime();
		}
	}

	//2020-06-08 17:53:27.590DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：60427572,统计值：500000
//2020-06-08 17:53:27.646DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：51775131,统计值：2000000
//2020-06-08 17:53:27.755DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：108507453,统计值：4500000
//2020-06-08 17:53:27.946DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：191543930,统计值：8000000
//2020-06-08 17:53:28.257DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：309441985,统计值：12500000
	@Test
	public void testLock() {
		for (int i = 1; i <= 5; i++) {
			int round = 100000 * i;
			int threadNum = 5 * i;
			CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum * 2 + 1);
			new LockTest("Lock", round, threadNum, cyclicBarrier).testTime();
		}
	}

	//2020-06-08 17:56:02.432DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：68395515,统计值：500000
//2020-06-08 17:56:02.462DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：26160449,统计值：2000000
//2020-06-08 17:56:02.543DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：80606045,统计值：4500000
//2020-06-08 17:56:02.689DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：145937933,统计值：8000000
//2020-06-08 17:56:02.921DEBUG:%PARSER_ERROR[value]org.mark.demo.multithread.Template -耗时：231478658,统计值：12500000
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