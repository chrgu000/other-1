springmvc 第一天 springmvc的基础知识

 

课程安排：

 

第一天：springmvc的基础知识

什么是springmvc？

springmvc框架原理（掌握）

​         前端控制器、处理器映射器、处理器适配器、视图解析器

springmvc入门程序

​         目的：对前端控制器、**处理器映射器、处理器适配器**、视图解析器学习

​         非注解的处理器映射器、处理器适配器

​         注解的处理器映射器、处理器适配器（掌握）

springmvc和mybatis整合（掌握）

 

springmvc注解开发：（掌握）

​         常用的注解学习

​         参数绑定（简单类型、pojo、集合类型（明天讲））

​         自定义参数绑定（掌握）

springmvc和struts2区别

 

第二天：springmvc的高级应用

​         参数绑定（集合类型）

​         数据回显

​         上传图片

​         json数据交互

​         RESTful支持

​         拦截器

 

# 1      springmvc框架

## 1.1     什么是springmvc

springmvc是spring框架的一个模块，springmvc和spring无需通过中间整合层进行整合。

springmvc是一个基于mvc的web框架。

 

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image002.jpg)

 

 

## 1.2     mvc在b/s系统 下的应用

 

mvc是一个设计模式，mvc在b/s系统 下的应用：

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image003.gif)

 

## 1.3     springmvc框架

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image004.gif)

 

 

第一步：发起请求到前端控制器(DispatcherServlet)

第二步：前端控制器请求HandlerMapping查找 Handler

​         可以根据xml配置、注解进行查找

第三步：处理器映射器HandlerMapping向前端控制器返回Handler

第四步：前端控制器调用处理器适配器去执行Handler

第五步：处理器适配器去执行Handler

第六步：Handler执行完成给适配器返回ModelAndView

第七步：处理器适配器向前端控制器返回ModelAndView

​         ModelAndView是springmvc框架的一个底层对象，包括Model和view

第八步：前端控制器请求视图解析器去进行视图解析

​         根据逻辑视图名解析成真正的视图(jsp)

第九步：视图解析器向前端控制器返回View

第十步：前端控制器进行视图渲染

​         视图渲染将模型数据(在ModelAndView对象中)填充到request域

第十一步：前端控制器向用户响应结果

 

 

组件：

1、前端控制器DispatcherServlet（不需要程序员开发）

作用接收请求，响应结果，相当于转发器，中央处理器。

有了DispatcherServlet减少了其它组件之间的耦合度。

 

2、处理器映射器HandlerMapping(不需要程序员开发)

作用：根据请求的url查找Handler

 

 

3、处理器适配器HandlerAdapter

作用：按照特定规则（HandlerAdapter要求的规则）去执行Handler

 

4、处理器Handler(需要程序员开发)

注意：编写Handler时按照HandlerAdapter的要求去做，这样适配器才可以去正确执行Handler

 

5、视图解析器View resolver(不需要程序员开发)

作用：进行视图解析，根据逻辑视图名解析成真正的视图（view）

 

6、视图View(需要程序员开发jsp)

View是一个接口，实现类支持不同的View类型（jsp、freemarker、pdf...）

 

 

# 2      入门程序

## 2.1     需求

以案例作为驱动。

springmvc和mybaits使用一个案例（商品订单管理）。

 

功能需求：商品列表查询

 

## 2.2     环境准备

数据库环境：mysql5.1

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image006.jpg)

java环境：

jdk1.7.0_72

eclipse indigo

 

springmvc版本：spring3.2

 

需要spring3.2所有jar（一定包括spring-webmvc-3.2.0.RELEASE.jar）

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image008.jpg)

 

## 2.3     配置前端控制器

在web.xml中配置前端控制器。

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image010.jpg)

 

## 2.4     配置处理器适配器

在classpath下的springmvc.xml中配置处理器适配器

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image012.jpg)

 

通过查看原代码：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image014.jpg)

 

此适配器能执行实现Controller接口的Handler。

 

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image016.jpg)

 

## 2.5     开发Handler

需要实现 controller接口，才能由org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter适配器执行。

```
public class ItemsController1 implements Controller {
   @Override
   public ModelAndView handleRequest(HttpServletRequest request,
         HttpServletResponse response) throws Exception {
      //调用service查找 数据库，查询商品列表，这里使用静态数据模拟
      List<Items> itemsList = new ArrayList<Items>();
      //向list中填充静态数据   
      Items items_1 = new Items();
      items_1.setName("联想笔记本");
      items_1.setPrice(6000f);
      items_1.setDetail("ThinkPad T430 联想笔记本电脑！");
      Items items_2 = new Items();
      items_2.setName("苹果手机");
      items_2.setPrice(5000f);
      items_2.setDetail("iphone6苹果手机！"); 
      itemsList.add(items_1);
      itemsList.add(items_2);
      //返回ModelAndView
      ModelAndView modelAndView =  new ModelAndView();
      //相当 于request的setAttribut，在jsp页面中通过itemsList取数据
      modelAndView.addObject("itemsList", itemsList); 
      //指定视图
      modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");
      return modelAndView;
   }
}
```

## 2.6     视图编写

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image018.jpg)

 

 

## 2.7     配置Handler

将编写Handler在spring容器加载。

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image020.jpg)

 

## 2.8     配置处理器映射器

在classpath下的springmvc.xml中配置处理器映射器

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image022.jpg)

 

 

## 2.9     配置视图解析器

 

需要配置解析jsp的视图解析器。

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image024.jpg)

 

## 2.10部署调试

 

访问地址：http://localhost:8080/springmvcfirst1208/queryItems.action

 

处理器映射器根据url找不到Handler，报下边的错误。说明url错误。

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image026.jpg)

 

处理器映射器根据url找到了Handler，转发的jsp页面找到，报下边的错误，说明jsp页面地址错误了。

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image028.jpg)

 

# 3      非注解的处理器映射器和适配器

## 3.1     非注解的处理器映射器

 

处理器映射器：

org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping

 

 

另一个映射器：

org.springframework.web.servlet.handler.SimpleUrlHandlerMapping

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image030.jpg)

 

多个映射器可以并存，前端控制器判断url能让哪些映射器映射，就让正确的映射器处理。

 

 

## 3.2     非注解的处理器适配器

 

org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter

要求编写的Handler实现 Controller接口。

 

org.springframework.web.servlet.mvc.[HttpRequestHandlerAdapter]()

要求编写的Handler实现 HttpRequestHandler接口。

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image032.jpg)

 

​      //使用此方法可以通过修改response，设置响应的数据格式，比如响应json数据

 

/*

​      response.setCharacterEncoding("utf-8");

​      response.setContentType("application/json;charset=utf-8");

​      response.getWriter().write("json串");*/

 

# 4      DispatcherSerlvet.properties

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image034.jpg)

 

前端控制器从上边的文件中加载处理映射器、适配器、视图解析器等组件，如果不在springmvc.xml中配置，使用默认加载的。

 

 

 

 

# 5      注解的处理器映射器和适配器

 

在spring3.1之前使用org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping注解映射器。

 

在spring3.1之后使用org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping注解映射器。

 

在spring3.1之前使用org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter注解适配器。

 

在spring3.1之后使用org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter注解适配器。

 

## 5.1     配置注解映射器和适配器。

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image036.jpg)

 

<!--使用 mvc:annotation-driven代替上边注解映射器和注解适配器配置

   mvc:annotation-driven默认加载很多的参数绑定方法，

   比如json转换解析器就默认加载了，如果使用mvc:annotation-driven不用配置上边的RequestMappingHandlerMapping和RequestMappingHandlerAdapter

   实际开发时使用mvc:annotation-driven

​    -->

   <!--<mvc:annotation-driven></mvc:annotation-driven> -->

 

 

 

## 5.2     开发注解Handler

 

使用注解的映射器和注解的适配器。（注解的映射器和注解的适配器必须配对使用）

 

//使用Controller标识 它是一个控制器

@Controller

**public** **class** ItemsController3 {

   

   //商品查询列表

   //@RequestMapping实现 对queryItems方法和url进行映射，一个方法对应一个url

   //一般建议将url和方法写成一样

   @RequestMapping("/queryItems")

   **public** ModelAndView queryItems()**throws** Exception{

​      

​      //调用service查找 数据库，查询商品列表，这里使用静态数据模拟

​      List<Items> itemsList = **new** ArrayList<Items>();

​      //向list中填充静态数据

​      

​      Items items_1 = **new** Items();

​      items_1.setName("联想笔记本");

​      items_1.setPrice(6000f);

​      items_1.setDetail("ThinkPad T430 联想笔记本电脑！");

​      

​      Items items_2 = **new** Items();

​      items_2.setName("苹果手机");

​      items_2.setPrice(5000f);

​      items_2.setDetail("iphone6苹果手机！");

​      

​      itemsList.add(items_1);

​      itemsList.add(items_2);

​      

​      //返回ModelAndView

​      ModelAndView modelAndView =  **new** ModelAndView();

​      //相当 于request的setAttribut，在jsp页面中通过itemsList取数据

​      modelAndView.addObject("itemsList", itemsList);

​      

​      //指定视图

​      modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");

​      

​      **return** modelAndView;

​      

   }

 

 

## 5.3     在spring容器中加载Handler

 

   <!-- 对于注解的Handler可以单个配置

   实际开发中建议使用组件扫描

​    -->

   <!-- <beanclass="cn.itcast.ssm.controller.ItemsController3" /> -->

   <!-- 可以扫描controller、service、...

   这里让扫描controller，指定controller的包

​    -->

   <context:component-scan base-package=*"cn.itcast.ssm.controller"*></context:component-scan>

 

 

## 5.4     部署调试

访问：http://localhost:8080/springmvcfirst1208/queryItems.action

 

# 6      源码分析（了解）

 

通过前端控制器源码分析springmvc的执行过程。

 

第一步：前端控制器接收请求

 

调用doDiapatch

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image038.jpg)

 

第二步：前端控制器调用处理器映射器查找 Handler

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image040.jpg)

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image042.jpg)

 

第三步：调用处理器适配器执行Handler，得到执行结果ModelAndView

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image044.jpg)

 

第四步：视图渲染，将model数据填充到request域。

 

视图解析，得到view:

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image046.jpg)

 

调用view的渲染方法，将model数据填充到request域

 

渲染方法：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image048.jpg)

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image050.jpg)

 

 

# 7      入门程序小结

 

通过入门程序理解springmvc前端控制器、**处理器映射器、处理器适配器**、视图解析器用法。

 

前端控制器配置：

第一种：*.action，访问以.action结尾 由DispatcherServlet进行解析

 

第二种：/，所以访问的地址都由DispatcherServlet进行解析，对于静态文件的解析需要配置不让DispatcherServlet进行解析

   使用此种方式可以实现 RESTful风格的url

 

**处理器映射器：**

**非注解处理器映射器（了解）**

**注解的处理器映射器（掌握）**

**         ****对标记@Controller****类中标识有@RequestMapping****的方法进行映射。在@RequestMapping****里边定义映射的url****。使用注解的映射器不用在xml****中配置url****和Handler****的映射关系。**

** **

**处理器适配器：**

**非注解处理器适配器（了解）**

**注解的处理器适配器（掌握）**

**         ****注解处理器适配器和注解的处理器映射器是配对使用。理解为不能使用非注解映射器进行映射。**

 

<mvc:annotation-driven></mvc:annotation-driven>可以代替下边的配置：

 

   <!--注解映射器 -->

   <bean class=*"org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"*/>

   <!--注解适配器 -->

   <bean class=*"org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"*/>

 

实际开发使用：mvc:annotation-driven

 

 

视图解析器配置前缀和后缀：

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image052.jpg)

 

程序中不用指定前缀和后缀：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image054.jpg)

 

 

# 8      springmvc和mybatis整合

 

## 8.1     需求

使用springmvc和mybatis完成商品列表查询。

 

 

## 8.2     整合思路

 

 

springmvc+mybaits的系统架构：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image055.gif)

 

 

 

 

第一步：整合dao层

​         mybatis和spring整合，通过spring管理mapper接口。

​         使用mapper的扫描器自动扫描mapper接口在spring中进行注册。

 

第二步：整合service层

​         通过spring管理 service接口。

​         使用配置方式将service接口配置在spring配置文件中。

​         实现事务控制。

 

第三步：整合springmvc

​         由于springmvc是spring的模块，不需要整合。

 

 

## 8.3     准备环境

 

数据库环境：mysql5.1

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image056.jpg)

java环境：

jdk1.7.0_72

eclipse indigo

 

springmvc版本：spring3.2

 

所需要的jar包：

数据库驱动包：mysql5.1

mybatis的jar包

mybatis和spring整合包

log4j包

dbcp数据库连接池包

spring3.2所有jar包

jstl包

 

参考：![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image058.jpg)

 

工程结构：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image060.jpg)

 

## 8.4     整合dao

 

mybatis和spring进行整合。

 

### 8.4.1    sqlMapConfig.xml

 

mybatis自己的配置文件。

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image062.jpg)

 

### 8.4.2    applicationContext-dao.xml

 

配置：

数据源

SqlSessionFactory

mapper扫描器

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image064.jpg)

 

 

### 8.4.3    逆向工程生成po类及mapper(单表增删改查)

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image066.jpg)

 

将生成的文件拷贝至工程中。

 

### 8.4.4    手动定义商品查询mapper

 

针对综合查询mapper，一般情况会有关联查询，建议自定义mapper

 

 

#### 8.4.4.1             ItemsMapperCustom.xml

 

sql语句：

​         SELECT* FROM items  WHERE items.name LIKE '%笔记本%'

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image068.jpg)

 

 

#### 8.4.4.2             ItemsMapperCustom.java

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image070.jpg)

 

## 8.5     整合service

 

让spring管理service接口。

 

### 8.5.1    定义service接口

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image072.jpg)

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image074.jpg)

 

 

### 8.5.2    在spring容器配置service(applicationContext-service.xml)

 

创建applicationContext-service.xml，文件中配置service。

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image076.jpg)

 

 

### 8.5.3    事务控制(applicationContext-transaction.xml)

在applicationContext-transaction.xml中使用spring声明式事务控制方法。

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image078.jpg)

 

 

## 8.6     整合springmvc

### 8.6.1    springmvc.xml

创建springmvc.xml文件，配置处理器映射器、适配器、视图解析器。

 

<!--可以扫描controller、service、...

   这里让扫描controller，指定controller的包

​    -->

   <context:component-scan base-package=*"cn.itcast.ssm.controller"*></context:component-scan>

   

​      

   <!--注解映射器 -->

   <!-- <beanclass="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>-->

   <!--注解适配器 -->

   <!-- <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>-->

   

   <!-- 使用 mvc:annotation-driven代替上边注解映射器和注解适配器配置

   mvc:annotation-driven默认加载很多的参数绑定方法，

   比如json转换解析器就默认加载了，如果使用mvc:annotation-driven不用配置上边的RequestMappingHandlerMapping和RequestMappingHandlerAdapter

   实际开发时使用mvc:annotation-driven

​    -->

   <mvc:annotation-driven></mvc:annotation-driven>

   

 

   <!-- 视图解析器

   解析jsp解析，默认使用jstl标签，classpath下的得有jstl的包

​    -->

   <bean

​      class=*"org.springframework.web.servlet.view.InternalResourceViewResolver"*>

​      <!-- 配置jsp路径的前缀 -->

​      <property name=*"prefix"*value=*"/WEB-INF/jsp/"*/>

​      <!-- 配置jsp路径的后缀 -->

​      <property name=*"suffix"*value=*".jsp"*/>

   </bean>

 

### 8.6.2    配置前端控制器

 

参考入门程序。

 

### 8.6.3    编写Controller(就是Handler)

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image080.jpg)

 

### 8.6.4    编写jsp

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image082.jpg)

 

 

## 8.7     加载spring容器

 

将mapper、service、controller加载到spring容器中。

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image084.jpg)

 

建议使用通配符加载上边的配置文件。

 

在web.xml中，添加spring容器监听器，加载spring容器。

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image086.jpg)

 

 

# 9      商品修改功能开发

## 9.1     需求

操作流程：

1、进入商品查询列表页面

2、点击修改，进入商品修改页面，页面中显示了要修改的商品（从数据库查询）

​         要修改的商品从数据库查询，根据商品id(主键)查询商品信息

 

3、在商品修改页面，修改商品信息，修改后，点击提交

 

## 9.2     开发mapper

mapper：

​         根据id查询商品信息

​         根据id更新Items表的数据

不用开发了，使用逆向工程生成的代码。

 

## 9.3     开发service

接口功能：

​         根据id查询商品信息

​         修改商品信息

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image088.jpg)

 

 

 

## 9.4     开发controller

方法：

​         商品信息修改页面显示

​         商品信息修改提交

 

 

# 10            @RequestMapping

n  url映射

定义controller方法对应的url，进行处理器映射使用。

 

 

n  窄化请求映射

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image090.jpg)

n  限制http请求方法

出于安全性考虑，对http的链接进行方法限制。

如果限制请求为post方法，进行get请求，报错：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image092.jpg)

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image094.jpg)

 

 

# 11            controller方法的返回值

 

n  返回ModelAndView

需要方法结束时，定义ModelAndView，将model和view分别进行设置。

 

n  返回string

如果controller方法返回string，

 

1、表示返回逻辑视图名。

真正视图(jsp路径)=前缀+逻辑视图名+后缀

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image096.jpg)

2、redirect重定向

商品修改提交后，重定向到商品查询列表。

redirect重定向特点：浏览器地址栏中的url会变化。修改提交的request数据无法传到重定向的地址。因为重定向后重新进行request（request无法共享）

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image098.jpg)

 

3、forward页面转发

通过forward进行页面转发，浏览器地址栏url不变，request可以共享。

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image100.jpg)

 

 

n  返回void

 

在controller方法形参上可以定义request和response，使用request或response指定响应结果：

1、使用request转向页面，如下：

request.getRequestDispatcher("页面路径").forward(request,response);

 

2、也可以通过response页面重定向：

response.sendRedirect("url")

 

3、也可以通过response指定响应结果，例如响应json数据如下：

response.setCharacterEncoding("utf-8");

response.setContentType("application/json;charset=utf-8");

response.getWriter().write("json串");

 

 

# 12            参数绑定

 

## 12.1spring参数绑定过程

 

从客户端请求key/value数据，经过参数绑定，将key/value数据绑定到controller方法的形参上。

 

springmvc中，接收页面提交的数据是通过方法形参来接收。而不是在controller类定义成员变更接收！！！！

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image101.gif)

 

 

 

## 12.2默认支持的类型

直接在controller方法形参上定义下边类型的对象，就可以使用这些对象。在参数绑定过程中，如果遇到下边类型直接进行绑定。

#### 1.2.1.1 HttpServletRequest

通过request对象获取请求信息

#### 1.2.1.2 HttpServletResponse

通过response处理响应信息

#### 1.2.1.3 HttpSession

通过session对象得到session中存放的对象

#### 1.2.1.4 Model/ModelMap

model是一个接口，modelMap是一个接口实现 。

作用：将model数据填充到request域。

 

## 12.3简单类型

 

 

通过@RequestParam对简单类型的参数进行绑定。

如果不使用@RequestParam，要求request传入参数名称和controller方法的形参名称一致，方可绑定成功。

 

如果使用@RequestParam，不用限制request传入参数名称和controller方法的形参名称一致。

 

通过required属性指定参数是否必须要传入，如果设置为true，没有传入参数，报下边错误：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image103.jpg)

 

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image105.jpg)

 

 

参考教案对其它简单类型绑定进行测试。

 

## 12.4pojo绑定

页面中input的name和controller的pojo形参中的属性名称一致，将页面中数据绑定到pojo。

 

页面定义：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image107.jpg)

 

controller的pojo形参的定义：

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image109.jpg)

 

## 12.5自定义参数绑定实现日期类型绑定

 

对于controller形参中pojo对象，如果属性中有日期类型，需要自定义参数绑定。

将请求日期数据串传成日期类型，要转换的日期类型和pojo中日期属性的类型保持一致。

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image111.jpg)

 

所以自定义参数绑定将日期串转成java.util.Date类型。

 

需要向处理器适配器中注入自定义的参数绑定组件。

 

### 12.5.1             自定义日期类型绑定

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image113.jpg)

 

### 12.5.2             配置方式

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image115.jpg)

 

![img](https://github.com/jjj2010/other/blob/master/springmvc01/pic/clip_image117.jpg)

 

# 13            springmvc和struts2的区别 

 

1、springmvc基于方法开发的，struts2基于类开发的。

 

springmvc将url和controller方法映射。映射成功后springmvc生成一个Handler对象，对象中只包括了一个method。

方法执行结束，形参数据销毁。

springmvc的controller开发类似service开发。

 

2、springmvc可以进行单例开发，并且建议使用单例开发，struts2通过类的成员变量接收参数，无法使用单例，只能使用多例。

 

3、经过实际测试，struts2速度慢，在于使用struts标签，如果使用struts建议使用jstl。

 

 

 

# 14      问题

 

## 14.1post乱码

 

在web.xml添加post乱码filter

 

在web.xml中加入：

<filter>

<filter-name>CharacterEncodingFilter</filter-name>

<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>

<init-param>

<param-name>encoding</param-name>

<param-value>utf-8</param-value>

</init-param>

</filter>

<filter-mapping>

<filter-name>CharacterEncodingFilter</filter-name>

<url-pattern>/*</url-pattern>

</filter-mapping>

 

以上可以解决post请求乱码问题。

对于get请求中文参数出现乱码解决方法有两个：

 

修改tomcat配置文件添加编码与工程编码一致，如下：

 

<Connector URIEncoding="utf-8"connectionTimeout="20000" port="8080"protocol="HTTP/1.1" redirectPort="8443"/>

 

另外一种方法对参数进行重新编码：

String userName new 

String(request.getParamter("userName").getBytes("ISO8859-1"),"utf-8")

 

ISO8859-1是tomcat默认编码，需要将tomcat编码后的内容按utf-8编码

 

 

 