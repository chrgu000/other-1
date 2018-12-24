##SpringCloud Config Bus2.0改动,没有自动更新配置

这段时间一直在学习SpringCloud的知识，按照网上的教程一直从Eureka Server服务注册中心应用的搭建到SpringCloud Config 配置中心服务的搭建，就再这个服务搭建的过程中遇到了一个很棘手的问题：SpringCloud Config Bus 集成Rabbitmq刷新配置信息的功能

第一个问题是Rabbitmq客户端的安装，我电脑是Win10 64位系统，于是上网搜了一些Windows版本的Rabbitmq安装教程，结果很郁闷，一直都没有安装成功，Erlang 的环境也搭建好了，Rabbitmq也安装好了，结果安装Rabbitmq的可视化插件时总是不成功，报的错误我上网也没有搜到一例，各种修改都没有成功，具体的错误信息我没有保存，下次再在我电脑上安装Rabbitmq的时候报错信息我再贴上来，于是我就在我的虚拟机Linux环境中安装了Rabbitmq，不得不说，Linux环境中安装这些软件还是比Windows环境下要方便很多，我的虚拟机是Ubuntu18.x的，安装过程参考网上教程，我直接参考[官网安装教程](http://www.rabbitmq.com/install-debian.html)，安装过程没有什么问题，主要是虚拟机要联网。

第二个问题是Rabbitmq安装后使用Config Bus 去刷新config Client读取的Git上的配置文件信息，按照网上的教程，添加了

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

maven配置信息，添加了Rabbitmq的连接信息

```
spring.rabbitmq.host=192.168.189.138
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin
```

在config Client的读取配置文件中添加@RefreshScope注解信息

按照教程上的步骤启动相关联的服务，再以POST方式调用ConfigServer应用的 bus/refresh方法就应该可以了，结果没有那么顺利，修改Git上的配置信息，然后在浏览器上访问 http://localhost:9090/bus/refresh是get方式的，没有报错，返回如下的Json的信息

```
{
"name": "bus",
profiles:["refresh"],
"label": null,
"version": "28628f30ad291b9b1cc55fcba930909a483b2793",
"state": null,
"propertySources": []
}

```

再请求config Client的读取配置信息的请求，发现没有变化，后台没有报错。

再用PostMan以POST方式请求http://localhost:9090/bus/refresh，结果返回的是 405，后台提示

Request method 'POST' not supported

这个问题一直困扰我两天，终于有一天我上网找到了问题的原因，有位同仁和我的问题很类似，原来是SpringCloud2.0做了重大的改动， /bus/refresh全部整合到actuador里面了，具体的问题参考[这里](https://ask.csdn.net/questions/684123)，于是我就再pom.xml 里添加了

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

在application.properties文件中添加了

```
management.endpoint.bus-refresh.enabled=true
management.endpoints.web.exposure.include=bus-refresh
```

在config Client服务中也添加了上述的内容，重启服务，再请求，一开始请求会报500错误，再多请求几次就发现没有问题了，在检验一下config Client 的读取配置文件方法，果然能实现动态刷新配置信息了。

在这个过程中还有点小问题，就是Config Server 启动后，连接Rabbitmq服务会有Socket Closed 错误，但是没有影响bus的消息总线的刷新。