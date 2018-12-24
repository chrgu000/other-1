```
# 配置服务端
（1）修改tensquare-config工程的pom.xml，引用依赖
    <dependencies>
   	    <!-- 下面是集中配置依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <!-- 下面是springcloudbus依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-bus</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
        </dependency>
    </dependencies>
    
 （2）修改application.yml ，添加配置
      rabbitmq:
        host: 35.221.248.16  # 通过rabbitmq触发重新编译github上的配置文件
    management:
      endpoints:
        web:
          exposure:
            include: bus-refresh  # 通过http://127.0.0.1:8000/actuator/bus-refresh来触发重新编译
      endpoint:
        bus-refresh:
          enabled: true	
  
  （3） 配置客户端
  修改tensquare-base，添加依赖
 		 <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-bus</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
   （4）在github上修改tensquare-base的配置文件中配置rabbitMQ的地址：
                   server:
                  port: 9001
                spring:
                  application:
                    name: ts-base
                  datasource:
                    driver-class-name: com.mysql.jdbc.Driver
                    url: jdbc:mysql://35.221.248.16:3306/tensquare_base?characterEncoding=utf-8
                    username: root
                    password: 123456
                  rabbitmq:
                    host: 35.221.248.16
                  jpa:
                    database: mysql
                    show-sql: true
                eureka:
                  client:
                    service-url:
                      defaultZone: http://localhost:6868/eureka
                  instance:
                    prefer-ip-address: true 
                jwt:
                  test: 8888
（5）启动tensquare-eureka 、tensquare-config和tensquare-base 看是否正常运行
     获取${jwt.test}的值
（6）修改github上tensquare-base的配置文件中jwt.test的值
（7）在postman中请求 http://127.0.0.1:8000/actuator/bus-refresh ，method为post
（8）再次获取${jwt.test}的值，看变化如何
（9）测试后观察,发现并没有更新信息。因为缺少一个注解@RefreshScope 此注解用于刷新配置
    @RefreshScope
    @RestController
    public class TestController {
    @Value("${jwt.test}")
    private int test;
  @RequestMapping(value ="/test", method = RequestMethod.GET)
  public String test() {
   System.out.println("数字是：" + test);
   }
}
（10）添加后再次进行测试。
```

