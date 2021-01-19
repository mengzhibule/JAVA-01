【周三作业题目】：
1.使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例。

2.使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例。

3.（选做） 如果自己本地有可以运行的项目，可以按照 2 的方式进行演练。

4.（必做） 根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 的总结，提交到 Github。

【周日作业题目】：
1.（选做）运行课上的例子，以及 Netty 的例子，分析相关现象。

2.（必做）写一段代码，使用 HttpClient 或 OkHttp 访问 http://localhost:8801 ，代码提交到 Github。





## 周三作业第一题：

jdk1.8

1. JVM启动参数设置：`-XX:+UseParallelGC -Xmx256m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps`
2. 使用的是并行GC，堆内存最大为256M。
3. 结果：`OOM  java heap space`
4. 日志内容：

```bash
2021-01-20T01:02:32.826+0800: [GC (Allocation Failure) [PSYoungGen: 65384K->10732K(76288K)] 65384K->21803K(251392K), 0.0025530 secs] [Times: user=0.08 sys=0.14, real=0.00 secs] 
2021-01-20T01:02:32.837+0800: [GC (Allocation Failure) [PSYoungGen: 76159K->10746K(76288K)] 87230K->39125K(251392K), 0.0033361 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.847+0800: [GC (Allocation Failure) [PSYoungGen: 75991K->10749K(76288K)] 104370K->58858K(251392K), 0.0034489 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.856+0800: [GC (Allocation Failure) [PSYoungGen: 76285K->10741K(76288K)] 124394K->83040K(251392K), 0.0042090 secs] [Times: user=0.03 sys=0.17, real=0.00 secs] 
2021-01-20T01:02:32.867+0800: [GC (Allocation Failure) [PSYoungGen: 76236K->10750K(76288K)] 148535K->101820K(251392K), 0.0035391 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.877+0800: [GC (Allocation Failure) [PSYoungGen: 76286K->10736K(40448K)] 167356K->128127K(215552K), 0.0044132 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.883+0800: [GC (Allocation Failure) [PSYoungGen: 39908K->16060K(58368K)] 157299K->135328K(233472K), 0.0020001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.888+0800: [GC (Allocation Failure) [PSYoungGen: 45731K->25680K(58368K)] 164999K->146112K(233472K), 0.0027151 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.894+0800: [GC (Allocation Failure) [PSYoungGen: 55288K->28466K(58368K)] 175719K->157053K(233472K), 0.0038539 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.901+0800: [GC (Allocation Failure) [PSYoungGen: 58162K->19899K(58368K)] 186749K->165396K(233472K), 0.0040712 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.905+0800: [Full GC (Ergonomics) [PSYoungGen: 19899K->0K(58368K)] [ParOldGen: 145497K->139552K(175104K)] 165396K->139552K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0155041 secs] [Times: user=0.19 sys=0.01, real=0.02 secs] 
2021-01-20T01:02:32.923+0800: [GC (Allocation Failure) [PSYoungGen: 29239K->8621K(58368K)] 168791K->148173K(233472K), 0.0015400 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.927+0800: [GC (Allocation Failure) [PSYoungGen: 38317K->11871K(58368K)] 177869K->159548K(233472K), 0.0024639 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:32.930+0800: [Full GC (Ergonomics) [PSYoungGen: 11871K->0K(58368K)] [ParOldGen: 147677K->151670K(175104K)] 159548K->151670K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0131057 secs] [Times: user=0.20 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:32.946+0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->0K(58368K)] [ParOldGen: 151670K->158023K(175104K)] 181366K->158023K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0147284 secs] [Times: user=0.20 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:32.964+0800: [Full GC (Ergonomics) [PSYoungGen: 29624K->0K(58368K)] [ParOldGen: 158023K->164729K(175104K)] 187648K->164729K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0146815 secs] [Times: user=0.20 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:32.982+0800: [Full GC (Ergonomics) [PSYoungGen: 28946K->0K(58368K)] [ParOldGen: 164729K->168732K(175104K)] 193675K->168732K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0139937 secs] [Times: user=0.20 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:32.999+0800: [Full GC (Ergonomics) [PSYoungGen: 29679K->0K(58368K)] [ParOldGen: 168732K->173038K(175104K)] 198411K->173038K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0152789 secs] [Times: user=0.20 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.018+0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->1026K(58368K)] [ParOldGen: 173038K->175004K(175104K)] 202734K->176031K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0151541 secs] [Times: user=0.20 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.036+0800: [Full GC (Ergonomics) [PSYoungGen: 29679K->7713K(58368K)] [ParOldGen: 175004K->174845K(175104K)] 204683K->182559K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0155513 secs] [Times: user=0.20 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.054+0800: [Full GC (Ergonomics) [PSYoungGen: 29372K->11973K(58368K)] [ParOldGen: 174845K->175062K(175104K)] 204217K->187035K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0186519 secs] [Times: user=0.20 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.075+0800: [Full GC (Ergonomics) [PSYoungGen: 29246K->14545K(58368K)] [ParOldGen: 175062K->175023K(175104K)] 204308K->189569K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0167286 secs] [Times: user=0.20 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:33.092+0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->17461K(58368K)] [ParOldGen: 175023K->174770K(175104K)] 204719K->192232K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0162601 secs] [Times: user=0.02 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.110+0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->18047K(58368K)] [ParOldGen: 174770K->174705K(175104K)] 204466K->192753K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0194174 secs] [Times: user=0.01 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.130+0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->18906K(58368K)] [ParOldGen: 174705K->174751K(175104K)] 204401K->193657K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0170264 secs] [Times: user=0.20 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.149+0800: [Full GC (Ergonomics) [PSYoungGen: 29696K->20724K(58368K)] [ParOldGen: 174751K->174595K(175104K)] 204447K->195319K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0199344 secs] [Times: user=0.19 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.170+0800: [Full GC (Ergonomics) [PSYoungGen: 29518K->22109K(58368K)] [ParOldGen: 174595K->174653K(175104K)] 204113K->196763K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0194167 secs] [Times: user=0.41 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.190+0800: [Full GC (Ergonomics) [PSYoungGen: 29678K->24291K(58368K)] [ParOldGen: 174653K->174662K(175104K)] 204331K->198953K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0168203 secs] [Times: user=0.05 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.207+0800: [Full GC (Ergonomics) [PSYoungGen: 29317K->23773K(58368K)] [ParOldGen: 174662K->174850K(175104K)] 203979K->198624K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0179265 secs] [Times: user=0.19 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.226+0800: [Full GC (Ergonomics) [PSYoungGen: 29610K->24527K(58368K)] [ParOldGen: 174850K->174847K(175104K)] 204461K->199374K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0231074 secs] [Times: user=0.20 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.250+0800: [Full GC (Ergonomics) [PSYoungGen: 29640K->24569K(58368K)] [ParOldGen: 174847K->174934K(175104K)] 204487K->199504K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0172740 secs] [Times: user=0.19 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.268+0800: [Full GC (Ergonomics) [PSYoungGen: 29673K->26293K(58368K)] [ParOldGen: 174934K->174780K(175104K)] 204608K->201074K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0263781 secs] [Times: user=0.14 sys=0.00, real=0.03 secs] 
2021-01-20T01:02:33.295+0800: [Full GC (Ergonomics) [PSYoungGen: 29672K->27053K(58368K)] [ParOldGen: 174780K->174684K(175104K)] 204453K->201737K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0187783 secs] [Times: user=0.41 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.314+0800: [Full GC (Ergonomics) [PSYoungGen: 29517K->27589K(58368K)] [ParOldGen: 174684K->174883K(175104K)] 204202K->202472K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0192951 secs] [Times: user=0.19 sys=0.02, real=0.02 secs] 
2021-01-20T01:02:33.334+0800: [Full GC (Ergonomics) [PSYoungGen: 29687K->27963K(58368K)] [ParOldGen: 174883K->174539K(175104K)] 204570K->202503K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0062177 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:33.340+0800: [Full GC (Ergonomics) [PSYoungGen: 29686K->28818K(58368K)] [ParOldGen: 174539K->174162K(175104K)] 204225K->202981K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0138305 secs] [Times: user=0.20 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:33.354+0800: [Full GC (Ergonomics) [PSYoungGen: 29429K->27604K(58368K)] [ParOldGen: 174162K->174995K(175104K)] 203592K->202600K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0177191 secs] [Times: user=0.20 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.372+0800: [Full GC (Ergonomics) [PSYoungGen: 29529K->28469K(58368K)] [ParOldGen: 174995K->174946K(175104K)] 204525K->203416K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0266073 secs] [Times: user=0.19 sys=0.00, real=0.03 secs] 
2021-01-20T01:02:33.399+0800: [Full GC (Ergonomics) [PSYoungGen: 29458K->28469K(58368K)] [ParOldGen: 174946K->174946K(175104K)] 204405K->203416K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0018379 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:33.401+0800: [Full GC (Ergonomics) [PSYoungGen: 29479K->28616K(58368K)] [ParOldGen: 174946K->174843K(175104K)] 204426K->203459K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0276144 secs] [Times: user=0.19 sys=0.00, real=0.03 secs] 
2021-01-20T01:02:33.429+0800: [Full GC (Ergonomics) [PSYoungGen: 29691K->28544K(58368K)] [ParOldGen: 174843K->174843K(175104K)] 204534K->203387K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0020137 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:33.431+0800: [Full GC (Ergonomics) [PSYoungGen: 29526K->28619K(58368K)] [ParOldGen: 174843K->174687K(175104K)] 204370K->203306K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0047218 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-01-20T01:02:33.436+0800: [Full GC (Ergonomics) [PSYoungGen: 28981K->28619K(58368K)] [ParOldGen: 174687K->174687K(175104K)] 203668K->203306K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0025036 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:33.439+0800: [Full GC (Ergonomics) [PSYoungGen: 29407K->29407K(58368K)] [ParOldGen: 174687K->174687K(175104K)] 204094K->204094K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0019279 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-01-20T01:02:33.441+0800: [Full GC (Allocation Failure) [PSYoungGen: 29407K->29407K(58368K)] [ParOldGen: 174687K->174661K(175104K)] 204094K->204069K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0240838 secs] [Times: user=0.11 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.464+0800: [Full GC (Ergonomics) [PSYoungGen: 29693K->29675K(58368K)] [ParOldGen: 175081K->174890K(175104K)] 204775K->204565K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0235847 secs] [Times: user=0.16 sys=0.00, real=0.02 secs] 
2021-01-20T01:02:33.488+0800: [Full GC (Allocation Failure) [PSYoungGen: 29675K->29675K(58368K)] [ParOldGen: 174890K->174890K(175104K)] 204565K->204565K(233472K), [Metaspace: 3284K->3284K(1056768K)], 0.0019694 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
Heap
 PSYoungGen      total 58368K, used 29696K [0x00000000fab00000, 0x0000000100000000, 0x0000000100000000)
  eden space 29696K, 100% used [0x00000000fab00000,0x00000000fc800000,0x00000000fc800000)
  from space 28672K, 0% used [0x00000000fe400000,0x00000000fe400000,0x0000000100000000)
  to   space 28672K, 0% used [0x00000000fc800000,0x00000000fc800000,0x00000000fe400000)
 ParOldGen       total 175104K, used 174894K [0x00000000f0000000, 0x00000000fab00000, 0x00000000fab00000)
  object space 175104K, 99% used [0x00000000f0000000,0x00000000faacba08,0x00000000fab00000)
 Metaspace       used 3314K, capacity 4500K, committed 4864K, reserved 1056768K
  class space    used 362K, capacity 388K, committed 512K, reserved 1048576K
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at GcTest.generateGarbage(GcTest.java:47)
	at GcTest.main(GcTest.java:24)
```

5. 结论：

   1. 本次GC一共发生47次，其中12次`minor GC`，35次`Full GC`。

   2. 最后的内存分布是这样的，新生代总共58M，老年代175M，那整个堆内存，大概是占用了58+175 = 233M。

   3. 最后一次的`Full GC`，已经回收不掉任何对象了，此时再新增对象，发现已经申请不到空间了，所以就报`OOM`了。

   4. 本次gc使用的是并行GC，从每一次的GC的日志来看，STW的时间维持在10-20ms以内。最大的一次STW时间，是30ms，整体来说还算稳定。

   5. 对Young gc分析，` [GC (Allocation Failure) # Allocation 指的是分配速率  Failure表示是因为在年轻代中没有足够的空间能够存储新的数据了。` 

      `[PSYoungGen: 65384K->10732K(76288K)] # 新生代一开始65M,经过这一次gc后，还剩10M还没回收，一共回收了65-10 = 55M，总共还有76M，这个我是截取的是初始的JVM日志，我理解76M是新生代最大申请到的内存空间 ，那65M指的其实应该是Eden区，大概占用(65/76=85%)的新生代空间`

      `65384K->21803K(251392K),# 堆一开始65M,经过这一次gc后，还剩21M还没回收，一共回收了65-21 = 44M，总共还有251M，配合前面的新生代情况，可以得出21-10=11M的对象已经在老年代了 `

       `0.0025530 secs # 本次GC花费2ms] `

      `[Times: user=0.08 sys=0.14, real=0.00 secs # CPU消耗情况]`