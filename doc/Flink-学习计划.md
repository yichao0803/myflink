# Flink 学习计划

## 基本知识

* 架构
* 状态管理
* DataStream
* Libraries
* TableAPI & SQL
* 部署和操作
* 调试和监控
* 链接器
* 用例

## 源码

### Runtime 模块

1. [Apache Flink 初探](http://matt33.com/2019/11/23/flink-learn-start-1/)，Flink 的 简介、架构、部署和示例，完成时间：2019-11-23；

   

### Graph 转换



1. StreamGraph 的转换：[Flink DataStream API 概述及 StreamGraph 如何转换](http://matt33.com/2019/12/08/flink-stream-graph-2/)，完成时间：2019-12-08；
2. JobGraph 的转换：[Flink Streaming 作业如何转化为 JobGraph](http://matt33.com/2019/12/09/flink-job-graph-3/)，完成时间：2019-12-10；
3. ExecutionGraph 的转换：[Flink 如何生成 ExecutionGraph](http://matt33.com/2019/12/20/flink-execution-graph-4/)，完成时间：2019-12-19；

### 调度模块

1. Flink Master：包含三部分：Resource Manager, Dispatcher and JobManager，主要介绍前两个，[Flink Master 详解](http://matt33.com/2019/12/23/flink-master-5/)，完成时间：2019-12-23；
2. [Flink JobManager 详解](http://matt33.com/2019/12/27/flink-jobmanager-6/)，完成时间：2019-12-27；
3. TaskManager 详解第一篇 [Flink TaskManager 详解（一）](http://matt33.com/2020/03/15/flink-taskmanager-7/)，计划时间：2020-01-05 前，完成时间：2020-03-15；
4. [Flink 基于 MailBox 实现的 StreamTask 线程模型](http://matt33.com/2020/03/20/flink-task-mailbox/)，完成时间：2020-03-22；
5. 调度模型，计划时间：2020-01-05 前；
6. Flink 中的 AKKA 应用；
7. Flink 中的 BlobServer；
8. TaskManager 的内存管理；
9. JobManager 的 HA 实现；

### state & checkpoint

1. StateBackend 实现；
2. Checkpoint 流程；

### on yarn

1. Yarn 提交流程详解；

### Shuffle 模块

1. 网络协议栈；
2. Shuffle 实现；

### SQL 模块

### 算子模块


## 参考资料

* [Apache Flink China 社区电子书合集](https://ververica.cn/developers/special-issue/)
* [Flink 源码分析](https://github.com/wangzzu/awesome/issues/28)
* [Flink 原理与实现：架构和拓扑概览](http://wuchong.me/blog/2016/05/03/flink-internals-overview/)
* [Flink CookBook—Apach Flink核心知识介绍](https://mp.weixin.qq.com/s/l-x3wSxuIvPMgxZzwYxZkA)
* [Flink SQL 系列 | 5 个 TableEnvironment 我该用哪个？](https://mp.weixin.qq.com/s/UeoOYX1n6pnedHh8VcY8OQ)
