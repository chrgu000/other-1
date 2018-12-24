```
# 配置服务端
# 在github上新建一个仓库，将项目的application.yml改名为base-dev.yml上传到仓库
# base即application name  dev即profile
# 上传完成后 项目的地址为https://github.com/jjj2010/tensquare-config.git

# 配置中心微服务
# 依赖
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
    </dependencies>
# 创建启动类ConfigApplication
@EnableConfigServer  # 添加一个注解
@SpringBootApplication
public class ConfigApplication {

    public static void main(String[] args){
        SpringApplication.run(ConfigApplication.class,args);
    }
}
# 编写配置文件application.yml
server:
  port: 8000
spring:
  application:
    name: ts-config
  cloud:
    config:
      server:
        git:
          uri: https://github.com/jjj2010/tensquare-config.git
# 启动项目 浏览器测试：http://localhost:8000/base-dev.yml 可以看到配置内容
```

```
# 配置客户端
# 添加依赖
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
# 添加bootstrap.yml ,删除application.yml
spring:
  cloud:
    config:
      name: base
      profile: dev
      label: master
      uri: http://127.0.0.1:8000
# 测试： 启动工程tensquare-eureka tensquare-config tensquare-base，看是否可以正常运行
```

