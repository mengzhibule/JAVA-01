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

1. `2021-01-21T01:54:58.826+0800: #GC发生的时间 `

   `[GC (Allocation Failure) # 表示本次GC发生原因，是因为新生代内存不足而导致新对象内存分配失败2021-01-21T01:54:58.826+0800: `

    `[ParNew: 139584K->17471K(157248K), # ParNew表示新生代使用的是ParNew垃圾收集器 0.0060950 secs] `

    `139584K->44797K(506816K) # 堆内存gc前后数值, 0.0061466 secs] `

    `[Times: user=0.09 sys=0.09, real=0.01 secs] `

2. ` [GC (CMS Initial Mark) [1 CMS-initial-mark: 201628K(349568K)] 219243K(506816K), 0.0001455 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
   2021-01-21T01:54:58.943+0800: [CMS-concurrent-mark-start]
   2021-01-21T01:54:58.944+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
   2021-01-21T01:54:58.944+0800: [CMS-concurrent-preclean-start]
   2021-01-21T01:54:58.944+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
   2021-01-21T01:54:58.944+0800: [CMS-concurrent-abortable-preclean-start]
   2021-01-21T01:54:58.955+0800: [GC (Allocation Failure) 2021-01-21T01:54:58.955+0800: [ParNew: 157247K->17469K(157248K), 0.0156657 secs] 358875K->262395K(506816K), 0.0157044 secs] [Times: user=0.17 sys=0.01, real=0.02 secs] 
   2021-01-21T01:54:58.984+0800: [GC (Allocation Failure) 2021-01-21T01:54:58.984+0800: [ParNew: 157245K->17470K(157248K), 0.0161587 secs] 402171K->307218K(506816K), 0.0161889 secs] [Times: user=0.34 sys=0.03, real=0.02 secs] 
   2021-01-21T01:54:59.012+0800: [GC (Allocation Failure) 2021-01-21T01:54:59.012+0800: [ParNew: 157121K->17471K(157248K), 0.0149892 secs] 446868K->349362K(506816K), 0.0150199 secs] [Times: user=0.20 sys=0.00, real=0.01 secs] 
   2021-01-21T01:54:59.027+0800: [CMS-concurrent-abortable-preclean: 0.002/0.084 secs] [Times: user=0.73 sys=0.05, real=0.08 secs] 
   2021-01-21T01:54:59.027+0800: [GC (CMS Final Remark) ` 

   `[YG occupancy: 20731 K (157248 K) `

   表示年轻代当前的内存占用情况，通常Final Remark阶段要尽量运行在年轻代是足够干净的时候，这样可以消除紧接着的连续的几个STW阶段。]

    `2021-01-21T01:54:59.027+0800: [Rescan (parallel) , 0.0002092 secs]`

   这是整个final remark阶段扫描对象的用时总计，该阶段会重新扫描CMS堆中剩余的对象，重新从“根对象”开始扫描，并且也会处理对象关联。

    `2021-01-21T01:54:59.028+0800: [weak refs processing, 0.0000321 secs]`

   第一个子阶段，随着这个阶段的进行处理弱引用

    `2021-01-21T01:54:59.028+0800: [class unloading, 0.0001756 secs]`

   第二个子阶段，表示卸载无用的类

    `2021-01-21T01:54:59.028+0800: [scrub symbol table, 0.0002848 secs] 2021-01-21T01:54:59.028+0800: [scrub string table, 0.0000734 secs] `

   最后一个子阶段，表示清理分别包含类级元数据和内部化字符串的符号和字符串表

    `[1 CMS-remark: 331890K(349568K)] `

    表示经历了上面的阶段后老年代的内存使用情况 

    `352621K(506816K)`

    表示final remark后整个堆的内存使用情况

   `, 0.0008174 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
   2021-01-21T01:54:59.028+0800: [CMS-concurrent-sweep-start]
   2021-01-21T01:54:59.029+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
   2021-01-21T01:54:59.029+0800: [CMS-concurrent-reset-start]
   2021-01-21T01:54:59.029+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] `

   这一段日志实际上表示的是cms的6个阶段：

   * initial Mark：开始收集所有的GC Roots和直接引用到的对象

   * Concurrent Mark：这个阶段会遍历整个老年代并且标记所有存活的对象，从“初始化标记”阶段找到的GC Roots开始。并发标记的特点是和应用程序线程同时运行。并不是老年代的所有存活对象都会被标记，因为标记的同时应用程序会改变一些对象的引用等。当这些引用发生变化的时候，JVM会标记堆的这个区域为Dirty Card。

   * Concurrent Preclean：把上一个阶段被标记为Dirty Card的对象以及可达的对象重新遍历标记，完成后清除Dirty Card标记。另外，一些必要的清扫工作也会做，还会做一些final remark阶段需要的准备工作。

     CMS-concurrent-abortable-preclean：并发预清理，这个阶段尝试着去承担接下来STW的Final Remark阶段足够多的工作，由于这个阶段是重复的做相同的事情直到发生abort的条件（比如：重复的次数、多少量的工作、持续的时间等等）之一才会停止。这个阶段很大程度的影响着即将来临的Final Remark的停顿。

   * Final Remark：这个阶段是CMS中第二个并且是最后一个STW的阶段。该阶段的任务是完成标记整个年老代的所有的存活对象。由于之前的预处理是并发的，它可能跟不上应用程序改变的速度，这个时候，STW是非常需要的来完成这个严酷考验的阶段。

     通常CMS尽量运行Final Remark阶段在年轻代是足够干净的时候，目的是消除紧接着的连续的几个STW阶段。

   * Concurrent sweep：和应用线程同时进行，不需要STW。这个阶段的目的就是移除那些不用的对象，回收他们占用的空间并且为将来使用。

   * Concurrent Reset：这个阶段并发执行，重新设置CMS算法内部的数据结构，准备下一个CMS生命周期的使用。

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

以[G1GC&512M](gclog/g1/G1Gc&512M.gc.log.md)为例

**结论：**

```bash
2021-01-21T01:58:49.034+0800: [GC pause (G1 Evacuation Pause) (young), 0.0018257 secs]
   [Parallel Time: 1.3 ms, GC Workers: 13]
      [GC Worker Start (ms): Min: 151.8, Avg: 151.9, Max: 151.9, Diff: 0.1]
      [Ext Root Scanning (ms): Min: 0.1, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.6]
      [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
         [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.8, Avg: 0.9, Max: 1.0, Diff: 0.2, Sum: 11.1]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.8]
         [Termination Attempts: Min: 1, Avg: 2.2, Max: 8, Diff: 7, Sum: 28]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.3]
      [GC Worker Total (ms): Min: 1.1, Avg: 1.1, Max: 1.2, Diff: 0.1, Sum: 14.8]
      [GC Worker End (ms): Min: 153.0, Avg: 153.0, Max: 153.0, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.5 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.2 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 25.0M(25.0M)->0.0B(24.0M) Survivors: 0.0B->4096.0K Heap: 28.3M(512.0M)->10.2M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

* **Eden**，（1）当前新生代收集触发的原因是Eden空间满了，分配了25.0M，使用了25.0M；（2）所有的Eden分区都被疏散处理了，在新生代结束后Eden分区的使用大小成为了0.0B；（3）Eden分区的大小缩小为24.0M；
* **Survivors:0.0B->4096.0K**：由于年轻代分区的回收处理，survivor的空间从0.0B涨到4096.0K；
* **Heap:28.3M(512.0M)->10.2M(512.0M)**：（1）在本次垃圾收集活动开始的时候，堆空间整体使用量是28.3M，堆空间的最大值是512.0M；（2）在本次垃圾收集结束后，堆空间的使用量是10.2M，最大值保持不变。

* **Parallel Time**：并行收集任务在运行过程中引发的STW（Stop The World）时间，从新生代垃圾收集开始到最后一个任务结束，共花费1.3 ms

* **GC Workers**：有13个线程负责垃圾收集，通过参数`-XX:ParallelGCThreads`设置，这个参数的值的设置，跟CPU有关，如果物理CPU支持的线程个数小于8，则最多设置为8；如果物理CPU支持的线程个数大于8，则默认值为**3 +（（5*CPU）/ 8）**。本机16核 （16 * 5 / 8 + 3 = 13）

* **GC Worker Start**：第一个垃圾收集线程开始工作时JVM启动后经过的时间（min）；最后一个垃圾收集线程开始工作时JVM启动后经过的时间（max）；diff表示min和max之间的差值。理想情况下，你希望他们几乎是同时开始，即diff趋近于0。

* **Ext Root Scanning**：扫描root集合（线程栈、JNI、全局变量、系统表等等）花费的时间，扫描root集合是垃圾收集的起点，尝试找到是否有root集合中的节点指向当前的收集集合（CSet）

* **Update RS(Remembered Set or RSet)**：每个分区都有自己的RSet，用来记录其他分区指向当前分区的指针，如果RSet有更新，G1中会有一个post-write barrier管理跨分区的引用——新的被引用的card会被标记为dirty，并放入一个日志缓冲区，如果这个日志缓冲区满了会被加入到一个全局的缓冲区，在JVM运行的过程中还有线程在并发处理这个全局日志缓冲区的dirty card。Update RS表示允许垃圾收集线程处理本次垃圾收集开始前没有处理好的日志缓冲区，这可以确保当前分区的RSet是最新的。
  * **Processed Buffers**，这表示在Update RS这个过程中处理多少个日志缓冲区。

- **Scan RS**：扫描每个新生代分区的RSet，找出有多少指向当前分区的引用来自CSet。
- **Code Root Scanning**：扫描代码中的root节点（局部变量）花费的时间
- **Object Copy**：在疏散暂停期间，所有在CSet中的分区必须被转移疏散，Object Copy就负责将当前分区中存活的对象拷贝到新的分区。
- **Termination**：当一个垃圾收集线程完成任务时，它就会进入一个临界区，并尝试帮助其他垃圾线程完成任务（steal outstanding tasks），min表示该垃圾收集线程什么时候尝试terminatie，max表示该垃圾收集回收线程什么时候真正terminated。
  - **Termination Attempts**：如果一个垃圾收集线程成功盗取了其他线程的任务，那么它会再次盗取更多的任务或再次尝试terminate，每次重新terminate的时候，这个数值就会增加。
- **GC Worker Other**：垃圾收集线程在完成其他任务的时间
- **GC Worker Total**：展示每个垃圾收集线程的最小、最大、平均、差值和总共时间。
- **GC Worker End**：min表示最早结束的垃圾收集线程结束时该JVM启动后的时间；max表示最晚结束的垃圾收集线程结束时该JVM启动后的时间。理想情况下，你希望它们快速结束，并且最好是同一时间结束。

- **Code Root Fixup** ：释放用于管理并行垃圾收集活动的数据结构，应该接近于0，该步骤是线性执行的；
- **Code Root Purge**：清理更多的数据结构，应该很快，耗时接近于0，也是线性执行。
- **Clear CT**：清理card table

- **Choose CSet**：选择要进行回收的分区放入CSet（G1选择的标准是垃圾最多的分区优先，也就是存活对象率最低的分区优先）
- **Ref Proc**：处理Java中的各种引用——soft、weak、final、phantom、JNI等等。
- **Ref Enq**：遍历所有的引用，将不能回收的放入pending列表
- **Redirty Card**：在回收过程中被修改的card将会被重置为dirty
- **Humongous Register**：JDK8u60提供了一个特性，巨型对象可以在新生代收集的时候被回收——通过`G1ReclaimDeadHumongousObjectsAtYoungGC`设置，默认为true。
- **Humongous Reclaim**：做下列任务的时间：确保巨型对象可以被回收、释放该巨型对象所占的分区，重置分区类型，并将分区还到free列表，并且更新空闲空间大小。
- **Free CSet**：将要释放的分区还回到free列表。

```bash
2021-01-21T01:58:49.246+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0009998 secs]
   [Parallel Time: 0.6 ms, GC Workers: 13]
	...
   [Eden: 9216.0K(151.0M)->0.0B(150.0M) Survivors: 4096.0K->6144.0K Heap: 242.8M(512.0M)->235.2M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-21T01:58:49.247+0800: [GC concurrent-root-region-scan-start]
2021-01-21T01:58:49.247+0800: [GC concurrent-root-region-scan-end, 0.0000769 secs]
2021-01-21T01:58:49.247+0800: [GC concurrent-mark-start]
2021-01-21T01:58:49.248+0800: [GC concurrent-mark-end, 0.0007814 secs]
2021-01-21T01:58:49.248+0800: [GC remark 2021-01-21T01:58:49.248+0800: [Finalize Marking, 0.0000986 secs] 2021-01-21T01:58:49.249+0800: [GC ref-proc, 0.0001426 secs] 2021-01-21T01:58:49.249+0800: [Unloading, 0.0003350 secs], 0.0008018 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-21T01:58:49.249+0800: [GC cleanup 243M->241M(512M), 0.0004330 secs]
 [Times: user=0.01 sys=0.00, real=0.00 secs] 
2021-01-21T01:58:49.250+0800: [GC concurrent-cleanup-start]
2021-01-21T01:58:49.250+0800: [GC concurrent-cleanup-end, 0.0000101 secs]
```

1. 标志着并发垃圾收集阶段的开始：

- **GC pause(G1 Evacuation Pause)(young)(initial-mark)**：为了充分利用STW的机会来trace所有可达（存活）的对象，initial-mark阶段是作为新生代垃圾收集中的一部分存在的（搭便车）。initial-mark设置了两个TAMS（top-at-mark-start）变量，用来区分存活的对象和在并发标记阶段新分配的对象。在TAMS之前的所有对象，在当前周期内都会被视作存活的。

2. 表示第并发标记阶段做的第一个事情：根分区扫描

- **GC concurrent-root-region-scan-start**：根分区扫描开始，根分区扫描主要扫描的是新的survivor分区，找到这些分区内的对象指向当前分区的引用，如果发现有引用，则做个记录；
- **GC concurrent-root-region-scan-end**：根分区扫描结束

3. 表示并发标记阶段

- **GC Concurrent-mark-start**：并发标记阶段开始。（1）并发标记阶段的线程是跟应用线程一起运行的，不会STW，所以称为并发；并发标记阶段的垃圾收集线程，默认值是Parallel Thread个数的25%，这个值也可以用参数`-XX:ConcGCThreads`设置；（2）trace整个堆，并使用位图标记所有存活的对象，因为在top TAMS之前的对象是隐式存活的，所以这里只需要标记出那些在top TAMS之后、阈值之前的；（3）记录在并发标记阶段的变更，G1这里使用了SATB算法，该算法要求在垃圾收集开始的时候给堆做一个快照，在垃圾收集过程中这个快照是不变的，但实际上肯定有些对象的引用会发生变化，这时候G1使用了pre-write barrier记录这种变更，并将这个记录存放在一个SATB缓冲区中，如果该缓冲区满了就会将它加入到一个全局的缓冲区，同时G1有一个线程在并行得处理这个全局缓冲区；（4）在并发标记过程中，会记录每个分区的存活对象占整个分区的大小的比率；
- **GC Concurrent-mark-end**：并发标记阶段结束

4. 重新标记阶段，会Stop the World

- **Finalize Marking**：Finalizer列表里的Finalizer对象处理
- **GC ref-proc**：引用（soft、weak、final、phantom、JNI等等）处理
- **Unloading**：类卸载
- 除了前面这几个事情，这个阶段最关键的结果是：绘制出当前并发周期中整个堆的最后面貌，剩余的SATB缓冲区会在这里被处理，所有存活的对象都会被标记；

5. 清理阶段，也会Stop the World

- 计算出最后存活的对象：标记出initial-mark阶段后分配的对象；标记出至少有一个存活对象的分区；
- 为下一个并发标记阶段做准备，previous和next位图会被清理；
- 没有存活对象的老年代分区和巨型对象分区会被释放和清理；
- 处理没有任何存活对象的分区的RSet；
- 所有的老年代分区会按照自己的存活率（存活对象占整个分区大小的比例）进行排序，为后面的CSet选择过程做准备；

6. 并发清理阶段

- **GC concurrent-cleanup-start**：并发清理阶段启动。完成第5步剩余的清理工作；将完全清理好的分区加入到二级free列表，等待最终还会到总体的free列表；
- **GC concurrent-cleanup-end**：并发清理阶段结束

```bash
2021-01-21T01:58:49.277+0800: [GC pause (G1 Evacuation Pause) (mixed), 0.0032497 secs]
   [Parallel Time: 2.8 ms, GC Workers: 13]
...
   [Eden: 7168.0K(7168.0K)->0.0B(61.0M) Survivors: 20.0M->3072.0K Heap: 370.8M(512.0M)->344.6M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

在并发收集阶段结束后，你会看到混合收集阶段的日志，如下图所示，该日志的大部分跟之前讨论的新生代收集相同，只有第1部分不一样：**GC pause(G1 Evacuation Pause)(mixed)**，这一行表示这是一个混合垃圾收集周期；在混合垃圾收集处理的CSet不仅包括新生代的分区，还包括老年代分区——也就是并发标记阶段标记出来的那些老年代分区。

```bash
2021-01-21T01:58:49.677+0800: [Full GC (Allocation Failure)  453M->334M(512M), 0.0339723 secs]
   [Eden: 0.0B(25.0M)->0.0B(31.0M) Survivors: 0.0B->0.0B Heap: 453.6M(512.0M)->334.9M(512.0M)], [Metaspace: 3120K->3120K(1056768K)]
 [Times: user=0.01 sys=0.01, real=0.03 secs] 
```

如果堆内存空间不足以分配新的对象，或者是Metasapce空间使用率达到了设定的阈值，那么就会触发Full GC——你在使用G1的时候应该尽量避免这种情况发生，因为G1的Full Gc是单线程、会Stop The World，代价非常高。

```bash
2021-01-21T01:58:49.843+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) (to-space exhausted), 0.0008303 secs]
```

**老年代不够了，这个时候会把young区所有对象不管死活都转成old区对象，所以总的内存使用量会暴增**

属于晋升失败的情况——G1收集器完成了标记阶段，开始启动混合式垃圾收集，准备要清理老年代分区，但是老年代分区在垃圾收集器释放出足够的空间之前就已经被耗尽了。这种失败通常意味着混合式垃圾收集需要更迅速得完成垃圾收集，每次新生代垃圾收集需要处理更多的老年代分区。一般来说，一系列的to-space exhausted之后会跟着一次Full GC。



G1日志分析主要参考[此文](https://juejin.cn/post/6844903893906751501)