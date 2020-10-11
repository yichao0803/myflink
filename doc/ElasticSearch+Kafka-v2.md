## Elasticsearch 
### 1、详细描述一下 Elasticsearch 搜索的过程？
**解答：**
搜索拆解为“query then fetch” 两个阶段。
query 阶段的目的：定位到位置，但不取。
步骤拆解如下：

* （1）假设一个索引数据有 5 主+1 副本 共 10 分片，一次请求会命中（主或者副本分片中）的一个。
* （2）每个分片在本地进行查询，结果返回到本地有序的优先队列中。
* （3）第 2）步骤的结果发送到协调节点，协调节点产生一个全局的排序列表。
fetch 阶段的目的：取数据。
路由节点获取所有文档，返回给客户端。

### 2、Elasticsearch 是如何实现 Master 选举的？

****解答：****

* （1）Elasticsearch 的选主是 ZenDiscovery 模块负责的，主要包含 Ping（节点之间通过这个 RPC 来发现彼此）和 Unicast（单播模块包含一个主机列表以控制哪些节点需要 ping 通）这两部分；
* （2）对所有可以成为 master 的节点（node.master: true）根据 nodeId 字典排序，每次选举每个节点都把自己所知道节点排一次序，然后选出第一个（第 0 位）节点，暂且认为它是 master 节点。
* （3）如果对某个节点的投票数达到一定的值（可以成为 master 节点数 n/2+1）并且该节点自己也选举自己，那这个节点就是 master。否则重新选举一直到满足上述条件。
* （4）补充：master 节点的职责主要包括集群、节点和索引的管理，不负责文档级别的管理；data 节点可以关闭 http 功能*。

### 3、ElasticSearch 中模糊查询、精确查询、聚合查询、AND 查询、OR 查询的关键字

**解答：** match、term、aggs、must、should

### 4、ElasticSearch  如何修改数据项类型

**解答：** ElasticSearch 的数据项类型，一旦创建是无法修改的；只能重新创建索引，来实现。

## kafka

### 1、Kafka中有哪些组件及组件作用?
**解答：** Kafka重要的组件是：

- Producer ：消息生产者，就是向 kafka broker 发消息的客户端。
- Consumer ：消息消费者，向 kafka broker 取消息的客户端。
- Topic ：可以理解为一个队列，一个 Topic 又分为一个或多个分区，
- Consumer Group：这是 kafka 用来实现一个 topic 消息的广播（发给所有的 consumer）和单播（发给任意一个 consumer）的手段。一个 topic 可以有多个 Consumer Group。
- Broker ：一台 kafka 服务器就是一个 broker。一个集群由多个 broker 组成。一个 broker 可以容纳多个 topic。
- Partition：为了实现扩展性，一个非常大的 topic 可以分布到多个 broker上，每个 partition 是一个有序的队列。partition 中的每条消息都会被分配一个有序的id（offset）。将消息发给 consumer，kafka 只保证按一个 partition 中的消息的顺序，不保证一个 topic 的整体（多个 partition 间）的顺序。
- Offset：kafka 的存储文件都是按照 offset.kafka 来命名，用 offset 做名字的好处是方便查找。例如你想找位于 2049 的位置，只要找到 2048.kafka 的文件即可。当然 the first offset 就是 00000000000.kafka。

### 2、数据传输的事物定义有哪三种？

**解答：** 数据传输的事务定义通常有以下三种级别：

*（1）最多一次: 消息不会被重复发送，最多被传输一次，但也有可能一次不传输
*（2）最少一次: 消息不会被漏发送，最少被传输一次，但也有可能被重复传输.
*（3）精确的一次（Exactly once）: 不会漏传输也不会重复传输,每个消息都传输被一次而

且仅仅被传输一次，这是大家所期望的

### 3、Kafka之——消费模式有那些？

**解答：**

**自动提交offset**

**手动提交offset**

**手动提交partition的offset**

### 4、kafka  基本命令

#### 创建名称为 demo 的 topic  

```bash 
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic demo --partitions 10 --replication-factor 3
```

#### 查看所有 topic 列表

```bash 
bin/kafka-topics.sh --zookeeper localhost:2181 --list
```

#### 查看指定 topic 明细

```bash
bin/kafka-topics.sh --zookeeper localhost:2181 --desc --topic demo
```

#### 删除 topic 

```bash 
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic demo
```

####  Producer  生产 message

```bash
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic demo
```

#### Consumer 消费 message 

```bash 
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic demo
```





