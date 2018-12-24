### Zuul路由转发

2.3.1 管理后台微服务网关
（1）创建子模块`tensquare_manager`，`pom.xml`引入`eureka-client` 和`zuul`的依赖

```
<dependencies>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
  </dependency>
</dependencies>
```

（2）创建`application.yml`

```
server:
  port: 12000        # zuul微服务的端口 
spring:
  application:
    name: ts-manager # 指定服务名
eureka:
  client:
    serviceUrl:       # Eureka客户端与Eureka服务端进行交互的地址
      defaultZone: http://127.0.0.1:6868/eureka/   # Eureka服务端地址
  instance:
    prefer‐ip‐address: true
zuul:
  routes:
    ts-base:             		 # 活动
      path: /base/**             # 配置请求URL的请求规则，与微服务的路由规则无关，自己定义
      serviceId: ts-base 		 # 指定Eureka注册中心中的服务id
```

（3）编写启动类

```
@EnableZuulProxy
@SpringBootApplication
public class Application {
public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

