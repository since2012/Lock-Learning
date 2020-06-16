# 线程安全Queue

## 线程安全队列

在Java多线程编程中，生产者消费者模型，想必大家都在熟悉不过了，简单来说就是一部分线程负责向容器中生产，而另一部分线程负责从容器中获取。

在这个模型当中，Java主要利用队列的数据结构进行实现。为了保证数据的安全，Java提供了两种线程安全的Queue队列，分为阻塞队列和非阻塞队列（并发队列）。

其中，阻塞队列典型的实现类是BlockingQueue（接口）--ArrayBlockingQueue实现类，而非阻塞队列典型的实现类就是ConcurrentLinkedQueue。

### 阻塞队列

阻塞，顾名思义：当我们的生产者向队列中生产数据时，若队列已满，那么生产线程会暂停下来，直到队列中有可以存放数据的地方，才会继续工作；
而当我们的消费者向队列中获取数据时，若队列为空，则消费者线程会暂停下来，直到容器中有元素出现，才能进行获取操作。


直白的来说：队列满时，生产线程停止生产；队列空时，消费线程停止活动。

阻塞队列怎么进行阻塞操作

对于队列（集合）来说，最常用的操作，无疑只有两类，一种是添加操作，一种是移除操作！

当添加时，队列怎么处理？
```
add(e):队列满时，抛出异常；
offer(e):队列满时，返回false；
put(e)：队列满时，线程一直阻塞；
offer(e,time,unit):队列满时，线程先阻塞一段时间，超时则直接返回。
```

当移除时，队列如何处理？
```
remove():队列空时，抛出异常；
poll():队列空时，返回null；
take():队列空时，线程一直阻塞；
poll(time,unit):队列空时，线程被阻塞一段时间，超时则直接返回。
```

以上就是，阻塞队列在队列已满，或者队列为空时，再继续调用添加方法，或者移除方法时，所进行的逻辑处理。

有哪些阻塞队列？

在Java中，java.util.concurrent包提供了很多阻塞队列的实现。

其中，包括：
```
ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。
LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。
PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列。
LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
```

## BlockingQueue

BlockingQueue是一个接口，是所有阻塞队列的父类，定义了阻塞队列的主要操作方法。

```
public interface BlockingQueue<E> extends Queue<E> {
    
    boolean add(E e);

    boolean offer(E e);

    void put(E e) throws InterruptedException;

    boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;

    E take() throws InterruptedException;

    E poll(long timeout, TimeUnit unit) throws InterruptedException;

    int remainingCapacity();

    boolean remove(Object o);

    public boolean contains(Object o);

    int drainTo(Collection<? super E> c);

    int drainTo(Collection<? super E> c, int maxElements);
}
```

添加方法：
```
add：插入元素，如果队列满了，抛出异常(底层调用offer方法)；
put：插入元素，如果队列满了，就等待；
offer：插入元素，如果队列满了，就直接返回false；
```

获取方法：
```
element(继承父类)：如果队列为空，直接抛出异常(底层调用peek方法)；
peek(继承父类)：如果队列为空，则返回null；
```

移除方法：
```
remove：移除对应元素，如果队列为空，则返回false;
take：移除元素，如果队列为空，则一直等待；
poll：移除元素，如果队列为空，则返回null；
```

## ArrayBlockingQueue

ArrayBlockingQueue是一个阻塞队列，底层使用数组结构实现，按照先进先出（FIFO）的原则对元素进行排序。

ArrayBlockingQueue是一个线程安全的集合，通过ReentrantLock锁来实现，在并发情况下可以保证数据的一致性。

此外，ArrayBlockingQueue的容量是有限的，数组的大小在初始化时就固定了，不会随着队列元素的增加而出现扩容的情况，也就是说ArrayBlockingQueue是一个“有界缓存区”。

当向队列插入元素时，首先会插入到数组的0角标处，再有新元素进来时，依次类推，角标1、角标2、角标3。

整个item[]就是一个队伍，我们用时间来排序，展示入队场景。

而当有元素出队时，先移除角标为0的元素，与入队一样，依次类推，移除角标1、角标2...上的元素。

这也形成了“先进先出”。

接下来，我们来看看ArrayBlockingQueue的源码实现！

- 构造方法

在多线程中，默认不保证线程公平的访问队列。

什么叫做公平访问队列？我们都知道，在ArrayBlockingQueue中为了保证数据的安全，使用了ReentrantLock锁。由于锁的引入，导致了线程之间的竞争。
当有一个线程获取到锁时，其余线程处于等待状态。当锁被释放时，所有等待线程为夺锁而竞争。

而所谓的公平访问，就是等待的线程在获取锁而竞争时，按照等待的先后顺序进行获取操作，先等待的先获取，后等待的后获取。

而非公平访问，就是在获取时候，无论是先等待还是后等待的线程，均有可能获取到锁。

在ArrayBlockingQueue中，由于公平锁会降低队列的性能，因而使用非公平锁(默认)。

是否公平，根据ReentrantLock对象来实现---ReentrantLock lock = new ReentrantLock(false)，具体看下构造便可得知。

```
public class ArrayBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    //队列实现：数组
    final Object[] items;

    //当读取元素时数组的下标(下一个被添加元素的索引)
    int takeIndex;

    //添加元素时数组的下标 （下一个被取出元素的索引）
    int putIndex;

    //队列中元素个数：
    int count;

    //锁：
    final ReentrantLock lock;

    //控制take()操作时是否让线程等待
    private final Condition notEmpty;

    //控制put()操作时是否让线程等待
    private final Condition notFull;

    //初始化队列容量构造：
    public ArrayBlockingQueue(int capacity) {
        this(capacity, false);
    }

    //带初始容量大小和公平锁队列(公平锁通过ReentrantLock实现)：
    public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }
}
```
- 插入元素

在ArrayBlockingQueue中，提供了两种不同形式的元素插入--阻塞式和非阻塞式。

对于阻塞式插入来说，当队列中的元素已满时，则会将此线程停止，让其处于等待状态，直到队列中有空余位置产生。

```
//向队列尾部添加元素，如果队列满了，则线程等待
public void put(E e) throws InterruptedException {
    //不能插入非空元素,会抛出异常
    checkNotNull(e);
    //上锁，保证数据安全
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        //队列中元素 == 数组长度(队列满了),则线程等待
        while (count == items.length)
            notFull.await();
        //添加队列元素
        insert(e);
    } finally {
        //插入完成，释放锁
        lock.unlock();
    }
}
```

而对于非阻塞式来说，当队列中的元素已满时，并不会阻塞此线程的操作，而是让其返回又或者是抛出异常。

```
//向队列尾部添加元素，队列满了返回false
public boolean offer(E e) {
    //不能插入非空元素,会抛出异常
    checkNotNull(e);
    //上锁，保证数据安全
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        //队列中元素 == 数组长度(队列满了),则返回false
        if (count == items.length)
            return false;
        else {
            //添加队列元素
            insert(e);
            return true;
        }
    } finally {
        //插入完成，释放锁
        lock.unlock();
    }
}
```

上面的offer(E e)并不会阻塞线程的执行，但是如果想让阻塞和非阻塞相结合的话，需要怎么处理?

ArrayBlockingQueue为我们提供了折中的方法--offer(E e, long timeout, TimeUnit unit);

向队列尾部添加元素，可以设置线程等待时间，如果超过指定时间队列还是满的，则返回false；

```
public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
    //不能插入非空元素,会抛出异常
    checkNotNull(e);
    //转换成超时时间阀值：
    long nanos = unit.toNanos(timeout);
    //上锁，保证数据安全
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        //对队列是否元素满了，做判断。
        while (count == items.length) {
            //如果队列是满的，则每次遍历都去递减一次nanos的值
            if (nanos <= 0)
                return false;
            nanos = notFull.awaitNanos(nanos);
        }
        //添加队列元素
        insert(e);
        return true;
    } finally {
        //插入完成，释放锁
        lock.unlock();
    }
}
```

以上添加方法，都是通过返回false/true来实现的，而在ArrayBlockingQueue中，还提供了集合最原始的插入方法--add(E e)。

该方法在插入时候，如果队列中的元素满了，则会抛出异常。如果插入成功，则返回true。

在add(E e)中，使用父类的add（E e）,实际上其底层也是调用的offer（E e）方法。

```
//向队列尾部添加元素，队列满了抛出异常；
public boolean add(E e) {
    return super.add(e);
}
```

ArrayBlockingQueue中，最底层的插入方法，上面的各种实现，都是基于insert(E x)来实现的。由于insert(E x)是用private来修饰的，所以我们不能直接对其进行调用。

```
//插入元素到队尾，调整putIndex，唤起等待的获取线程
private void insert(E x) {
    //向数组中插入元素
    items[putIndex] = x;
    //设置下一个被取出元素的索引
    putIndex = inc(putIndex);
    //增加队列元素个数：
    ++count;
    //唤醒notEmpty上的等待线程
    notEmpty.signal();
}
```

- 获取元素

//获取队列头部元素，如果队列为空，则返回null.不为空。  // 则返回队列头部，并从队列中删除。  public E poll() {  final ReentrantLock lock = this.lock;  lock.lock();  try {  return (count == 0) ? null : extract();  } finally {  lock.unlock();  }  }

```
    //返回队列的头部元素，并从队列中删除。如果队列为空，则等待
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            //如果队列为空，则进行等待
            while (count == 0)
                notEmpty.await();

            //获取头部元素：
            return extract();
        } finally {
            lock.unlock();
        }
    }

    //获取队列头部元素，如果队列为空，则设置线程等待时间，超过指定时间，还为空，则返回null。
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            return extract();
        } finally {
            lock.unlock();
        }
    }
```

##  LinkedBlockingQueue

上篇中，说到了ArrayBlockingQueue阻塞队列。在ArrayBlockingQueue中，底层使用了数组结构来实现。

那么，提到数组了就不得不提及链表。作为两对成双成对的老冤家，链表也可以实现阻塞队列。

下面，就让我们进入今天的正题LinkedBlockingQueue！！！！

LinkedBlockingQueue是一个使用链表实现的阻塞队列，支持多线程并发操作，可保证数据的一致性。

与ArrayBlockingQueue相同的是，LinkedBlockingQueue也实现了元素“先进先出（FIFO）”规则，也使用ReentrantLock来保证数据的一致性；

与ArrayBlockingQueue不同的是，LinkedBlockingQueue通常被认为是“无界”的，在默认情况下LinkedBlockingQueue的链表长度为Integer.MAX_VALUE。

下面，我们就对LinkedBlockingQueue的原理具体分析分析！

(1)成员变量

对于ArrayBlockingQueue来说，当队列在进行入队和出队时，永远只能有一个操作被执行。因为该队列只有一把锁，所以在多线程执行中并不允许同时出队和入队。

与ArrayBlockingQueue不同的是，LinkedBlockingQueue拥有两把锁，一把读锁，一把写锁，可以在多线程情况下，满足同时出队和入队的操作。

在ArrayBlockingQueue中，由于出队入队使用了同一把锁，无论元素增加还是减少，都不会影响到队列元素数量的统计，所以使用了int类型的变量作为队列数量统计。

但是，在LinkedBlockingQueue中则不同。上面说了，在LinkedBlockingQueue中使用了2把锁，在同时出队入队时，都会涉及到对元素数量的并发修改，
会有线程安全的问题。因此，在LinkedBlockingQueue中使用了原子操作类AtomicInteger，底层使用CAS（compare and set）来解决[数据安全](https://cloud.tencent.com/solution/data_protection)问题。

```
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    
    //队列容量大小，默认为Integer.MAX_VALUE
    private final int capacity;

    //队列中元素个数：(与ArrayBlockingQueue的不同)
    //出队和入队是两把锁
    private final AtomicInteger count = new AtomicInteger(0);

    //队列--头结点
    private transient Node<E> head;

    //队列--尾结点
    private transient Node<E> last;

    //与ArrayBlockingQueue的不同,两把锁
    //读取锁
    private final ReentrantLock takeLock = new ReentrantLock();

    //出队等待条件
    private final Condition notEmpty = takeLock.newCondition();

    //插入锁
    private final ReentrantLock putLock = new ReentrantLock();

    //入队等待条件
    private final Condition notFull = putLock.newCondition();
}
```

(2)链表结点

由于LinkedBlockingQueue是链表结构，所以必然会有结点存在。

结点中，保存这元素的值，以及本结点指向下一个结点的指针。

```
//队列存储元素的结点(链表结点):
static class Node<E> {
    //队列元素：
    E item;

    //链表中指向的下一个结点
    Node<E> next;

    //结点构造：
    Node(E x) { item = x; }
}
```

(3)构造函数

之前，我们说了LinkedBlockingQueue可以称为是无界队列，为什么是无界的，就是因为LinkedBlockingQueue的默认构造函数中，
指定的队列大小为Integer.MAX_VALUE = 2147483647，想必没有哪个应用程序能达到这个数量。

在初始化中，LinkedBlockingQueue的头尾结点中的元素被置为null；

```
//默认构造函数：
public LinkedBlockingQueue() {
    //默认队列长度为Integer.MAX_VALUE
    this(Integer.MAX_VALUE);
}

//指定队列长度的构造函数：
public LinkedBlockingQueue(int capacity) {
    //初始化链表长度不能为0
    if (capacity <= 0) throw new IllegalArgumentException();
    this.capacity = capacity;
    //设置头尾结点，元素为null
    last = head = new Node<E>(null);
}
```

(4)插入元素（入队）

LinkedBlockingQueue的插入获取和ArrayBlockingQueue基本类似，都包含有阻塞式和非阻塞式。

put（E e）是阻塞式插入，如果队列中的元素与链表长度相同，则此线程等待，直到有空余空间时，才执行。

```
//向队列尾部插入元素：队列满了线程等待
public void put(E e) throws InterruptedException {
    //不能插入为null元素：
    if (e == null) throw new NullPointerException();
    int c = -1;
    //创建元素结点：
    Node<E> node = new Node(e);
    final ReentrantLock putLock = this.putLock;
    final AtomicInteger count = this.count;
    //加插入锁，保证数据的一致性：
    putLock.lockInterruptibly();
    try {
        //当队列元素个数==链表长度
        while (count.get() == capacity) {
            //插入线程等待：
            notFull.await();
        }
        //插入元素：
        enqueue(node);
        //队列元素增加：count+1,但返回+1前的count值：
        c = count.getAndIncrement();
        //容量还没满，唤醒生产者线程
        // (例如链表长度为5，此时第五个元素已经插入，c=4，+1=5，所以超过了队列容量，则不会再唤醒生产者线程)
        if (c + 1 < capacity)
            notFull.signal();
    } finally {
        //释放锁：
        putLock.unlock();
    }
    //当c=0时，即意味着之前的队列是空队列,消费者线程都处于等待状态，需要被唤醒进行消费
    if (c == 0)
        //唤醒消费者线程：
        signalNotEmpty();
}
```

offer(E e)是非阻塞式插入，队列中的元素与链表长度相同时，直接返回false,不会阻塞线程。

```
//向队列尾部插入元素：返回true/false
public boolean offer(E e) {
    //插入元素不能为空
    if (e == null) throw new NullPointerException();
    final AtomicInteger count = this.count;
    //如果队列元素==链表长度，则直接返回false
    if (count.get() == capacity)
        return false;
    int c = -1;
    //创建元素结点对象：
    Node<E> node = new Node(e);
    final ReentrantLock putLock = this.putLock;
    //加锁，保证数据一致性
    putLock.lock();
    try {
        //队列元素个数 小于 链表长度
        if (count.get() < capacity) {
            //向队列中插入元素：
            enqueue(node);
            //增加队列元素个数：
            c = count.getAndIncrement();
            //容量还没满，唤醒生产者线程：
            if (c + 1 < capacity)
                notFull.signal();
        }
    } finally {
        //释放锁：
        putLock.unlock();
    }
    //此时，代表队列中还有一条数据，可以进行消费，唤醒消费者线程
    if (c == 0)
        signalNotEmpty();
    return c >= 0;
}
```

(5)获取元素（出队）

take()：阻塞式出队，获取队列头部元素，如果队列中没有元素，则此线程的等待，直到队列中有元素才执行。

```
//从队列头部获取元素，并返回。队列为null，则一直等待
public E take() throws InterruptedException {
    E x;
    int c = -1;
    final AtomicInteger count = this.count;
    final ReentrantLock takeLock = this.takeLock;
    //设置读取锁：
    takeLock.lockInterruptibly();
    try {
        //如果此时队列为空，则获取线程等待
        while (count.get() == 0) {
            notEmpty.await();
        }
        //从队列头部获取元素：
        x = dequeue();
        //减少队列元素-1,返回count减少前的值；
        c = count.getAndDecrement();
        //队列中还有可以消费的元素，唤醒其他消费者线程
        if (c > 1)
            notEmpty.signal();
    } finally {
        //释放锁：
        takeLock.unlock();
    }
    //队列中出现了空余元素，唤醒生产者进行生产。
    // (链表长度为5，队列在执行take前有5个元素，执行到此处时候有4个元素了，但是c的值还是5，所以会进入到if中来)
    if (c == capacity)
        signalNotFull();
    return x;
}
```

poll()：非阻塞式出队，当队列中没有元素，则返回null.

```
//获取头部元素，并返回。队列为空，则直接返回null
public E poll() {
    final AtomicInteger count = this.count;
    //如果队列中还没有元素，则直接返回 null
    if (count.get() == 0)
        return null;
    E x = null;
    int c = -1;
    final ReentrantLock takeLock = this.takeLock;
    //加锁，保证数据的安全
    takeLock.lock();
    try {
        //此时在判断，队列元素是否大于0
        if (count.get() > 0) {
            //移除队头元素
            x = dequeue();
            //减少队列元素个数
            c = count.getAndDecrement();
            //此时队列中，还有1个元素，唤醒消费者线程继续执行
            if (c > 1)
                notEmpty.signal();
        }
    } finally {
        //释放锁：
        takeLock.unlock();
    }
    //队列中出现了空余元素，唤醒生产者进行生产。
    // (链表长度为5，队列在执行take前有5个元素，执行到此处时候有4个元素了，但是c的值还是5，所以会进入到if中来)
    if (c == capacity)
        signalNotFull();
    return x;
}
```

(6)出队入队介绍

入队

入队操作，很简单，就是向链表中逐个插入新的元素！

首先，将最后的结点指向新插入的结点，其次将last结点置为新插入的结点，流程结束！

出队

相比于入队来说，出队的情况要复杂一点点！

但是，请记住一点，就是头部元素永远为null！

首先，将头部元素的指向下一个结点的引用，指向自己，主要为了GC的快速清理！

再将，队列中的第一个元素变成头结点，而头结点又保有永远为null的属性，则将头结点元素置为null，也就是出队操作！

(7)ArrayBlockingQueue与LinkedBlockingQueue对比

ArrayBlockingQueue底层基于数组实现，需要使用者指定队列长度，是一个不折不扣的有界队列。

LinkedBlockingQueue底层基于链表实现，无需使用者指定队列长度（可自定义)，当使用默认大小时候，是一个无界队列。

ArrayBlockingQueue由于默认必须设置队列长度，所以在使用时会能更好的预测系统性能；而LinkedBlockingQueue默认无参构造，无需指定队列长度，所以在使用时一定要多加注意，当队列中元素短时间内暴增时，可能会对系统产生灾难性影响。

但是，LinkedBlockingQueue的一大优点也是ArrayBlockingQueue所不具备的，那么就是在多个CPU的情况下，LinkedBlockingQueue可以做到同一时刻既消费、又生产。故LinkedBlockingQueue的性能也要优于ArrayBlockingQueue。

（8）生产者消费者实现

使用LinkedBlockingQueue简单模拟消费者生产者实现；
LinkedBlockingQueueTest

## 非阻塞队列

什么是非阻塞队列？

与阻塞队列相反，非阻塞队列的执行并不会被阻塞，无论是消费者的出队，还是生产者的入队。

在底层，非阻塞队列使用的是CAS(compare and set)来实现线程执行的非阻塞。

非阻塞队列的操作

与阻塞队列相同，非阻塞队列中的常用方法，也是出队和入队。

入队方法：

```
add()：底层调用offer();

offer()：Queue接口继承下来的方法，实现队列的入队操作，不会阻碍线程的执行，插入成功返回true；
```

出队方法：

```
poll()：移动头结点指针，返回头结点元素，并将头结点元素出队；队列为空，则返回null；

peek()：移动头结点指针，返回头结点元素，并不会将头结点元素出队；队列为空，则返回null；
```

下面，我们具体说下ConcurrentLinkedQueue的原理，以及实现！

ConcurrentLinkedQueue是一个线程安全的队列，基于链表结构实现，是一个无界队列，理论上来说队列的长度可以无限扩大。

与其他队列相同，ConcurrentLinkedQueue也采用的是先进先出（FIFO）入队规则，对元素进行排序。当我们向队列中添加元素时，
新插入的元素会插入到队列的尾部；而当我们获取一个元素时，它会从队列的头部中取出。

因为ConcurrentLinkedQueue是链表结构，所以当入队时，插入的元素依次向后延伸，形成链表；而出队时，则从链表的第一个元素开始获取，依次递增；

不知道，我这样形容能否让你对链表的入队、出队产生一个大概的思路！

简单使用

值得注意的是，在使用ConcurrentLinkedQueue时，如果涉及到队列是否为空的判断，切记不可使用size()==0的做法，因为在size()方法中，
是通过遍历整个链表来实现的，在队列元素很多的时候，size()方法十分消耗性能和时间，只是单纯的判断队列为空使用isEmpty()即可！！！
ConcurrentLinkedQueueTest

引言：在笔者研究源码时，发现无论是idea,还是eclipse，在debug模式下，跟踪ConcurrentLinkedQueue源码时都会产生bug，
具体情况就是debug控制台中类的内存地址和实际的内存地址不一致，导致代码在debug执行时并不会按照正常逻辑来执行。

详细描述，可参考如下内容：[神奇的控制台](https://link.jianshu.com/?t=https%3A%2F%2Fwww.zhihu.com%2Fquestion%2F62501203%2Fanswer%2F278573225)

解决方案：将ConcurrentLinkedQueue源码拷出，本地新建一个类，使用run执行，在方法的前后增加自己的输出语句，打印出实际的内存地址，便可一探究竟。
如果你不想对源码进行修改，只想用debug模式，建议将拷贝源码中的ConcurrentLinkedQueue的继承和实现统统去掉，
形式如下：public class ConcurrentLinkedQueue<E>，这样也可以保证debug模式的正常运行。

## ConcurrentLinkedQueue实现原理

### 链表结点

在ConcurrentLinkedQueue中，元素保存在结点中，对外以元素形式存在，对内则以结点形式存在。

每一个结点中，都有指向下一个结点的指针，依次向后排列，形成链表结构。

在ConcurrentLinkedQueue中，有一个内部类--Node，此类代表队列的结点。

在Node中，item表示元素，next为指向下一个元素的指针，并且都被volatitle所修饰。

之前，我们说了ConcurrentLinkedQueue是使用CAS来实现非阻塞入队出队。在Node结点中，我们也使用了CAS来实现结点的操作。

使用CAS来替换本结点中的元素，使用CAS来替换本结点中指向下一个元素的指针。

```
//单向链表中：结点对象Node：
private static class Node<E> {
    //链表中存储的元素：
    volatile E item;

    //本结点指向下一结点的引用：
    volatile Node<E> next;

    //结点构造：使用UNSAFE机制（CAS）实现元素存储
    Node(E item) {UNSAFE.putObject(this, itemOffset, item);}

    //替换本结点中的元素值：cmp是期望值，val是目标值。当本结点中元素的值等于cmp的时，则将其替换为val
    boolean casItem(E cmp, E val) {return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);}

    //将本结点中指向下一个结点的引用指向自己
    void lazySetNext(Node<E> val) {UNSAFE.putOrderedObject(this, nextOffset, val);}

    //替换本结点中指向下一个结点的引用：cmp是期望值，val是目标值。当本结点中的指向下一个结点的引用等于cmp时，则将其替换为指向val
    boolean casNext(Node<E> cmp, Node<E> val) {return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);}

    //JDK提供的Unsafe对象，底层CAS原理实现
    private static final sun.misc.Unsafe UNSAFE;
    
    private static final long itemOffset;
    
    private static final long nextOffset;

    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class k = Node.class;

            //结点中元素的起始地址偏移量：
            itemOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("item"));
            
            //结点中指向下一个元素引用的起始地址偏移量：
            nextOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("next"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
```

总体来说，在Node类中，元素的替换，指针的改变都是通过CAS来实现的。在方法中，如果执行成功则返回true,执行失败则返回false；

可能有的朋友对itemOffset、nextOffset不太理解，这块我给大家稍微做个解释！

其实，你可以理解为当一个对象创建后，JVM在内存中为这个对象分配了一片区域，该区域用来存储该类的一些信息，这些信息中就包括item、next等属性。
而为了能更快的从内存中，对这些属性获取修改，我们就需要使用Unsafe类，该类可以帮助获取到这些属性所在内存中具体的位置，有了位置的信息，我们的程序就能更快的进行操作！

### 成员变量

在ConcurrentLinkedQueue中，head、tail属性就是队列中常见的头指针、尾指针。值得注意的是，head、tail属性都被volatitle所修饰。

volatitlte是一个轻量级的同步机制，当有线程对其所修饰的属性进行更新时，被更新的值会立刻同步到内存中去，并且使其他cpu所缓存的值置为无效。当其他线程对该属性操作时，必须从主存中获取。

此外，与Node类类似，在ConcurrentLinkedQueue中也包含了Unsafe类，以及headOffset--头结点偏移量，tailOffset--尾结点偏移量。

```
//队列中头结点：
private transient volatile Node<E> head;

//队列中尾结点：
private transient volatile Node<E> tail;

private static final sun.misc.Unsafe UNSAFE;
private static final long headOffset;
private static final long tailOffset;

static {
    try {
        UNSAFE = sun.misc.Unsafe.getUnsafe();
        Class k =ConcurrentLinkedQueue.class;
        headOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("head"));
        tailOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("tail"));
    } catch (Exception e) {
        throw new Error(e);
    }
}
```

### 构造函数

在默认构造中，队列的头尾结点指针都指向同一个结点，并且结点元素为null；

```
//默认构造，指定头尾结点元素为null：
public ConcurrentLinkedQueue() {
    head = tail = new Node<E>(null);
}
```

### 入队

在ConcurrentLinkedQueue中，入队操作包含两种方法，一个是add(E e)，一个是offer(E e)。

add(E e)底层调用offer(E e)，所以我们主要说说offer(E e).

```
//向队列尾部添加元素(底层调用offer):
public boolean add(E e) {
    return offer(e);
}

//入队：向队列尾部添加元素:
public boolean offer(E e) {
    //不能添加为空元素：抛异常
    checkNotNull(e);

    //创建新结点：
    final Node<E> newNode = new Node<E>(e);
    
    //p的类型为Node<E>(这块需要注意，不需要显式声明)
    for (Node<E> t = tail, p = t;;) {
        //获取链表中尾部结点的下一个结点：
        Node<E> q = p.next;
        
        //并判断下一个结点是否为null(正常情况下均为null)，为null则说明p是链表中的最后一个节点
        if (q == null) {------------------------⑴
            //将p节点中指向下一个结点的引用指向newNode节点（向链表中插入元素）
            if (p.casNext(null, newNode)) {
                if (p != t) 
                    casTail(t, newNode);  
                return true;
            }//CAS插入失败，则进入下次循环
            
        } else if (p == q){------------------------⑵
            p = (t != (t = tail)) ? t : head;
        
        } else {------------------------⑶
            p = (p != t && t != (t = tail)) ? t : q;
        }
    }
}
```

在offer(E e)的判断中，由于使用了三目运算符，导致可读性较差，对于有的朋友来说可能难以理解。

我们对其进行了优化，由三目运算符修改为if/else的形式：

p = (t != (t = tail)) ? t : head 替换为：

```
Node<E> tmp=t;
t = tail;
if (tmp==t) {
    p=head;
} else {
    p=t;
}
```

p = (p != t && t != (t = tail)) ? t : q 替换为：

```
if (p==t) {
    p = q;
} else {
    Node<E> tmp=t;
    t = tail;
    if (tmp==t) {
        p=q;
    } else {
        p=t;
    }
}
```

结合上面的源码，我们来具体说说入队的流程:

```
当插入的元素为空时候，会抛出异常，禁止向队列中插入尾空元素；

创建插入元素的新结点newNode，从tail指针处遍历链表结构。

例如：
    向队列中插入第一个元素时，元素="1111"，tail=Node(null)、t=tail、p=tail、p.next=null、q=null。此处需要注意，由于是插入队列的第一个元素，所以需要回过去看下队列的默认构造是如何实现。

    q=null，进入判断条件，p.casNext(null,newNode)，调用p结点的cas方法，判断p结点的next属性是否为null，如果为null，则将next属性指向newNode结点。调用成功，返回true；调用失败，返回false。由于底层使用CAS实现，所以casNext()方法将是一个原子性操作。

    如果调用失败，则进行下一次循坏，直至插入成功为止。而调用成功，则进入if内部，判断p和t是否相同，此时是何含义呢？

    在ConcurrentLinkedQueue中，当插入一个结点时，并不会每插入一次都改变一次tail结点的指向，当我们发现p/t不同时，也就是说最后一个结点和tail结点不为同一个时，我们就需要调用casTail()方法，来修改tail结点的指向。

    例如，当我们向队列中，插入第一个元素时候，直至插入结束，我们也并没有修改tail结点的指向，当第二次插入时候会进行修改。
    
    下面，我们来看下第二个元素的插入情况，元素="2222"。

    同样，判断为null，创建新结点。进入第一次循环：tail=Node(null)、t=tail、p=tail、p.next=Node(1111)、q=Node(1111)。
    进入第一个判断q==null不成立，第二个判断p==q不成立，进入else：看上面的简化代码，发现此时p==t，所以将p=q，结束循环，进入下一次。

    进入第二次循环，t=tail，p=q=Node(1111)，p.next=null，q=null。进入第一个循环判断q=null成立，此时与第一次插入情况相同。插入完成后，判断p!=t，此时p=Node(1111)/t=tail，进入判断，调用casTail(t, newNode)，将tail指针指向newNode结点，至此插入成功，返回true。

    上面我们说到了⑴⑶两种情况，接下来我们说说⑶。什么情况下，回进入⑵的判断中呢？
    
    当我们再添加完首个元素后，立即进行出队操作，此时再去添加一个元素，那么就会在循环中直接进入⑵的判断中。此时需要结合出队代码一块学习。

    tail=Node(null)，t=tail，p=tail，p.next=p=Node(null)，q=p=Node(null)，也就是此时的tail节点元素为null，而指向下一个元素的指针也指向了自己，这是由于元素出队导致的。

    进入第二个循环p==q，回看上面的优化代码，得到p=head；开始第二次循环，head在出队时被设置成了指向第一次插入的元素(此时该元素的值为null,但结点依旧存在)。p.next=null，q=null，进入第一个判断p==null，并进入p!=t，重新设置tail指针。
```

说了很多，想必不少人已经看蒙了，下面我们用图片来进行下简单的描述！！！

### 出队

```
//出队：移除队列中头结点中的元素
public E poll() {
    restartFromHead:
    for (;;) {
        //p的类型为Node<E>(这块需要注意，不需要显式声明)
        for (Node<E> h = head, p = h, q;;) {
            //头部结点的元素：
            E item = p.item;
            //如果p节点的元素不为空，使用CAS设置p节点引用的元素为null
            if (item != null && p.casItem(item, null)) {
                if (p != h) 
                    updateHead(h, ((q = p.next) != null) ? q : p);
                return item;
            } else if ((q = p.next) == null) {
                //如果p的下一个结点为null，则说明队列中没有元素，更新头结点
                updateHead(h, p);
                return null;
            } else if (p == q) {
                continue restartFromHead;
            } else {
                p = q;
            }
        }
    }
}
```

与入队流程相比，出队流程的操作也同样复杂，需要我们静下心来细细学习！！

我们假设，此时队列中已经存在了4个元素。

出队第一个元素：

```
进入for循环：head=Node(null)，h=head，p=head，q=null；item=p.item=null；进行if判断，第一个判断不满足，第二个判断中将q进行了修改：q=p.next=Node(1111)，第三个判断p==q不满足，直接进入最后的else，将p=q=Node(1111);

开始第二次循环，item=p.item=1111，进入第一个判断p.casItem(item,null)，将p节点中的元素值1111置为null；进行判断p!=h，成立调用update(h,q)方法，将head指针指向Node(2222)处，并将原来head指向结点Node(null)的next属性指向自己；
```

出队第二个元素：

```
进入for循环：head=Node(2222)，h=head，p=head，q=null；item=p.item=2222；进行if判断，满足条件，进行p.casItem(item,null)，p=Node(null)--head=Node(null)；判断p!=h，此时结果为等于，则不修改head指针；
```

出队第三个元素：

```
进入for循环：head=Node(null)，h=head，p=head，q=null；item=p.item=null；进行if判断，第一个判断不满足，第二个判断中将q进行了修改：q=p.next=Node(3333)，第三个判断p==q不满足，直接进入最后的else，将p=q=Node(3333);

开始第二次循环，item=p.item=3333，进入第一个判断p.casItem(item,null)，将p节点中的元素值3333置为null；进行判断p!=h，成立调用update(h,q)方法，将head指针指向Node(4444)处，并将原来head指向结点Node(null)的next属性指向自己；
```

出队第四个元素：

```
重复出队第二个元素的步骤，将第四个结点元素置为null；
```

### 获取

```
//获取头部元素，不移除队列中头结点中的元素
public E peek() {
    restartFromHead:
    for (;;) {
        for (Node<E> h = head, p = h, q;;) {
            E item = p.item;
            if (item != null || (q = p.next) == null) {
                updateHead(h, p);
                return item;
            }
            else if (p == q)
                continue restartFromHead;
            else
                p = q;
        }
    }
}
```

peek()方法与poll()方法类似，返回的都是头结点中的元素。

但有一点不同的是，peek()方法并不会移除头结点中的元素，而poll()在改变head指向的同时还移除了头结点中的元素，将其置为null。