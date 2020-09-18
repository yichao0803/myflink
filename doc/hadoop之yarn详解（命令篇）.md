# [hadoop之yarn详解（命令篇）](https://www.cnblogs.com/zsql/p/11636348.html)



**目录**

- [一、yarn命令概述](https://www.cnblogs.com/zsql/p/11636348.html#_label0)
- 二、命令详解
  - [2.1、application](https://www.cnblogs.com/zsql/p/11636348.html#_label1_0)
  - [2.2、applicationattempt](https://www.cnblogs.com/zsql/p/11636348.html#_label1_1)
  - [2.3、classpath](https://www.cnblogs.com/zsql/p/11636348.html#_label1_2)
  - [2.4、container](https://www.cnblogs.com/zsql/p/11636348.html#_label1_3)
  - [2.5、jar](https://www.cnblogs.com/zsql/p/11636348.html#_label1_4)
  - [2.6、logs](https://www.cnblogs.com/zsql/p/11636348.html#_label1_5)
  - [2.7、node](https://www.cnblogs.com/zsql/p/11636348.html#_label1_6)
  - [2.8、queue](https://www.cnblogs.com/zsql/p/11636348.html#_label1_7)
  - [2.9、daemonlog](https://www.cnblogs.com/zsql/p/11636348.html#_label1_8)
  - [2.10、nodemanager](https://www.cnblogs.com/zsql/p/11636348.html#_label1_9)
  - [2.11、proxyserver](https://www.cnblogs.com/zsql/p/11636348.html#_label1_10)
  - [2.12、resourcemanager](https://www.cnblogs.com/zsql/p/11636348.html#_label1_11)
  - [2.13、rmadmin](https://www.cnblogs.com/zsql/p/11636348.html#_label1_12)
  - [2.14、scmadmin](https://www.cnblogs.com/zsql/p/11636348.html#_label1_13)
  - [2.15、 sharedcachemanager](https://www.cnblogs.com/zsql/p/11636348.html#_label1_14)
  - [2.16、timelineserver](https://www.cnblogs.com/zsql/p/11636348.html#_label1_15)

 

------

本篇主要对yarn命令进行阐述

[回到顶部](https://www.cnblogs.com/zsql/p/11636348.html#_labelTop)

## 一、yarn命令概述

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
[root@lgh ~]# yarn -help 
Usage: yarn [--config confdir] COMMAND
where COMMAND is one of:
  resourcemanager -format-state-store   deletes the RMStateStore
  resourcemanager                       run the ResourceManager
                                        Use -format-state-store for deleting the RMStateStore.
                                        Use -remove-application-from-state-store <appId> for 
                                            removing application from RMStateStore.
  nodemanager                           run a nodemanager on each slave
  timelineserver                        run the timeline server
  rmadmin                               admin tools
  version                               print the version
  jar <jar>                             run a jar file
  application                           prints application(s)
                                        report/kill application
  applicationattempt                    prints applicationattempt(s)
                                        report
  container                             prints container(s) report
  node                                  prints node report(s)
  queue                                 prints queue information
  logs                                  dump container logs
  classpath                             prints the class path needed to
                                        get the Hadoop jar and the
                                        required libraries
  daemonlog                             get/set the log level for each
                                        daemon
  top                                   run cluster usage tool
 or
  CLASSNAME                             run the class named CLASSNAME
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

使用语法：

yarn [--config confdir] COMMAND [--loglevel loglevel] [GENERIC_OPTIONS] [COMMAND_OPTIONS]

```
--config confdir        #覆盖默认的配置目录，默认为${HADOOP_PREFIX}/conf.
--loglevel loglevel     #覆盖日志级别。有效的日志级别为FATAL，ERROR，WARN，INFO，DEBUG和TRACE。默认值为INFO。
GENERIC_OPTIONS         #多个命令支持的一组通用选项
COMMAND COMMAND_OPTIONS #以下各节介绍了各种命令及其选项
```

[回到顶部](https://www.cnblogs.com/zsql/p/11636348.html#_labelTop)

## 二、命令详解



### 2.1、application

使用语法：yarn application [options] #打印报告，申请和杀死任务

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
-appStates <States>         #与-list一起使用，可根据输入的逗号分隔的应用程序状态列表来过滤应用程序。有效的应用程序状态可以是以下之一：ALL，NEW，NEW_SAVING，SUBMITTED，ACCEPTED，RUNNING，FINISHED，FAILED，KILLED
-appTypes <Types>           #与-list一起使用，可以根据输入的逗号分隔的应用程序类型列表来过滤应用程序。
-list                       #列出RM中的应用程序。支持使用-appTypes来根据应用程序类型过滤应用程序，并支持使用-appStates来根据应用程序状态过滤应用程序。
-kill <ApplicationId>       #终止应用程序。
-status <ApplicationId>     #打印应用程序的状态。
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)



### 2.2、applicationattempt

使用语法：yarn applicationattempt [options] #打印应用程序尝试的报告

```
-help                    #帮助
-list <ApplicationId>    #获取到应用程序尝试的列表，其返回值ApplicationAttempt-Id 等于 <Application Attempt Id>
-status <Application Attempt Id>    #打印应用程序尝试的状态。
```



### 2.3、classpath

使用语法：yarn classpath #打印需要得到Hadoop的jar和所需要的lib包路径



### 2.4、container

使用语法：yarn container [options] #打印container(s)的报告

```
-help                            #帮助
-list <Application Attempt Id>   #应用程序尝试的Containers列表
-status <ContainerId>            #打印Container的状态
```



### 2.5、jar

使用语法：yarn jar <jar> [mainClass] args... #运行jar文件，用户可以将写好的YARN代码打包成jar文件，用这个命令去运行它。



### 2.6、logs

使用语法：yarn logs -applicationId <application ID> [options] #转存container的日志。

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
-applicationId <application ID>    #指定应用程序ID，应用程序的ID可以在yarn.resourcemanager.webapp.address配置的路径查看（即：ID）
-appOwner <AppOwner>               #应用的所有者（如果没有指定就是当前用户）应用程序的ID可以在yarn.resourcemanager.webapp.address配置的路径查看（即：User）
-containerId <ContainerId>         #Container Id
-help                              #帮助
-nodeAddress <NodeAddress>         #节点地址的格式：nodename:port （端口是配置文件中:yarn.nodemanager.webapp.address参数指定）
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)



### 2.7、node

使用语法：yarn node [options] #打印节点报告

```
-all             #所有的节点，不管是什么状态的。
-list             #列出所有RUNNING状态的节点。支持-states选项过滤指定的状态，节点的状态包含：NEW，RUNNING，UNHEALTHY，DECOMMISSIONED，LOST，REBOOTED。支持--all显示所有的节点。
-states <States> #和-list配合使用，用逗号分隔节点状态，只显示这些状态的节点信息。
-status <NodeId> #打印指定节点的状态。
```



### 2.8、queue

使用语法：yarn queue [options] #打印队列信息

```
-help     #帮助
-status  #<QueueName>    打印队列的状态
```



### 2.9、daemonlog

使用语法：

yarn daemonlog -getlevel <host:httpport> <classname>
yarn daemonlog -setlevel <host:httpport> <classname> <level>

```
-getlevel <host:httpport> <classname>            #打印运行在<host:port>的守护进程的日志级别。这个命令内部会连接http://<host:port>/logLevel?log=<name>
-setlevel <host:httpport> <classname> <level>    #设置运行在<host:port>的守护进程的日志级别。这个命令内部会连接http://<host:port>/logLevel?log=<name>
```



### 2.10、nodemanager

使用语法：yarn nodemanager #启动nodemanager



### 2.11、proxyserver

使用语法：yarn proxyserver #启动web proxy server



### 2.12、resourcemanager

使用语法：yarn resourcemanager [-format-state-store] #启动ResourceManager

```
-format-state-store     # RMStateStore的格式. 如果过去的应用程序不再需要，则清理RMStateStore， RMStateStore仅仅在ResourceManager没有运行的时候，才运行RMStateStore
```



### 2.13、rmadmin

使用语法： #运行Resourcemanager管理客户端

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
yarn rmadmin [-refreshQueues]
              [-refreshNodes]
              [-refreshUserToGroupsMapping] 
              [-refreshSuperUserGroupsConfiguration]
              [-refreshAdminAcls] 
              [-refreshServiceAcl]
              [-getGroups [username]]
              [-transitionToActive [--forceactive] [--forcemanual] <serviceId>]
              [-transitionToStandby [--forcemanual] <serviceId>]
              [-failover [--forcefence] [--forceactive] <serviceId1> <serviceId2>]
              [-getServiceState <serviceId>]
              [-checkHealth <serviceId>]
              [-help [cmd]]
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
-refreshQueues    #重载队列的ACL，状态和调度器特定的属性，ResourceManager将重载mapred-queues配置文件
-refreshNodes     #动态刷新dfs.hosts和dfs.hosts.exclude配置，无需重启NameNode。
                  #dfs.hosts：列出了允许连入NameNode的datanode清单（IP或者机器名）
                  #dfs.hosts.exclude：列出了禁止连入NameNode的datanode清单（IP或者机器名）
                  #重新读取hosts和exclude文件，更新允许连到Namenode的或那些需要退出或入编的Datanode的集合。
-refreshUserToGroupsMappings            #刷新用户到组的映射。
-refreshSuperUserGroupsConfiguration    #刷新用户组的配置
-refreshAdminAcls                       #刷新ResourceManager的ACL管理
-refreshServiceAcl                      #ResourceManager重载服务级别的授权文件。
-getGroups [username]                   #获取指定用户所属的组。
-transitionToActive [–forceactive] [–forcemanual] <serviceId>    #尝试将目标服务转为 Active 状态。如果使用了–forceactive选项，不需要核对非Active节点。如果采用了自动故障转移，这个命令不能使用。虽然你可以重写–forcemanual选项，你需要谨慎。
-transitionToStandby [–forcemanual] <serviceId>                  #将服务转为 Standby 状态. 如果采用了自动故障转移，这个命令不能使用。虽然你可以重写–forcemanual选项，你需要谨慎。
-failover [–forceactive] <serviceId1> <serviceId2>  #启动从serviceId1 到 serviceId2的故障转移。如果使用了-forceactive选项，即使服务没有准备，也会尝试故障转移到目标服务。如果采用了自动故障转移，这个命令不能使用。
-getServiceState <serviceId>                        #返回服务的状态。（注：ResourceManager不是HA的时候，时不能运行该命令的）
-checkHealth <serviceId>                            #请求服务器执行健康检查，如果检查失败，RMAdmin将用一个非零标示退出。（注：ResourceManager不是HA的时候，时不能运行该命令的）
-help [cmd]                                         #显示指定命令的帮助，如果没有指定，则显示命令的帮助。
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)



### 2.14、scmadmin

使用语法：yarn scmadmin [options] #运行共享缓存管理客户端

```
-help            #查看帮助
-runCleanerTask    #运行清理任务
```



### 2.15、 sharedcachemanager

使用语法：yarn sharedcachemanager #启动共享缓存管理器



### 2.16、timelineserver

使用语法：yarn timelineserver  #启动timelineserver

 

**更多hadoop生态文章见：[ hadoop生态系列](https://www.cnblogs.com/zsql/p/11560374.html)**

 

**参考：
https://hadoop.apache.org/docs/r2.7.7/hadoop-yarn/hadoop-yarn-site/YarnCommands.html
**

 

每个阶段有每个阶段的使命，愿自己的努力，能够更好的完成自己的使命，谨记：别忘了自己是谁！！！

## 三、参考资料

https://www.cnblogs.com/zsql/p/11636348.html