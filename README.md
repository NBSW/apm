# apm
基于jdk agent方式，动态注入字节码至被监控的应用程序
监控方法进入、退出、异常、返回值、参数、JVM基本信息
修改字节码使用ByteBuddy(1.6.1 比较新的版本）其底层是使用asm，因此效率不错
由于bytebuddy的文档和示例太少，参考stagemonitor设计和使用方式

插件提供一下功能
1、被监控的应用程序内部编码方式潜入（调用一会初始化代码即可)
2、独立于被监控的应用程序，跟应用一起启动
3、支持运行时嵌入代码，与应用程序完全独立，应用程序启动后 再启动本监控程序
4、利用配置文件，可以灵活的监控代码，自定义java监控插件开发（基于ServiceLoade方式)
5、为prometheus提供监控数据，自带了http接口（jdk内部提供，无第三方web插件引入)

性能问题
通过压测  注入监控后 执行一个方法1000w次 要额外增加1000多毫秒

下一步新增功能:
调用链跟踪



