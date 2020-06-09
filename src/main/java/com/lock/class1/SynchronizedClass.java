package com.lock.class1;


import lombok.extern.slf4j.Slf4j;

/**
 * synchronized (类.class)
 * 所需获取的令牌只有1个，非此即彼
 * 
 * @author onlyone
 */
@Slf4j
public class SynchronizedClass {

    public void m1() {
        synchronized (SynchronizedClass.class) {
			log.debug("m1方法获得锁");
            try {
				Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
			log.debug("m1方法释放锁");
        }
    }

    public void m2() {
        synchronized (SynchronizedClass.class) {
			log.debug("m2方法获得锁");
            try {
				Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
			log.debug("m2方法释放锁");

        }
    }

    static class Task1 implements Runnable {

        private SynchronizedClass synchronizedClass;

        public Task1(SynchronizedClass synchronizedClass){
            this.synchronizedClass = synchronizedClass;
        }

        @Override
        public void run() {
            synchronizedClass.m1();
        }
    }

    static class Task2 implements Runnable {

        private SynchronizedClass synchronizedClass;

        public Task2(SynchronizedClass synchronizedClass){
            this.synchronizedClass = synchronizedClass;
        }

        @Override
        public void run() {
            synchronizedClass.m2();
        }
    }

//6个线程运行6秒，串行执行
//2020-06-08 13:40:45.171DEBUG:Thread-0com.lock.class1.SynchronizedClass -m1方法获得锁
//2020-06-08 13:40:46.174DEBUG:Thread-0com.lock.class1.SynchronizedClass -m1方法释放锁
//2020-06-08 13:40:46.174DEBUG:Thread-5com.lock.class1.SynchronizedClass -m2方法获得锁
//2020-06-08 13:40:47.175DEBUG:Thread-5com.lock.class1.SynchronizedClass -m2方法释放锁
//2020-06-08 13:40:47.176DEBUG:Thread-4com.lock.class1.SynchronizedClass -m1方法获得锁
//2020-06-08 13:40:48.179DEBUG:Thread-4com.lock.class1.SynchronizedClass -m1方法释放锁
//2020-06-08 13:40:48.179DEBUG:Thread-3com.lock.class1.SynchronizedClass -m2方法获得锁
//2020-06-08 13:40:49.180DEBUG:Thread-3com.lock.class1.SynchronizedClass -m2方法释放锁
//2020-06-08 13:40:49.180DEBUG:Thread-2com.lock.class1.SynchronizedClass -m1方法获得锁
//2020-06-08 13:40:50.181DEBUG:Thread-2com.lock.class1.SynchronizedClass -m1方法释放锁
//2020-06-08 13:40:50.181DEBUG:Thread-1com.lock.class1.SynchronizedClass -m2方法获得锁
//2020-06-08 13:40:51.181DEBUG:Thread-1com.lock.class1.SynchronizedClass -m2方法释放锁

    public static void main(String[] args) throws InterruptedException {
        SynchronizedClass syn = new SynchronizedClass();
        new Thread(new Task1(syn)).start();
        new Thread(new Task2(syn)).start();
		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
		new Thread(new Task1(syn)).start();
		new Thread(new Task2(syn)).start();
        // 主线程阻塞，防止jvm提早退出
		Thread.sleep(15000);
    }

}
