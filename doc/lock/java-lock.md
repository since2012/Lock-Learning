## 锁
---

并发 (Concurrency)：一个处理器“同时”处理多个任务
并行 (Parallelism)：多个处理器 “同时”处理多个任务

#### 常见锁类型：

1.互斥锁（Mutex）

* 同步块 synchronized block

* 对象锁 object.readLock()

* 可重入锁

可重入锁，也叫做递归锁，指的是同一线程外层函数获得锁之后 ，内层递归函数仍然有获取该锁的代码，但不受影响。ReentrantLock 和synchronized 都是 可重入锁。

在lock函数内，应验证线程是否为已经获得锁的线程。当unlock（）第一次调用时，实际上不应释放锁。（采用计数进行统计）

可重入锁最大的特点是避免死锁。

2.信号量（Semaphore）

* 公平和非公平

3.乐观锁（CAS）

* ABA问题：无锁堆栈实现
