# Apache Flink  
- [Apache Flink](#apache-flink)
- [Stateful Computations over Data Streams](#stateful-computations-over-data-streams)
  * [1. Streaming Processing Concepts (common concepts for stream processing)](#1-streaming-processing-concepts--common-concepts-for-stream-processing-)
  * [2.Architecture](#2architecture)
  * [3.State Management](#3state-management)
  * [4.DataStream](#4datastream)
  * [5.Libraries](#5libraries)
  * [6.Table API & SQL](#6table-api---sql)
  * [7.Deployment and Operations](#7deployment-and-operations)
  * [8.Debugging and Monitoring](#8debugging-and-monitoring)
  * [9.Ecosystem](#9ecosystem)
  * [10.Use Cases](#10use-cases)
- [问题](#--)
- [参考资料](#----)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>


# Apache Flink  
[TOC]

- [Apache Flink](#apache-flink)
- [Stateful Computations over Data Streams](#stateful-computations-over-data-streams)
  * [1.Streaming Processing Concepts (common concepts for stream processing)](#1-streaming-processing-concepts--common-concepts-for-stream-processing-)
  * [2.Architecture](#2-architecture)
  * [3.State Management](#3-state-management)
  * [4.DataStream](#4-datastream)
  * [5.Libraries](#5-libraries)
  * [6.Table API & SQL](#6-table-api---sql)
  * [7.Deployment and Operations](#7-deployment-and-operations)
  * [8.Debugging and Monitoring](#8-debugging-and-monitoring)
  * [9.Ecosystem](#9-ecosystem)
  * [10.Use Cases](#10-use-cases)
- [问题](#--)
- [参考资料](#----)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>


#  Stateful Computations over Data Streams 

## 1. Streaming Processing Concepts (common concepts for stream processing)

* Bounded and Unbounded Data and Processing 
* Latency and Throughput 
* Time Semantics 
  * Processing Time 
  * Event Time 
  * Watermarks 
  * Window
  * Trigger

## 2.Architecture

* Layered APIs 
  * SQL/Table API (dynamic tables)
  * DataStream API (stream, windows)
  * ProcessFunction(events, state, time)
* Components of a Flink Setup 
  * JobManager (also called masters )
  * TaskManager(also called workers)
  * Clients
* Task Execution
  * Operators
  * Tasks
    * Setting Parallelism
      * Operator Level 
      * Execution Environment Level 
      * Client Level 
      * System Level (set parallelism in flink-conf.yaml)
    * Task Failure Recovery 
      * Restart Strategies
      * Failover Strategies 
  * Slots and Resources  

## 3.State Management

* State Backend 
  * MemoryStateBackend
  * FsStateBackend
  * RocksDBStateBacked
* Kinds of state in Flink 
  * Operator State 
    * List state 
    * Union List state 
    * Broadcast state 
  * Keyed State 
    * Value state 
    * List state 
    * Map state 
* Fault Tolerance (容错)
  * Checkpointing
    * Barriers
    * Exactly Once & At Least Once 
    * Asynchronous State Snapshots
    * Checkpointing Algorithm(Chandy-Lamport)
    * Incremental Checkpointing
  * Savepoints
* State Rescalable 
* Queryable Statue 
* State Schema Evolution 
* State Processor API （read ,write ,and modify savepoints and chekpoints using Flink's batch DataSet API）

## 4.DataStream

* Setup Environment (Local and Remote)
* Source 
  * Built-in Source(readTextFile,fromCollection,etc)
  * Custom Source (addSource)
* Sink 
  * Built-in Sink(writeAsText,writeAsCsv,etc)
  * Custom Source(addSink)
* Transformations
  * Basic Transformations
    * Map
    * Filter
    * FlatMap
  * KeyedStream Transformations
    * KeyBy
    * Aggregations
    * Reduce
  * Multistream Transformations
    * Union
    * Connect, coMap, coFlatMap
    * Split & select
  * Distribution Transformations 
    * Random
    * Round-Robin
    * Rescale
    * Broadcast
    * Global 
    * Custom
* Data Types
  * Types
  * TypeInformation
* Functions
* Iterations
* Time-Based and Window Operators
  * Time characteristics
    * Processing Time 
    * Event Time 
    * Ingestion Time
  * Assign Timestamps and generating watemarks
  * ProcessFunction 
    * TimeService and Timer
    * Emitting to Side Outputs（发射至侧面输出）
  * Windows
    * Window Assigners (窗口分配器)
    * Windos Functions 
    * Triggers (触发器)
    * Evictors (驱逐者)
    * Allowed Lateness （允许延迟）
    * Side Output (侧输出)
  * Joining
    * Windows Join 
      * Tumbling Window Join 
      * Sliding Window Join 
      * Session Window Join 
    * Interval Join 
  * 
* Async I/O

## 5.Libraries

* CEP (Complex Event Processing)
* State Processor API
* Gelly

## 6.Table API & SQL

* Streaming Concepts 
  * Dynamic Tables
  * Time Attributes
    * Processing Time 
    * Event Time 
  * Temporal Tables
  * Query Configuration
* SQL
  * DDL 
  * Query
  * SQL Client
* Table API
  * Java/ScalaTable API
  * Python Table API
* Functions
  * Built-in Functions
  * User Defined Functions
* Connect to External Systems
  * Table Connectors
  * Table Formats
  * Table Schema
  * Update Modes
  * User-defined Table Source & Sinks
* Planners
  * Flink Planner
  * Blink Planner 
* Modules
* Catalog
  * Register an External Catalog
  * Register Tables in Catalog
* Hive Integration 

## 7.Deployment and Operations

* Deployment Modes
  * Local Cluster
  * Standalone Cluster 
  * YARN
  * Mesos
  * Docker
  * Kubernetes
* High Availability
  * Standalone Cluster High Availability
  * YARN Cluster High Availability
* Command-Line Interface
  * Job Submission
  * Job Management
  * Savepoints
* Python REPL
* Scala REPL
* Security
  * Kerberos
  * SSL
* File Systems (consume and persistently store data)
  * Local File System 
  * Pluggable File Systems
    * Amazon S3
    * MapR FS
    * OpenStack Swift FS
    * Aliyun Object Storage Service
    * Azure Blob Storage
  * HDFS and Hadoop File System Support

## 8.Debugging and Monitoring

* Metrics
* Logging 
* Flink Web UI
  * Monitoring Checkpointing 
  * Monitoring Back Pressure 
  * Monitoring Watermark 
* Flink Rest API
  * Managing and monitoring a Flink Cluster
  * Managing and monitoring Flink  Applictions
* Application Profiling 
  * Profiling with Java Filght Recorder
  * Profiling with JITWatch

## 9.Ecosystem

*  Connectors
  * File System 
    * Hive
    * HDFS
  * Queue
    * Apache Kafka
    * Amazon Kinesis Streams
    * RabbitMQ
  * Key-Value
    * Cassandra
    * Redis
    * Hbase
  * Search Engine 
    * Elasticsearch
  * Services 
    * Twitter
* Third-Party Projects
  * Liabraries
    * SAMOA
    * Mahout
    * flink-htm
    * Tink
  * System extensions
    * Cascading
    * GRADOOP
    * FastR
    * BigPetStore
    * Zeppeline
    * Beam
    * Alluxio
    * Ignite
    * FlinkK8sOperator

## 10.Use Cases

* Application types

  * Event-driven Applications

    ![image-Apache-Flink-01](https://raw.githubusercontent.com/yichao0803/myflink/master/image/image-Apache-Flink-01.png)
    
  * Data Analytics Applications 

    ![image-Apache-Flink-02](https://raw.githubusercontent.com/yichao0803/myflink/master/image/image-Apache-Flink-02.png)
  * Data Pipeline Applications

    ![image-Apache-Flink-03](https://raw.githubusercontent.com/yichao0803/myflink/master/image/image-Apache-Flink-03.png))

* Domain Type

  * Datawarehouse(数仓)
  * OLAP
  * AI

* Company

  ![image-Apache-Flink-04](https://raw.githubusercontent.com/yichao0803/myflink/master/image/image-Apache-Flink-04.png)



# 问题

模块 [6.Table API & SQL] 尚未细化


#  参考资料

[Apache Flink China 社区电子书合集](https://ververica.cn/developers/special-issue/)