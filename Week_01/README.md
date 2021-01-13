## 作业

1. 自己写一个简单Hello.java，涉及基本类型，四则运算，流程，分支，循环，自己分析一下对应的字节码。

   [源文件](./code/Hello.java)

   [字节码](./code/HelloByteCode.txt)

2. 自定义一个ClassLoader，加载一个Hello.xlass文件，执行hello方法。是通过这个小工具得到的， java -jar fileconvert.jar  Hello.class -> Hello.class.fck改名成 Hello.xlass

   [CustomClassLoader](./code/CustomClassLoader.java)

3. 图示Xmx,Xms,Xmn,Metaspace,DirectMemory,Xss

   [jvm参数表示](./note/jvm参数表示.jpg)

4. 检查一下自己维护的业务系统的jvm参数配置，用jstat和jstack，jmap查看详情，独立分析一下大概情况，思考有没有不合理的地方，如何改进。

5. 本机使用 G1 GC 启动一个程序，仿照课上案例分析一下 JVM 情况。
   可以使用 gateway-server-0.0.1-SNAPSHOT.jar 注意关闭自适应参数:-XX:-UseAdaptiveSizePolicy

   > java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseSerialGC -jar target/gateway-server- 0.0.1-SNAPSHOT.jar
   > java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseParallelGC -jar target/gateway-server- 0.0.1-SNAPSHOT.jar
   > java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseConcMarkSweepGC -jar target/gateway-server-0.0.1-SNAPSHOT.jar
   > java -Xmx1g -Xms1g -XX:-UseAdaptiveSizePolicy -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -jar target/gateway-server-0.0.1-SNAPSHOT.jar
   > 使用 jmap，jstat，jstack，以及可视化工具，查看 jvm 情况。 mac 上可以用 wrk，windows 上可以按照 superbenchmark 压测 [http://localhost:8088/api/hello ](http://localhost:8088/api/hello)查看 jvm。

