### jdk并发包里常用的类

* ConcurrentHashMap

	是线程安全的。
	
	Put方法，首先是对key.hashCode进行hash操作，得到hash值。然后获取对应的segment对象，接着调用Segment对象的put方法完成当前操作。当调用put方法时，首先lock操作，完成操作后再释放锁。
	
	http://ifeve.com/concurrenthashmap/

* Semaphore

	可以控制某资源同时被访问的个数。例如连接池中通常要控制创建连接的个数。
	
	tryAcquire方法，获得锁<br>
	release方法，释放锁

* CountdownLatch

	闭锁，确保一个服务不会开始，直到它依赖的其他服务都已近开始，它允许一个或多个线程，等待一个事件集的发生。
	通过减计数的方式，控制多个线程同时开始某个动作。当计数为0时，await后的代码才会被执行。
    提供await（）和countDown（）两个方法。

* CyclicBarrier

	cyclicBarrier中的await方法会对count值减1，并阻塞当前线程（java.util.concurrent.locks.Condition.await()），如果count==0时先执行CyclicBarrier内部的Runnable任务（java.lang.Runnable.run()），然后唤醒所有阻塞的线程（java.util.concurrent.locks.Condition.signalAll()），count恢复初始值（可以进入下一轮循环）。
	
	与CountdownLatch不同的是，它可以循环重用。

CyclicBarrierTest

* AtomicInteger

	原子操作，线程安全。之前如果多线程累计计数，需要通过锁控制。
IncrementAndGet方法，关键是调用了compareAndSwap方法，是native方法，基于cpu的**CAS原语来实现**的。简单原理是由cpu比较内存位置上的值是否为当前值，如果是换成新值，否则返回false

* FutureTask

	用于异步获取执行结果或取消执行任务。通过传入Callable给FutureTask，直接调用run方法执行，之后可以通过FutureTask的get异步方法获得执行结果。FutureTask即使多次调用了run方法，它只会执行一次Callable任务，当然也可以通过cancel来取消执行。
《分布式java应用》P158

* ArrayBlockingQueue

	基于数组、先进先出、线程安全的集合

* CopyOnWriteArrayList

	线程安全，读操作时无锁的ArrayList。每次新增一个对象时，会将创建一个新的数组（长度+1），将之前的数组中的内容复制到新的数组中，并将新增的对象放入数组末尾。最后做引用切换。
	
* CopyOnWriteArraySet

	与上面的类似，无非在add时，会调用addIfAbsent，由于每次add时都要进行数组遍历，因此性能会略低于CopyOnWriteArrayList

* ReentrantLock

	单锁。控制并发的，和synchronized达到的效果是一致的。
Lock方法，借助于CAS机制来控制锁。Unlock方法，释放锁

* ReentrantReadWriteLock

	与ReentrantLock没有任何继承关系，提供了读锁和写锁，在读多写少的场景中大幅度提升性能。
	
	持有读锁时，不能直接调用写锁的lock方法<br>
	持有写锁时，其他线程的读或写都会被阻塞。

* 如何避免死锁

	1.制定锁的顺序，来避免死锁（先A后B，避免A->B和B->A同时存在）；
	
	2.尝试使用定时锁（readLock.tryLock(timeout)）
	
	3.在持有锁的方法中进行其他方法的调用，尽量使用开放调用（当调用方法不需要持有锁时，叫做开放调用）
	
	4.减少锁的持有时间、减小锁代码块的粒度。



