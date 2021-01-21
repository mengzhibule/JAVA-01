测试环境：

* jdk版本：`1.8.0_201`
* 虚拟机：`Java HotSpot(TM) 64-Bit Server VM (build 25.201-b09, mixed mode)`
* 本机配置：`32G16核`

### ParallelGC

**最大堆内存256M**

1. JVM启动参数设置：`-XX:+UseParallelGC -Xmx256m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`OOM  java heap space`
3. 日志内容：

[ParallelGc&256M.gc.log](gclog/parallel/ParallelGc&256M.gc.log.md)

**最大堆内存512M**

1. JVM参数：`-XX:+UseParallelGC -Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:10290`
3. 日志内容：

[ParallelGC&512M.gc.log](gclog/parallel/ParallelGC&512M.gc.log.md)

**最大堆内存1G**

1. JVM参数：`-XX:+UseParallelGC -Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:21927`
3. 日志内容：

[ParallelGC&1G.gc.log](gclog/parallel/ParallelGC&1G.gc.log.md)

**最大堆内存2G**

1. JVM参数：`-XX:+UseParallelGC -Xmx2g -Xms2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:24121`
3. 日志内容：

[ParallelGC&2G.gc.log](gclog/parallel/ParallelGC&2G.gc.log.md)

**最大堆内存4G**

1. JVM参数：`-XX:+UseParallelGC -Xmx4g -Xms4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:27602`
3. 日志内容：

[ParallelGC&4G.gc.log](gclog/parallel/ParallelGC&4G.gc.log.md)

**最大堆内存8G**

1. JVM参数：`-XX:+UseParallelGC -Xmx8g -Xms8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:23215`
3. 日志内容：

[ParallelGC&8G.gc.log](gclog/parallel/ParallelGC&8G.gc.log.md)



**对ParalleGC日志分析：**

以[ParallelGc&256M.gc.log](gclog/parallel/ParallelGc&256M.gc.log.md)为例

1. 本次GC一共发生47次，其中12次`minor GC`，35次`Full GC`。

2. 最后的内存分布是这样的，新生代总共58M，老年代175M，那整个堆内存，大概是占用了58+175 = 233M。

3. 最后一次的`Full GC`，已经回收不掉任何对象了，此时再新增对象，发现已经申请不到空间了，所以就报`OOM`了。

4. 本次gc使用的是并行GC，从每一次的GC的日志来看，STW的时间维持在10-20ms以内。最大的一次STW时间，是30ms，整体来说还算稳定。

5. 对Young gc分析，` [GC (Allocation Failure) # Allocation 指的是分配速率  Failure表示是因为在年轻代中没有足够的空间能够存储新的数据了。` 

   `[PSYoungGen: 65384K->10732K(76288K)] # 新生代一开始65M,经过这一次gc后，还剩10M还没回收，一共回收了65-10 = 55M，总共还有76M，这个我是截取的是初始的JVM日志，我理解76M是新生代最大申请到的内存空间 ，那65M指的其实应该是Eden区，大概占用(65/76=85%)的新生代空间`

   `65384K->21803K(251392K),# 堆一开始65M,经过这一次gc后，还剩21M还没回收，一共回收了65-21 = 44M，总共还有251M，配合前面的新生代情况，可以得出21-10=11M的对象已经在老年代了 `

    `0.0025530 secs # 本次GC花费2ms] `

   `[Times: user=0.08 sys=0.14, real=0.00 secs # CPU消耗情况]`

6. 对于Full GC分析

   `[Full GC (Allocation Failure) `

   `[PSYoungGen: 29407K->29407K(58368K)] # 新生代，跟Young GC一样`

   `[ParOldGen: 174687K->174661K(175104K)] # 老年代，回收了174687-174661 = 26KB，总共175104K`

   ` 204094K->204069K(233472K) # 堆, ` 

   `[Metaspace: 3284K->3284K(1056768K) # 元空间], 0.0240838 secs]`

   ` [Times: user=0.11 sys=0.00, real=0.02 secs]`

**根据结果和日志分析得出的结论：**

1. 对于GcTest.java的代码和parallel gc而言，在设置到堆内存设置到最大为4G的时候，最为合适。
2. 堆内存设置的越小，GC的频率就越高，即使能与用户线程并行执行，也免不了会有STW的时间损耗，所以频率越高，STW的时间损耗也就越高，也是影响性能的主要原因之一。
3. 若是GC来不及回收内存，导致内存占满，会导致OOM。
4. 如果堆内存设置的很大，虽说GC的频率会降低，但是每一次GC所需要的时间却是比较长的，因为每一次GC的垃圾内存会比较大。
5. 对于新生代的分布，我们看到确实是`8:1:1`的比例，其中`[PSYoungGen: 65384K->10732K(76288K)]`应该可以认为是`Eden和其中一个Survivor区`



### Serial GC

**最大堆内存256M**

1. JVM参数：`-XX:+UseSerialGC -Xmx256m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：` java.lang.OutOfMemoryError: Java heap space`
3. 日志内容：

[SerialGC&256M](gclog/serial/SerialGc&256M.gc.log.md)

**最大堆内存512M**

1. JVM参数：`-XX:+UseSerialGC -Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:13790`
3. 日志内容：

[SerialGC&512M](gclog/serial/SerialGc&512M.gc.log.md)

**最大堆内存1G**

1. JVM参数：`-XX:+UseSerialGC -Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:22325`
3. 日志内容：

[SerialGC&1g](gclog/serial/SerialGc&1G.gc.log.md)

**最大堆内存2G**

1. JVM参数：`-XX:+UseSerialGC -Xmx2g -Xms2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:21720`
3. 日志内容：

[SerialGC&2g](gclog/serial/SerialGC&2G.gc.log.md)

**最大堆内存4G**

1. JVM参数：`-XX:+UseSerialGC -Xmx4g -Xms4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:20903`
3. 日志内容：

[SerialGC&4g](gclog/serial/SerialGC&4G.gc.log.md)

**最大堆内存8G**

1. JVM参数：`-XX:+UseSerialGC -Xmx8g -Xms8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:17788`
3. 日志内容：

[SerialGC&8g](gclog/serial/SerialGC&8G.gc.log.md)



**日志内容分析：**

与Parallel GC日志相同，唯一的不同点是Serial GC的单线程操作。另外DefNew实际上就新生代的内存情况。

**结论：**

1. 总体结论来说，跟Parallel GC类似，堆内存还是要适中最好。
2. 但是相比较Parallel GC，能够生成的对象相对来说比较。这是因为Serial GC的单线程操作，每次GC耗时都相对于parallel Gc比较长，也可以说STW的时间比较长，也就间接导致用户线程相当长的时间里是不work的。



### CMSGC

**最大堆内存256M**

1. JVM参数：`-XX:+UseConcMarkSweepGC -Xmx256m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：` java.lang.OutOfMemoryError: Java heap space`
3. 日志内容：

[CMSGC&256M](gclog/cms/CMSGc&256M.gc.log.md)

**最大堆内存512M**

1. JVM参数：`-XX:+UseConcMarkSweepGC -Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:13438`
3. 日志内容：

[CMSGC&512M](gclog/cms/CMSGc&512M.gc.log.md)

**最大堆内存1G**

1. JVM参数：`-XX:+UseConcMarkSweepGC -Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:22261`
3. 日志内容：

[CMSGC&1g](gclog/cms/CMSGc&1G.gc.log.md)

**最大堆内存2G**

1. JVM参数：`-XX:+UseConcMarkSweepGC -Xmx2g -Xms2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:22724`
3. 日志内容：

[CMSGC&2g](gclog/cms/CMSGc&2G.gc.log.md)

**最大堆内存4G**

1. JVM参数：`-XX:+UseConcMarkSweepGC -Xmx4g -Xms4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:23298`
3. 日志内容：

[CMSGC&4g](gclog/cms/CMSGc&4G.gc.log.md)

**最大堆内存8G**

1. JVM参数：`-XX:+UseConcMarkSweepGC -Xmx8g -Xms8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:23249`
3. 日志内容：

[CMSGC&8g](gclog/cms/CMSGc&8G.gc.log.md)

**日志内容分析**
以[CMSGC&512M](gclog/cms/CMSGc&512M.gc.log.md)为例：

**结论：**

### G1GC

**最大堆内存256M**

1. JVM参数：`-XX:+UseG1GC -Xmx256m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`java.lang.OutOfMemoryError: Java heap space`
3. 日志内容：

[G1GC&256M](gclog/g1/G1Gc&256M.gc.log.md)

**最大堆内存512M**

1. JVM参数：`-XX:+UseG1GC -Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:12440`
3. 日志内容：

[G1GC&512M](gclog/g1/G1Gc&512M.gc.log.md)

**最大堆内存1G**

1. JVM参数：`-XX:+UseG1GC -Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:22285`
3. 日志内容：

[G1GC&1g](gclog/g1/G1Gc&1G.gc.log.md)

**最大堆内存2G**

1. JVM参数：`-XX:+UseG1GC -Xmx2g -Xms2g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:23811`
3. 日志内容：

[G1GC&2g](gclog/g1/G1Gc&2G.gc.log.md)

**最大堆内存4G**

1. JVM参数：`-XX:+UseG1GC -Xmx4g -Xms4g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:23770`
3. 日志内容：

[G1GC&4g](gclog/g1/G1Gc&4G.gc.log.md)

**最大堆内存8G**

1. JVM参数：`-XX:+UseG1GC -Xmx8g -Xms8g -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 结果：`执行结束!共生成对象次数:25437`
3. 日志内容：

[G1GC&8g](gclog/g1/G1Gc&8G.gc.log.md)



**日志内容分析：**

以[G1GC&8g](gclog/g1/G1Gc&8G.gc.log.md)为例



**结论：**



### 总结：

