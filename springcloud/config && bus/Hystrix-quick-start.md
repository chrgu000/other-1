## 快速体验

Feign 本身支持`Hystrix`，不需要额外引入依赖。



（1）修改`tensquare`
`_qa`模块的`application.yml` ，开启`hystrix`

```
feign:
  hystrix:
	enabled: true

```



（2）在`com.tensquare.qa.client`包下创建`impl`包，包下创建熔断实现类，实现自接口`LabelClient`

```
@Component
public class LabelClientImpl implements LabelClient {
@Override
public Result findById(String id) {
return new Result(false, StatusCode.ERROR,"熔断器启动了");
  }
}
```



（3）修改LabelClient的注解

```
@FeignClient(value = "ts-base",fallback = LabelClientImpl.class)
public interface LabelClient {
     @RequestMapping(value = "/label/{labelId}",method = RequestMethod.GET)
     Result findById(@PathVariable("labelId") String labelId);
}
```



（4）测试运行
重新启动问答微服务，关闭基础微服务，测试看熔断器是否运行。