## ReentrantLock重入锁

在JDK5.0版本之前，重入锁的性能远远好于synchronized关键字，JDK6.0版本之后synchronized 得到了大量的优化，二者性能也不分伯仲，但是重入锁是可以完全替代synchronized关键字的。除此之外，重入锁又自带一系列高逼格UBFF：可中断响应、锁申请等待限时、公平锁。另外可以结合Condition来使用，使其更是逼格满满。

### 简介

ReentrantLock（重入锁）就是支持可重进入的锁，它表示该锁能支持一个线程对资源的重复加锁。另外还支持获取锁的公平和非公平选择ReentrantLock的实现不仅可以替代隐式的synchronized关键字，而且还能够提供超过关键字本身的多种功能。

### 公平与非公平

这个概念是针对锁的获取的，在绝对时间上，先对锁进行获取的请求一定先满足，那么这个锁是公平的，反之就是不公平的。公平锁的获取就是等待时间最长的线程最先获取锁，也就是锁获取是顺序的。但是公平锁的机制往往效率不高。

###基本用法
ReentrantLockTest

从上可以看出，使用重入锁进行加锁是一种显式操作，通过何时加锁与释放锁使重入锁对逻辑控制的灵活性远远大于synchronized关键字。同时，需要注意，有加锁就必须有释放锁，而且加锁与释放锁的分数要相同，这里就引出了“重”字的概念，如上边代码演示，放开①、②处的注释，与原来效果一致。

###高级用法
- 中断响应
对于synchronized块来说，要么获取到锁执行，要么持续等待。而重入锁的中断响应功能就合理地避免了这样的情况。比如，一个正在等待获取锁的线程被“告知”无须继续等待下去，就可以停止工作了。直接上代码，来演示使用重入锁如何解决死锁：
KillDeadlock

t1、t2线程开始运行时，会分别持有lock1和lock2而请求lock2和lock1，这样就发生了死锁。但是，在③处给t2线程状态标记为中断后，持有重入锁lock2的线程t2会响应中断，并不再继续等待lock1，同时释放了其原本持有的lock2，这样t1获取到了lock2，正常执行完成。t2也会退出，但只是释放了资源并没有完成工作。

- 锁申请等待限时
可以使用 tryLock()或者tryLock(long timeout, TimeUtil unit) 方法进行一次限时的锁等待。

前者不带参数，这时线程尝试获取锁，如果获取到锁则继续执行，如果锁被其他线程持有，则立即返回 false ，也就是不会使当前线程等待，所以不会产生死锁。 
后者带有参数，表示在指定时长内获取到锁则继续执行，如果等待指定时长后还没有获取到锁则返回false。

TryLockTest
 * 运行结果:
 * 线程2获取锁失败！
    上述示例中，t1先获取到锁，并休眠2秒，这时t2开始等待，等待1秒后依然没有获取到锁，就不再继续等待，符合预期结果。

- 公平锁
所谓公平锁，就是按照时间先后顺序，使先等待的线程先得到锁，而且，公平锁不会产生饥饿锁，也就是只要排队等待，最终能等待到获取锁的机会。使用重入锁（默认是非公平锁）创建公平锁：
```
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```
FairLockTest

- ReentrantLock 配合 Conditond 使用
配合关键字synchronized使用的方法如：await()、notify()、notifyAll()，同样配合ReentrantLock 使用的Conditon提供了以下方法：
```
public interface Condition {
    void await() throws InterruptedException; // 类似于Object.wait()
    void awaitUninterruptibly(); // 与await()相同，但不会再等待过程中响应中断
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    boolean await(long time, TimeUnit unit) throws InterruptedException;
    boolean awaitUntil(Date deadline) throws InterruptedException;
    void signal(); // 类似于Obejct.notify()
    void signalAll();
}
```
ReentrantLock 实现了Lock接口，可以通过该接口提供的newCondition()方法创建Condition对象：

```
public interface Lock {
    void readLock();
    void lockInterruptibly() throws InterruptedException;
    boolean tryLock();
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    void unlock();
    Condition newCondition();
}
```
ReentrantLockWithConditon


### 调用过程

ReentrantLock把所有Lock接口的操作都委派到一个sync类上，该类继承了队列同步器：

```java
static abstract class Sync extends AbstractQueuedSynchronizer 
final static class NonfairSync extends Sync
final static class FairSync extends Sync
```

1、sync有两个子类，分别实现的是公平锁和非公平锁，默认为非公平锁。

2、由于该同步组件的实现使用的模板方法模式，我们看下ReentrantLock.readLock()方法的调用过程（**默认为非公平锁**）

### 非公平锁语义

```java
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum readLock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```

1、首先判断锁当前的状态，如果锁空闲，则当前线程尝试获取。

2、当前线程获取锁成功，则设置当前线程为锁的拥有者。

3、如果锁状态为非空闲状态且当前线程为锁的拥有者，则直接将同步状态值进行增加并返回true，表示获取同步状态成功。

**这里就是支持锁重入的场景，跟synchronized类似：成功获取锁的线程再次获取锁，只是增加了同步状态值，与此对应的是,在释放锁的时候需要对应减少同步状态值，直到同步状态值为0的时候，才表示这把锁真正的被释放了。**

```Java
protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
```

### 公平锁语义

```java
 protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (isFirst(current) &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum readLock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
```

**这里与非公平锁大部分地方类似，只是线程在获取同步状态时，需要判断当前节点的前驱节点是否为首节点。公平锁总是按照队列的顺序来来获取锁的。**

### 使用场景

1、发现该操作已经在执行中则不再执行

a、用在定时任务时，如果任务执行时间可能超过下次计划执行时间，确保该任务只有一个正在执行，忽略重复触发。

b、用在界面交互时点击执行较长时间请求操作时，防止多次点击导致后台重复执行。

主要用于进行非重要任务时防止重复执行。

```Java
private ReentrantLock readLock = new ReentrantLock();
    
    public void test()
    {
        if(readLock.tryLock())
        {
            try
            {
               //dosomothing 
            }
           finally
           {
               readLock.unlock();
           }
        }
    }
```

#### 2、如果该操作已经在执行，则等待一个个执行（同步执行，与synchronized类似）

#### 3、如果该操作已经在执行，则尝试等待一段时间，超时则放弃执行

```java
 if(readLock.tryLock(5, TimeUnit.SECONDS))
        {
            try
            {
               //dosomothing 
            }
           finally
           {
               readLock.unlock();
           }
        }
```

这种其实属于场景2的改进，等待获得锁的操作有一个时间的限制，如果超时则放弃执行。
用来防止由于资源处理不当长时间占用导致死锁情况（大家都在等待资源，导致线程队列溢出）。

#### 场景4：如果发现该操作已经在执行，等待执行。这时可中断正在进行的操作立刻释放锁继续下一操作。

synchronized与Lock在默认情况下是不会响应中断(interrupt)操作，会继续执行完。lockInterruptibly()提供了可中断锁来解决此问题。（场景2的另一种改进，没有超时，只能等待中断或执行完毕）

## 读写锁ReentrantReadWriteLock

现实中有这样一种场景：对共享资源有读和写的操作，且写操作没有读操作那么频繁。在没有写操作的时候，多个线程同时读一个资源没有任何问题，所以应该允许多个线程同时读取共享资源；但是如果一个线程想去写这些共享资源，就不应该允许其他线程对该资源进行读和写的操作了。

　　针对这种场景，**JAVA的并发包提供了读写锁ReentrantReadWriteLock，它表示两个锁，一个是读操作相关的锁，称为共享锁；一个是写相关的锁，称为排他锁**，描述如下：

线程进入读锁的前提条件：
    没有其他线程的写锁，
    没有写请求或者**有写请求，但调用线程和持有锁的线程是同一个**

线程进入写锁的前提条件：
    没有其他线程的读锁
    没有其他线程的写锁

而读写锁有以下三个重要的特性：

支持非公平（默认）和公平的锁获取方式，吞吐量非公平高

读锁写锁都支持线程重进入

遵循获取写锁、获取读锁再释放的写锁的顺序，写锁能够降级为读锁

## 源码解读

#### 1、内部类

读写锁实现类中有许多内部类，我们先来看下这些类的定义：

```
public class ReentrantReadWriteLock implements ReadWriteLock, java.io.Serializable
```

读写锁并没有实现Lock接口，而是实现了ReadWriteLock。并发系列中真正实现Lock接口的并不多，除了前面提到过的重入锁（ReentrantLock），另外就是读写锁中为了实现读锁和写锁的两个内部类：

```
public static class ReadLock implements Lock, java.io.Serializable
public static class WriteLock implements Lock, java.io.Serializable
```

另外读写锁也设计成模板方法模式，通过继承队列同步器，提供了公平与非公平锁的特性：

```
static abstract class Sync extends AbstractQueuedSynchronizer
final static class NonfairSync extends Sync
final static class FairSync extends Sync
```

#### 2、读写状态的设计

同步状态在前面重入锁的实现中是表示被同一个线程重复获取的次数，即一个整形变量来维护，但是之前的那个表示仅仅表示是否锁定，而不用区分是读锁还是写锁。而读写锁需要在同步状态（一个整形变量）上维护多个读线程和一个写线程的状态。

读写锁对于同步状态的实现是在一个整形变量上通过“按位切割使用”：将变量切割成两部分，高16位表示读，低16位表示写。

假设当前同步状态值为S，get和set的操作如下：

1、获取写状态：

​    S&0x0000FFFF:将高16位全部抹去

2、获取读状态：

​    S>>>16:无符号补0，右移16位

3、写状态加1：

​     S+1

4、读状态加1：

　　S+（1<<16）即S + 0x00010000

在代码层的判断中，如果S不等于0，当写状态（S&0x0000FFFF），而读状态（S>>>16）大于0，则表示该读写锁的读锁已被获取。

### 3、写锁的获取与释放

```java
protected final boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);
            if (c != 0) {
                // (Note: if c != 0 and w == 0 then shared count != 0)
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)
                    throw new Error("Maximum readLock count exceeded");
            }
            if ((w == 0 && writerShouldBlock(current)) ||
                !compareAndSetState(c, c + acquires))
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }
```

1、c是获取当前锁状态；w是获取写锁的状态。

2、如果锁状态不为零，而写锁的状态为0，则表示读锁状态不为0，所以当前线程不能获取写锁。或者锁状态不为零，而写锁的状态也不为0，但是获取写锁的线程不是当前线程，则当前线程不能获取写锁。

**写锁是一个可重入的排它锁，在获取同步状态时，增加了一个读锁是否存在的判断。**

写锁的释放与ReentrantLock的释放过程类似，每次释放将写状态减1，直到写状态为0时，才表示该写锁被释放了。

### 4、读锁的获取与释放

```Java
 1 protected final int tryAcquireShared(int unused) {
 2             Thread current = Thread.currentThread();
 3             int c = getState();
 4             if (exclusiveCount(c) != 0 &&
 5                 getExclusiveOwnerThread() != current)
 6                 return -1;
 7             if (sharedCount(c) == MAX_COUNT)
 8                 throw new Error("Maximum readLock count exceeded");
 9             if (!readerShouldBlock(current) &&
10                 compareAndSetState(c, c + SHARED_UNIT)) {
11                 HoldCounter rh = cachedHoldCounter;
12                 if (rh == null || rh.tid != current.getId())
13                     cachedHoldCounter = rh = readHolds.get();
14                 rh.count++;
15                 return 1;
16             }
17             return fullTryAcquireShared(current);
18         }
```

1、读锁是一个支持重进入的共享锁，可以被多个线程同时获取。

2、在没有写状态为0时，读锁总会被成功获取，而所做的也只是增加读状态（线程安全）

**3、读状态是所有线程获取读锁次数的总和，而每个线程各自获取读锁的次数只能选择保存在ThreadLocal中，由线程自身维护。**

读锁的每次释放均减小状态（线程安全的，可能有多个读线程同时释放锁），减小的值是1<<16。

### 5、锁降级

**锁降级指的是写锁降级为读锁：把持住当前拥有的写锁，再获取到读锁，随后释放先前拥有的写锁的过程。**

**而锁升级是将读锁变成写锁，但是ReentrantReadWriteLock不支持这种方式。**

我们先来看锁升级的程序：

```
1  ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
2         rwl.readLock().readLock();
3         log.debug("get readLock");
4         rwl.writeLock().readLock();
5         log.debug("get writeLock");
```

这种线获取读锁，不释放紧接着获取写锁，会导致死锁！！！

```
1 ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
2         rwl.writeLock().readLock();
3         log.debug("get writeLock");
4         rwl.readLock().readLock();
5         log.debug("get readLock");
```

这个过程跟上面的刚好相反，程序可以正常运行不会出现死锁。但是锁降级并不会自动释放写锁。仍然需要显式的释放。

由于读写锁用于读多写少的场景，天然的使用于实现缓存，下面看一个简易的实现缓存的DEMO：

```Java
 1 import java.util.HashMap;
 2 import java.util.concurrent.locks.ReadWriteLock;
 3 import java.util.concurrent.locks.ReentrantReadWriteLock;
 4 
 5 
 6 public class CachedTest
 7 {
 8     volatile HashMap<String,String> cacheMap = new HashMap<String,String>();
 9     
10     ReadWriteLock rwLock = new ReentrantReadWriteLock();
11     
12     public String getS(String key)
13     {
14         rwLock.readLock().readLock();
15         String value = null;
16         try
17         {
18             if(cacheMap.get(key) == null)
19             {
20                 rwLock.readLock().unlock();
21                 rwLock.writeLock().readLock();
22                 try
23                 {
24                     //这里需要再次判断，防止后面阻塞的线程再次放入数据
25                     if(cacheMap.get(key) == null)
26                     {
27                         value = "" + Thread.currentThread().getName();
28                         cacheMap.put(key, value);
29                         log.debug(Thread.currentThread().getName() + "put the value" + value);
30                     }
31                 }
32                 finally
33                 {
34                     //这里是锁降级，读锁的获取与写锁的释放顺序不能颠倒
35                     rwLock.readLock().readLock();
36                     rwLock.writeLock().unlock();
37                 }
38             }
39         }
40         finally
41         {
42             rwLock.readLock().unlock();
43         }
44         return cacheMap.get(key);
45     }
46 }
```

1、业务逻辑很好理解，一个线程进来先获取读锁，如果map里面没有值，则释放读锁，获取写锁，将该线程的value放入map中。

2、这里有两次value为空的判断，第一次判断很好理解，**第二次判断是防止当前线程在获取写锁的时候，其他的线程阻塞在获取写锁的地方。当当前线程将vaule放入map之后，释放写锁。如果这个位置没有value的判断，后续获得写锁的线程以为map仍然为空，会再一次将value值放入map中，覆盖前面的value值，显然这不是我们愿意看见的。**

3、在第35行的位置，这里处理的锁降级的逻辑。按照我们正常的逻辑思维，因为是先释放写锁，再获取读锁。那么锁降级为什么要这么处理呢？**答案是为了保证数据的可见性，因为如果当前线程不获取读锁而是直接释放写锁，如果该线程在释放写锁与获取读锁这个时间段内，有另外一个线程获取的写锁并修改了数据，那么当前线程无法感知数据的变更。如果按照锁降级的原则来处理，那么当前线程获取到读锁之后，会阻塞其他线程获取写锁，那么数据就不会被其他线程所改动，这样就保证了数据的一致性。**