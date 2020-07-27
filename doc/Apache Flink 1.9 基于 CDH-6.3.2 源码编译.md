# Apache Flink 1.9.3  基于 CDH-6.3.2 源码编译

## 1、修改 maven 的仓库地址

```shell
root@node01 cloudera]# cat /usr/share/maven/conf/settings.xml
...
  </mirrors>
        <mirror>
                <id>alimaven</id>
                <name>aliyun maven</name>
                <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
                <mirrorOf>central</mirrorOf>
        </mirror>
  </mirrors>
...
```

## 2、下载解压 flink-1.9.3 源码，并查看 flink-shaded 版本

```shell
[root@node01 cloudera]# wget https://mirrors.tuna.tsinghua.edu.cn/apache/flink/flink-1.9.3/flink-1.9.3-src.tgz
[root@node01 cloudera]# tar -zxvf flink-1.9.3-src.tgz
[root@node01 cloudera]# cat flink-1.9.3/pom.xml | grep flink.shaded.version
        <flink.shaded.version>7.0</flink.shaded.version>
                <version>6.2.1-${flink.shaded.version}</version>
                <version>18.0-${flink.shaded.version}</version>
                <version>4.1.32.Final-${flink.shaded.version}</version>
                <version>2.0.25.Final-${flink.shaded.version}</version>
...
```

## 3、下载解压 flink-shaded-7.0 源码

```shell
[root@node01 cloudera]# wget https://archive.apache.org/dist/flink/flink-shaded-7.0/flink-shaded-7.0-src.tgz 
```

## 4、修改 flink 和 flink-shaded 的 pom.xml 文件

```xml
<repositories>
    <repository>
        <id>cloudera</id>
        <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
        <name>Cloudera Repositories</name>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

## 5、编译 flink-shaded 并安装到本地仓库

mvn -T4C clean install -DskipTests -Dhadoop.version=3.0.0-cdh6.3.2 -Dzookeeper.version=3.4.5-cdh6.3.2
注意:
需在 flink-shaded-9.0/flink-shaded-hadoop-2-uber/pom.xml 中的 dependencies 标签中添加如下依赖，否则运行启动时会出现 java.lang.NoSuchMethodError: org.apache.commons.cli.Option.builder(Ljava/lang/String;)Lorg/apache/commons/cli/Option$Builder; 异常。
原因:
默认使用的是 commons-cli 的 1.2 版本，该版本中的 Option 中不粗拿在 Option$Builder 内部类。

```xml
<dependency>
     <groupId>commons-cli</groupId>
     <artifactId>commons-cli</artifactId>
     <version>1.3.1</version>
</dependency>
```

**编译运行:**

```shell
[root@node01 flink-shaded-7.0]# mvn -T4C clean install -DskipTests -Dhadoop.version=3.0.0-cdh6.3.2 -Dzookeeper.version=3.4.5-cdh6.3.2
...
[INFO] Reactor Summary:
[INFO] 
[INFO] flink-shaded ...................................... SUCCESS [2.562s]
[INFO] flink-shaded-force-shading ........................ SUCCESS [1.550s]
[INFO] flink-shaded-asm-6 ................................ SUCCESS [2.945s]
[INFO] flink-shaded-guava-18 ............................. SUCCESS [4.558s]
[INFO] flink-shaded-netty-4 .............................. SUCCESS [7.088s]
[INFO] flink-shaded-netty-tcnative-dynamic ............... SUCCESS [4.131s]
[INFO] flink-shaded-jackson-parent ....................... SUCCESS [0.380s]
[INFO] flink-shaded-jackson-2 ............................ SUCCESS [4.371s]
[INFO] flink-shaded-jackson-module-jsonSchema-2 .......... SUCCESS [4.132s]
[INFO] flink-shaded-hadoop-2 ............................. SUCCESS [25.811s]
[INFO] flink-shaded-hadoop-2-uber ........................ SUCCESS [27.971s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 56.584s (Wall Clock)
[INFO] Finished at: Wed Apr 22 13:55:07 CST 2020
[INFO] Final Memory: 34M/3161M
[INFO] ------------------------------------------------------------------------
————————————————
版权声明：本文为CSDN博主「storm_fury」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_43215250/java/article/details/105699752
```

**问题:** 如果出现 `For artifact {org.apache.zookeeper:zookeeper:null:jar}`，异常如下:

```shell
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-remote-resources-plugin:1.5:process (process-resource-bundles) on project flink-shaded-hadoop-2-uber: Execution process-resource-bundles of goal org.apache.maven.plugins:maven-remote-resources-plugin:1.5:process failed: For artifact {org.apache.zookeeper:zookeeper:null:jar}: The version cannot be empty. -> [Help 1]
```

**解决办法:**
修改 flink-shaded-hadoop-2-uber下的pom.xml文件，增加zookeeper版本信息后再重新编译。

```xml
...
<properties>
    <hadoop.version>2.4.1</hadoop.version>
    <!-- 增加zookeeper版本信息 -->
    <zookeeper.version>3.4.5-cdh6.3.2</zookeeper.version>
</properties>
...
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <!-- 增加zookeeper版本信息 -->
    <version>${zookeeper.version}</version>
    <exclusions>
.... 
```

## 6、编译 flink 源码

修改 flink-connectors/flink-hbase/pom.xml 文件

```xml
<properties>
    <!--<hbase.version>1.4.3</hbase.version>-->
    <hbase.version>2.1.0-cdh6.3.2</hbase.version>
</properties>
```

**运行编译**

```shell
mvn -T4C clean package -DskipTests -Pvendor-repos -Pinclude-hadoop -Dhadoop.version=3.0.0-cdh6.3.2 -Dhive.version=2.1.1-cdh6.3.2 -Dscala-2.11

mvn -T8C clean package -DskipTests -Pvendor-repos -Pinclude-hadoop -Dhadoop.version=3.0.0-cdh6.3.2 -Dhive.version=2.1.1-cdh6.3.2 -Dhive.version=2.1.1-cdh6.3.2 -Dscala-2.11  -Dmaven.javadoc.skip=true -Dcheckstyle.skip=true -Drat.skip=true -e
```

**参数说明:**

```shell
-T4C                 使用 4 线程编译
-Pinclude-hadoop     将 hadoop的 jar包，打入到lib/中
-Pvendor-repos       使用cdh、hdp 的 hadoop 需要添加该参数
-Dhadoop.version=3.0.0-cdh6.3.2  指定 hadoop 的版本
-Dhive.version=2.1.1-cdh6.3.2    指定 hive 的版本
-Dscala-2.11         指定scala的版本为2.11

```

**运行日志:**

```shell

[INFO] Dependency-reduced POM written at: /root/flink/flink-yarn-tests/target/dependency-reduced-pom.xml
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] force-shading ...................................... SUCCESS [  1.562 s]
[INFO] flink .............................................. SUCCESS [  1.826 s]
[INFO] flink-annotations .................................. SUCCESS [  2.796 s]
[INFO] flink-shaded-curator ............................... SUCCESS [  4.307 s]
[INFO] flink-metrics ...................................... SUCCESS [  0.675 s]
[INFO] flink-metrics-core ................................. SUCCESS [  3.094 s]
[INFO] flink-test-utils-parent ............................ SUCCESS [  0.696 s]
[INFO] flink-test-utils-junit ............................. SUCCESS [  3.037 s]
[INFO] flink-core ......................................... SUCCESS [ 14.235 s]
[INFO] flink-java ......................................... SUCCESS [  3.673 s]
[INFO] flink-queryable-state .............................. SUCCESS [  0.136 s]
[INFO] flink-queryable-state-client-java .................. SUCCESS [  1.082 s]
[INFO] flink-filesystems .................................. SUCCESS [  0.176 s]
[INFO] flink-hadoop-fs .................................... SUCCESS [  6.619 s]
[INFO] flink-runtime ...................................... SUCCESS [ 52.700 s]
[INFO] flink-scala ........................................ SUCCESS [01:31 min]
[INFO] flink-mapr-fs ...................................... SUCCESS [  2.388 s]
[INFO] flink-filesystems :: flink-fs-hadoop-shaded ........ SUCCESS [  6.648 s]
[INFO] flink-s3-fs-base ................................... SUCCESS [ 12.930 s]
[INFO] flink-s3-fs-hadoop ................................. SUCCESS [ 10.969 s]
[INFO] flink-s3-fs-presto ................................. SUCCESS [ 19.427 s]
[INFO] flink-swift-fs-hadoop .............................. SUCCESS [ 36.020 s]
[INFO] flink-oss-fs-hadoop ................................ SUCCESS [ 22.753 s]
[INFO] flink-azure-fs-hadoop .............................. SUCCESS [ 16.714 s]
[INFO] flink-optimizer .................................... SUCCESS [  2.641 s]
[INFO] flink-clients ...................................... SUCCESS [  1.710 s]
[INFO] flink-streaming-java ............................... SUCCESS [  9.466 s]
[INFO] flink-test-utils ................................... SUCCESS [  8.908 s]
[INFO] flink-runtime-web .................................. SUCCESS [02:34 min]
[INFO] flink-examples ..................................... SUCCESS [  0.268 s]
[INFO] flink-examples-batch ............................... SUCCESS [ 17.527 s]
[INFO] flink-connectors ................................... SUCCESS [  0.179 s]
[INFO] flink-hadoop-compatibility ......................... SUCCESS [  8.119 s]
[INFO] flink-state-backends ............................... SUCCESS [  0.692 s]
[INFO] flink-statebackend-rocksdb ......................... SUCCESS [  7.235 s]
[INFO] flink-tests ........................................ SUCCESS [ 32.665 s]
[INFO] flink-streaming-scala .............................. SUCCESS [ 53.025 s]
[INFO] flink-table ........................................ SUCCESS [  0.696 s]
[INFO] flink-table-common ................................. SUCCESS [  2.327 s]
[INFO] flink-table-api-java ............................... SUCCESS [  1.670 s]
[INFO] flink-table-api-java-bridge ........................ SUCCESS [  5.761 s]
[INFO] flink-table-api-scala .............................. SUCCESS [ 13.226 s]
[INFO] flink-table-api-scala-bridge ....................... SUCCESS [ 13.425 s]
[INFO] flink-sql-parser ................................... SUCCESS [  9.792 s]
[INFO] flink-libraries .................................... SUCCESS [  0.158 s]
[INFO] flink-cep .......................................... SUCCESS [  9.668 s]
[INFO] flink-table-planner ................................ SUCCESS [02:59 min]
[INFO] flink-orc .......................................... SUCCESS [ 41.299 s]
[INFO] flink-jdbc ......................................... SUCCESS [ 24.730 s]
[INFO] flink-table-runtime-blink .......................... SUCCESS [  8.923 s]
[INFO] flink-table-planner-blink .......................... SUCCESS [03:56 min]
[INFO] flink-hbase ........................................ SUCCESS [58:26 min]
[INFO] flink-hcatalog ..................................... SUCCESS [01:02 min]
[INFO] flink-metrics-jmx .................................. SUCCESS [  2.448 s]
[INFO] flink-connector-kafka-base ......................... SUCCESS [ 56.503 s]
[INFO] flink-connector-kafka-0.9 .......................... SUCCESS [ 40.684 s]
[INFO] flink-connector-kafka-0.10 ......................... SUCCESS [ 22.682 s]
[INFO] flink-connector-kafka-0.11 ......................... SUCCESS [ 22.916 s]
[INFO] flink-formats ...................................... SUCCESS [  0.152 s]
[INFO] flink-json ......................................... SUCCESS [  4.551 s]
[INFO] flink-connector-elasticsearch-base ................. SUCCESS [04:28 min]
[INFO] flink-connector-elasticsearch2 ..................... SUCCESS [  9.800 s]
[INFO] flink-connector-elasticsearch5 ..................... SUCCESS [06:41 min]
[INFO] flink-connector-elasticsearch6 ..................... SUCCESS [07:48 min]
[INFO] flink-csv .......................................... SUCCESS [  0.424 s]
[INFO] flink-connector-hive ............................... SUCCESS [  01:30 h]
[INFO] flink-connector-rabbitmq ........................... SUCCESS [  4.636 s]
[INFO] flink-connector-twitter ............................ SUCCESS [  8.989 s]
[INFO] flink-connector-nifi ............................... SUCCESS [02:55 min]
[INFO] flink-connector-cassandra .......................... SUCCESS [07:52 min]
[INFO] flink-avro ......................................... SUCCESS [  8.661 s]
[INFO] flink-connector-filesystem ......................... SUCCESS [  9.138 s]
[INFO] flink-connector-kafka .............................. SUCCESS [01:13 min]
[INFO] flink-connector-gcp-pubsub ......................... SUCCESS [ 32.890 s]
[INFO] flink-sql-connector-elasticsearch6 ................. SUCCESS [  6.272 s]
[INFO] flink-sql-connector-kafka-0.9 ...................... SUCCESS [  0.308 s]
[INFO] flink-sql-connector-kafka-0.10 ..................... SUCCESS [  0.946 s]
[INFO] flink-sql-connector-kafka-0.11 ..................... SUCCESS [  0.572 s]
[INFO] flink-sql-connector-kafka .......................... SUCCESS [  0.969 s]
[INFO] flink-connector-kafka-0.8 .......................... SUCCESS [ 33.034 s]
[INFO] flink-avro-confluent-registry ...................... SUCCESS [ 46.461 s]
[INFO] flink-parquet ...................................... SUCCESS [01:14 min]
[INFO] flink-sequence-file ................................ SUCCESS [  0.495 s]
[INFO] flink-examples-streaming ........................... SUCCESS [ 15.486 s]
[INFO] flink-examples-table ............................... SUCCESS [ 28.537 s]
[INFO] flink-examples-build-helper ........................ SUCCESS [  0.149 s]
[INFO] flink-examples-streaming-twitter ................... SUCCESS [  0.750 s]
[INFO] flink-examples-streaming-state-machine ............. SUCCESS [  0.446 s]
[INFO] flink-examples-streaming-gcp-pubsub ................ SUCCESS [  3.710 s]
[INFO] flink-container .................................... SUCCESS [  5.100 s]
[INFO] flink-queryable-state-runtime ...................... SUCCESS [  2.803 s]
[INFO] flink-end-to-end-tests ............................. SUCCESS [  0.660 s]
[INFO] flink-cli-test ..................................... SUCCESS [  3.824 s]
[INFO] flink-parent-child-classloading-test-program ....... SUCCESS [  4.348 s]
[INFO] flink-parent-child-classloading-test-lib-package ... SUCCESS [  2.386 s]
[INFO] flink-dataset-allround-test ........................ SUCCESS [  0.361 s]
[INFO] flink-dataset-fine-grained-recovery-test ........... SUCCESS [  0.386 s]
[INFO] flink-datastream-allround-test ..................... SUCCESS [  4.718 s]
[INFO] flink-batch-sql-test ............................... SUCCESS [  1.176 s]
[INFO] flink-stream-sql-test .............................. SUCCESS [  0.266 s]
[INFO] flink-bucketing-sink-test .......................... SUCCESS [  3.176 s]
[INFO] flink-distributed-cache-via-blob ................... SUCCESS [  3.786 s]
[INFO] flink-high-parallelism-iterations-test ............. SUCCESS [  5.706 s]
[INFO] flink-stream-stateful-job-upgrade-test ............. SUCCESS [  3.737 s]
[INFO] flink-queryable-state-test ......................... SUCCESS [  3.399 s]
[INFO] flink-local-recovery-and-allocation-test ........... SUCCESS [  0.810 s]
[INFO] flink-elasticsearch2-test .......................... SUCCESS [  2.508 s]
[INFO] flink-elasticsearch5-test .......................... SUCCESS [  2.546 s]
[INFO] flink-elasticsearch6-test .......................... SUCCESS [  3.559 s]
[INFO] flink-quickstart ................................... SUCCESS [  1.778 s]
[INFO] flink-quickstart-java .............................. SUCCESS [  1.959 s]
[INFO] flink-quickstart-scala ............................. SUCCESS [  1.839 s]
[INFO] flink-quickstart-test .............................. SUCCESS [  0.229 s]
[INFO] flink-confluent-schema-registry .................... SUCCESS [  2.025 s]
[INFO] flink-stream-state-ttl-test ........................ SUCCESS [ 12.448 s]
[INFO] flink-sql-client-test .............................. SUCCESS [  0.451 s]
[INFO] flink-streaming-file-sink-test ..................... SUCCESS [  4.644 s]
[INFO] flink-state-evolution-test ......................... SUCCESS [  4.490 s]
[INFO] flink-mesos ........................................ SUCCESS [01:02 min]
[INFO] flink-yarn ......................................... SUCCESS [  3.044 s]
[INFO] flink-gelly ........................................ SUCCESS [  9.140 s]
[INFO] flink-gelly-scala .................................. SUCCESS [ 32.498 s]
[INFO] flink-gelly-examples ............................... SUCCESS [ 14.530 s]
[INFO] flink-metrics-dropwizard ........................... SUCCESS [  0.723 s]
[INFO] flink-metrics-graphite ............................. SUCCESS [  0.425 s]
[INFO] flink-metrics-influxdb ............................. SUCCESS [  1.666 s]
[INFO] flink-metrics-prometheus ........................... SUCCESS [  1.109 s]
[INFO] flink-metrics-statsd ............................... SUCCESS [  0.723 s]
[INFO] flink-metrics-datadog .............................. SUCCESS [  1.425 s]
[INFO] flink-metrics-slf4j ................................ SUCCESS [  0.694 s]
[INFO] flink-cep-scala .................................... SUCCESS [ 15.172 s]
[INFO] flink-table-uber ................................... SUCCESS [  7.296 s]
[INFO] flink-table-uber-blink ............................. SUCCESS [  2.252 s]
[INFO] flink-sql-client ................................... SUCCESS [01:17 min]
[INFO] flink-state-processor-api .......................... SUCCESS [  0.920 s]
[INFO] flink-python ....................................... SUCCESS [  8.156 s]
[INFO] flink-scala-shell .................................. SUCCESS [ 36.040 s]
[INFO] flink-dist ......................................... SUCCESS [ 17.387 s]
[INFO] flink-end-to-end-tests-common ...................... SUCCESS [  0.622 s]
[INFO] flink-metrics-availability-test .................... SUCCESS [  0.158 s]
[INFO] flink-metrics-reporter-prometheus-test ............. SUCCESS [  0.149 s]
[INFO] flink-heavy-deployment-stress-test ................. SUCCESS [ 16.983 s]
[INFO] flink-connector-gcp-pubsub-emulator-tests .......... SUCCESS [07:27 min]
[INFO] flink-streaming-kafka-test-base .................... SUCCESS [  3.631 s]
[INFO] flink-streaming-kafka-test ......................... SUCCESS [  6.609 s]
[INFO] flink-streaming-kafka011-test ...................... SUCCESS [  4.618 s]
[INFO] flink-streaming-kafka010-test ...................... SUCCESS [  6.604 s]
[INFO] flink-plugins-test ................................. SUCCESS [  1.079 s]
[INFO] dummy-fs ........................................... SUCCESS [  0.432 s]
[INFO] another-dummy-fs ................................... SUCCESS [  0.452 s]
[INFO] flink-tpch-test .................................... SUCCESS [  3.913 s]
[INFO] flink-contrib ...................................... SUCCESS [  0.159 s]
[INFO] flink-connector-wikiedits .......................... SUCCESS [  4.611 s]
[INFO] flink-yarn-tests ................................... SUCCESS [  3.433 s]
[INFO] flink-fs-tests ..................................... SUCCESS [  8.440 s]
[INFO] flink-docs ......................................... SUCCESS [ 12.579 s]
[INFO] flink-ml-parent .................................... SUCCESS [  0.681 s]
[INFO] flink-ml-api ....................................... SUCCESS [  1.005 s]
[INFO] flink-ml-lib ....................................... SUCCESS [  0.341 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:42 h (Wall Clock)
[INFO] Finished at: 2020-07-06T00:45:09+08:00
[INFO] Final Memory: 396M/1317M
[INFO] ------------------------------------------------------------------------

```

**问题1：** 出现如下 `org.apache.hadoop.yarn.api.records.ApplicationReport.newInstance` 不可用，信息如下:

```shell
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  08:18 min (Wall Clock)
[INFO] Finished at: 2019-07-27T13:18:30+08:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.0:testCompile (default-testCompile) on project flink-yarn_2.11: Compilation failure
[ERROR] /opt/flink2/flink-yarn/src/test/java/org/apache/flink/yarn/AbstractYarnClusterTest.java:[89,41] no suitable method found for newInstance(org.apache.hadoop.yarn.api.records.ApplicationId,org.apache.hadoop.yarn.api.records.ApplicationAttemptId,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,<nulltype>,org.apache.hadoop.yarn.api.records.YarnApplicationState,<nulltype>,<nulltype>,long,long,org.apache.hadoop.yarn.api.records.FinalApplicationStatus,<nulltype>,<nulltype>,float,<nulltype>,<nulltype>)
[ERROR]     method org.apache.hadoop.yarn.api.records.ApplicationReport.newInstance(org.apache.hadoop.yarn.api.records.ApplicationId,org.apache.hadoop.yarn.api.records.ApplicationAttemptId,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,org.apache.hadoop.yarn.api.records.Token,org.apache.hadoop.yarn.api.records.YarnApplicationState,java.lang.String,java.lang.String,long,long,long,org.apache.hadoop.yarn.api.records.FinalApplicationStatus,org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport,java.lang.String,float,java.lang.String,org.apache.hadoop.yarn.api.records.Token) is not applicable
[ERROR]       (actual and formal argument lists differ in length)
[ERROR]     method org.apache.hadoop.yarn.api.records.ApplicationReport.newInstance(org.apache.hadoop.yarn.api.records.ApplicationId,org.apache.hadoop.yarn.api.records.ApplicationAttemptId,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,org.apache.hadoop.yarn.api.records.Token,org.apache.hadoop.yarn.api.records.YarnApplicationState,java.lang.String,java.lang.String,long,long,org.apache.hadoop.yarn.api.records.FinalApplicationStatus,org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport,java.lang.String,float,java.lang.String,org.apache.hadoop.yarn.api.records.Token,java.util.Set<java.lang.String>,boolean,org.apache.hadoop.yarn.api.records.Priority,java.lang.String,java.lang.String) is not applicable
[ERROR]       (actual and formal argument lists differ in length)
[ERROR]     method org.apache.hadoop.yarn.api.records.ApplicationReport.newInstance(org.apache.hadoop.yarn.api.records.ApplicationId,org.apache.hadoop.yarn.api.records.ApplicationAttemptId,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,org.apache.hadoop.yarn.api.records.Token,org.apache.hadoop.yarn.api.records.YarnApplicationState,java.lang.String,java.lang.String,long,long,long,org.apache.hadoop.yarn.api.records.FinalApplicationStatus,org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport,java.lang.String,float,java.lang.String,org.apache.hadoop.yarn.api.records.Token,java.util.Set<java.lang.String>,boolean,org.apache.hadoop.yarn.api.records.Priority,java.lang.String,java.lang.String) is not applicable
[ERROR]       (actual and formal argument lists differ in length)
[ERROR]
[ERROR] -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
[ERROR]
[ERROR] After correcting the problems, you can resume the build with the command
[ERROR]   mvn <goals> -rf :flink-yarn_2.11

```

**解决办法:**
在 flink-yarn/pom.xml 文件的 build 中添加如下插件，跳过本模块的测试代码的编译。

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <source>${java.version}</source>
        <target>${java.version}</target>
        <!-- 略过测试代码的编译 -->
        <skip>true</skip>
        <!-- The semantics of this option are reversed, see MCOMPILER-209. -->
        <useIncrementalCompilation>false</useIncrementalCompilation>
        <compilerArgs>
             <!-- Prevents recompilation due to missing package-info.class, see MCOMPILER-205 -->
            <arg>-Xpkginfo:always</arg>
        </compilerArgs>
    </configuration>
</plugin> 
```

**问题2:** 出现如下 `/AbstractTableInputFormat.java:[235,99] cannot find symbol` 异常，信息如下:

```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.0:compile (default-compile) on project flink-hbase_2.11: Compilation failure: Compilation failure: 
[ERROR] /data/github/cloudera/flink-1.9.3/flink-connectors/flink-hbase/src/main/java/org/apache/flink/addons/hbase/AbstractTableInputFormat.java:[235,99] cannot find symbol
[ERROR]   symbol:   method getTableName()
[ERROR]   location: variable table of type org.apache.hadoop.hbase.client.HTable
[ERROR] /data/github/cloudera/flink-1.9.3/flink-connectors/flink-hbase/src/main/java/org/apache/flink/addons/hbase/TableInputFormat.java:[84,32] constructor HTable in class org.apache.hadoop.hbase.client.HTable cannot be applied to given types;
[ERROR]   required: org.apache.hadoop.hbase.client.ClusterConnection,org.apache.hadoop.hbase.client.TableBuilderBase,org.apache.hadoop.hbase.client.RpcRetryingCallerFactory,org.apache.hadoop.hbase.ipc.RpcControllerFactory,java.util.concurrent.ExecutorService
[ERROR]   found: org.apache.hadoop.conf.Configuration,java.lang.String
[ERROR]   reason: actual and formal argument lists differ in length
[ERROR] -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
[ERROR] 
[ERROR] After correcting the problems, you can resume the build with the command
[ERROR]   mvn <args> -rf :flink-hbase_2.11
————————————————
版权声明：本文为CSDN博主「storm_fury」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/weixin_43215250/java/article/details/105699752
```

**解决办法:**
修改 `flink-connectors/flink-hbase/src/main/java/org/apache/flink/addons/hbase/AbstractTableInputFormat.java` 第235行源码:
将

```java
final TableInputSplit split = new TableInputSplit(id, hosts, table.getTableName(), splitStart, splitStop); 
```

改为

```java
final TableInputSplit split = new TableInputSplit(id, hosts, table.getName().getName(), splitStart, splitStop); 
```

**问题3:** 出现如下 `/AbstractTableInputFormat.java:[235,99] cannot find symbol` 异常，信息如下:

```shell
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.0:compile (default-compile) on project flink-hbase_2.11: Compilation failure
[ERROR] /data/github/cloudera/flink-1.9.3/flink-connectors/flink-hbase/src/main/java/org/apache/flink/addons/hbase/TableInputFormat.java:[84,32] constructor HTable in class org.apache.hadoop.hbase.client.HTable cannot be applied to given types;
[ERROR]   required: org.apache.hadoop.hbase.client.ClusterConnection,org.apache.hadoop.hbase.client.TableBuilderBase,org.apache.hadoop.hbase.client.RpcRetryingCallerFactory,org.apache.hadoop.hbase.ipc.RpcControllerFactory,java.util.concurrent.ExecutorService
[ERROR]   found: org.apache.hadoop.conf.Configuration,java.lang.String
[ERROR]   reason: actual and formal argument lists differ in length
[ERROR] 
[ERROR] -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
[ERROR] 
[ERROR] After correcting the problems, you can resume the build with the command
[ERROR]   mvn <args> -rf :flink-hbase_2.11
```

**解决办法:**
修改 `flink-connectors/flink-hbase/src/main/java/org/apache/flink/addons/hbase/TableInputFormat.java` 第84行源码:

```java
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

...
	
	private HTable createTable() {
		LOG.info("Initializing HBaseConfiguration");
		//use files found in the classpath
		org.apache.hadoop.conf.Configuration hConf = HBaseConfiguration.create();

		  /*try {
                return new HTable(hConf, getTableName());
        } */
		try (Connection connection = ConnectionFactory.createConnection(hConf);
			 Table connTable = connection.getTable(TableName.valueOf(getTableName()))) {
			// use table as needed, the table returned is lightweight
			if (connTable instanceof HTable) {
				return (HTable)connTable;
			}
		} catch (Exception e) {
			LOG.error("Error instantiating a new HTable instance", e);
		}
		return null;
	}
```

如出现测试代码编译错误，可参考上述 flink-yarn 模块解决办法，添加maven插件，跳过测试代码的编译。

**问题4：** flink-runtime-web_2.11: Failed to run task: 'npm ci --cache-max=0 --no-save' 

```shell
 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 07:09 min (Wall Clock)
[INFO] Finished at: 2020-07-04T16:11:30+08:00
[INFO] Final Memory: 278M/1761M
[INFO] ------------------------------------------------------------------------
[INFO] prepare-compile in 0 s
[INFO] compile in 4 s
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ flink-gelly-examples_2.11 ---
[ERROR] Failed to execute goal com.github.eirslett:frontend-maven-plugin:1.6:npm (npm install) on project flink-runtime-web_2.11: Failed to run task: 'npm ci --cache-max=0 --no-save' failed. org.apache.commons.exec.ExecuteException: Process exited with an error: -4048 (Exit value: -4048) -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal com.github.eirslett:frontend-maven-plugin:1.6:npm (npm install) on project flink-runtime-web_2.11: Failed to run task
        at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:212)
        at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:153)
        at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:145)
        at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject(LifecycleModuleBuilder.java:116)
        at org.apache.maven.lifecycle.internal.builder.multithreaded.MultiThreadedBuilder$1.call(MultiThreadedBuilder.java:188)
        at org.apache.maven.lifecycle.internal.builder.multithreaded.MultiThreadedBuilder$1.call(MultiThreadedBuilder.java:184)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at java.lang.Thread.run(Thread.java:748)
Caused by: org.apache.maven.plugin.MojoFailureException: Failed to run task
        at com.github.eirslett.maven.plugins.frontend.mojo.AbstractFrontendMojo.execute(AbstractFrontendMojo.java:100)
        at org.apache.maven.plugin.DefaultBuildPluginManager.executeMojo(DefaultBuildPluginManager.java:132)
        at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:208)
        ... 11 more
Caused by: com.github.eirslett.maven.plugins.frontend.lib.TaskRunnerException: 'npm ci --cache-max=0 --no-save' failed.
        at com.github.eirslett.maven.plugins.frontend.lib.NodeTaskExecutor.execute(NodeTaskExecutor.java:63)
        at com.github.eirslett.maven.plugins.frontend.mojo.NpmMojo.execute(NpmMojo.java:62)
        at com.github.eirslett.maven.plugins.frontend.mojo.AbstractFrontendMojo.execute(AbstractFrontendMojo.java:94)
        ... 13 more
Caused by: com.github.eirslett.maven.plugins.frontend.lib.ProcessExecutionException: org.apache.commons.exec.ExecuteException: Process exited with an error: -4048 (Exit value: -4048)
        at com.github.eirslett.maven.plugins.frontend.lib.ProcessExecutor.execute(ProcessExecutor.java:82)
        at com.github.eirslett.maven.plugins.frontend.lib.ProcessExecutor.executeAndRedirectOutput(ProcessExecutor.java:64)
        at com.github.eirslett.maven.plugins.frontend.lib.NodeExecutor.executeAndRedirectOutput(NodeExecutor.java:29)
        at com.github.eirslett.maven.plugins.frontend.lib.NodeTaskExecutor.execute(NodeTaskExecutor.java:58)
        ... 15 more
Caused by: org.apache.commons.exec.ExecuteException: Process exited with an error: -4048 (Exit value: -4048)
        at org.apache.commons.exec.DefaultExecutor.executeInternal(DefaultExecutor.java:404)
        at org.apache.commons.exec.DefaultExecutor.execute(DefaultExecutor.java:166)
        at com.github.eirslett.maven.plugins.frontend.lib.ProcessExecutor.execute(ProcessExecutor.java:74)
        ... 18 more
[ERROR]
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
[ERROR]
[ERROR] After correcting the problems, you can resume the build with the command
```

为flink-runtime-web/添加国内仓库，编辑flink-runtime-web/pom.xml。
Flink1.9.x的flink-runtime-web模块引入了frontend-maven-plugin依赖，并安装了node和部分依赖组件，添加国内仓库，否则会访问不到：

```xml
<nodeDownloadRoot>https://registry.npm.taobao.org/dist/</nodeDownloadRoot>
<npmDownloadRoot>https://registry.npmjs.org/npm/-/</npmDownloadRoot>
```

完整：

```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
        <artifactId>frontend-maven-plugin</artifactId>
        <version>1.6</version>
        <executions>
            <execution>
                <id>install node and npm</id>
                <goals>
                    <goal>install-node-and-npm</goal>
                </goals>
                <configuration>                    <nodeDownloadRoot>https://registry.npm.taobao.org/dist/</nodeDownloadRoot>
                    <npmDownloadRoot>https://registry.npmjs.org/npm/-/</npmDownloadRoot>
                    <nodeVersion>v10.9.0</nodeVersion>
                </configuration>
            </execution>
            <execution>
                <id>npm install</id>
                <goals>
                    <goal>npm</goal>
                </goals>
                <configuration>
                    <arguments>ci --cache-max=0 --no-save  --no-bin-links</arguments>
                    <environmentVariables>
                        <HUSKY_SKIP_INSTALL>true</HUSKY_SKIP_INSTALL>
                    </environmentVariables>
                </configuration>
            </execution>
            <execution>
                <id>npm run build</id>
                <goals>
                    <goal>npm</goal>
                </goals>
                <configuration>
                    <arguments>run build</arguments>
                </configuration>
            </execution>
        </executions>
        <configuration>
            <workingDirectory>web-dashboard</workingDirectory>
        </configuration>
</plugin>
```



## 7、打包

查看flink的lib下的依赖包

```shell
[root@node01 cloudera]#cd flink-1.9.3/flink-dist/target/flink-1.9.3-bin
[root@node01 flink-1.9.3-bin]# pwd
/data/github/cloudera/flink-1.9.3/flink-dist/target/flink-1.9.3-bin
[root@node01 flink-1.9.3-bin]# cd flink-1.9.3/lib/
[root@node01 lib]# ll
total 201652
-rw-r--r-- 1 root root 105437196 Apr 24 07:27 flink-dist_2.11-1.9.2.jar
-rw-r--r-- 1 root root  59612259 Apr 22 13:55 flink-shaded-hadoop-2-uber-3.0.0-cdh6.3.2-7.0.jar
-rw-r--r-- 1 root root  18751140 Apr 24 07:25 flink-table_2.11-1.9.2.jar
-rw-r--r-- 1 root root  22182832 Apr 24 07:27 flink-table-blink_2.11-1.9.2.jar
-rw-r--r-- 1 root root    489884 Apr 22 11:54 log4j-1.2.17.jar
-rw-r--r-- 1 root root      9931 Apr 22 11:54 slf4j-log4j12-1.7.15.jar

```

打包，由于是基于scala2.11编译的，压缩包的名称必须是：flink-1.9.3-bin-scala_2.11.tgz

```shell
[root@node01 flink-1.9.3-bin]# tar zcvf flink-1.9.3-bin-scala_2.11.tar.gz flink-1.9.3

[root@node01 flink-1.9.3-bin]# ll
total 310688
drwxr-xr-x 9 root root       126 Apr 24 07:27 flink-1.9.3
-rw-r--r-- 1 root root 318142073 Apr 24 08:41 flink-1.9.3-bin-scala_2.11.tar.gz
```

## 8、使用 flink-shaded-9.0 依赖版本编译

需修改以下内容，然后再执行编译打包:

* 修改 flink-1.9.3/pom.xml 文件
  将 flink-shaded-asm-6 修改为 flink-shaded-asm-7
  将 6.2.1-${flink.shaded.version} 修改为 7.1-${flink.shaded.version}
  将 4.1.32.Final 修改为 4.1.39.Final

```xml
<dependency>
  <groupId>org.apache.flink</groupId>
  <artifactId>flink-shaded-asm-7</artifactId>
  <version>7.1-${flink.shaded.version}</version>
</dependency>
...
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-shaded-netty</artifactId>
    <version>4.1.32.Final-${flink.shaded.version}</version>
</dependency>
```

* 修改 flink-core/pom.xml flink-java/pom.xml flink-scala/pom.xml flink-runtime/pom.xml 文件
  将 flink-shaded-asm-6 修改为 flink-shaded-asm-7

```xml
<dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-shaded-asm-7</artifactId>        
</dependency>
```

* 修改以下源码，将引入的包名 org.apache.flink.shaded.asm6 修改为 org.apache.flink.shaded.asm7

  flink-1.9.3/flink-core/src/main/java/org/apache/flink/api/java/typeutils/TypeExtractionUtils.java
  flink-1.9.3/flink-java/src/main/java/org/apache/flink/api/java/ClosureCleaner.java
  flink-1.9.3/flink-scala/src/main/scala/org/apache/flink/api/scala/ClosureCleaner.scala

## 参考资料：

* [Apache Flink 基于 CDH-6.3.2 源码编译](https://blog.csdn.net/weixin_43215250/article/details/105699752)

* [[Flink 学习] -- 编译 CDH-6.3.0 版本的 Flink 1.9.0](https://blog.csdn.net/high2011/article/details/102612080)

* [基于CDH-5.14.2编译Flink1.9.1的安装包](https://blog.csdn.net/u014310499/article/details/103815171)

  