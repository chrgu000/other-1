# SpringMVC 防止表单提交的解决方案 顶 原

【转载自:(https://my.oschina.net/chenxiaobian/blog/664579)



# 1.表单重复提交的场景:

- 在网络延迟的情况下让用户有时间点击多次submit导致表单重复提交
- 表单提交后用户点击 刷新按钮导致表单重复提交
- 用户提交表单后，点击后退按钮回退到表单页面后进行再次提交

# 2.防止表单重复提交的方法

### 利用javascript防止表单重复提交

```
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
  <head>
    <title>Form表单</title>
        <script type="text/javascript">
        var isCommitted = false;//表单是否已经提交标识，默认为false
        function dosubmit(){
            if(isCommitted==false){
                isCommitted = true;//提交表单后，将表单是否已经提交标识设置为true
                return true;//返回true让表单正常提交
            }else{
                return false;//返回false那么表单将不提交
            }
        }
    </script>
  </head>
  
  <body>
      <form action="${pageContext.request.contextPath}/servlet/DoFormServlet" onsubmit="return dosubmit()" method="post">
        用户名：<input type="text" name="username">
        <input type="submit" value="提交" id="submit">
    </form>
  </body>
</html>
```

### 表单提交后, 将提交按钮设置为disable 

代码如下:

```
function dosubmit(){
    //获取表单提交按钮
    var btnSubmit = document.getElementById("submit");
    //将表单提交按钮设置为不可用，这样就可以避免用户再次点击提交按钮
    btnSubmit.disabled= "disabled";
    //返回true让表单可以正常提交
    return true;
}
```

### 利用Token的方式防止表单重复提交

在服务器端生成一个唯一的随机标识号，称为Token(令牌)，同时在当前用户的Session域中保存这个Token。然后将Token发送到客户端的Form表单中，在Form表单中使用隐藏域来存储这个Token，表单提交的时候连同这个Token一起提交到服务器端，然后在服务器端判断客户端提交上来的Token与服务器端生成的Token是否一致，如果不一致，那就是重复提交了，此时服务器端就可以不处理重复提交的表单。如果相同则处理表单提交，处理完后清除当前用户的Session域中存储的标识号。

# 3.SpringMVC防止表单重复提交

定义Token代码:

```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    boolean save() default false;
    boolean remove() default false;
}
```

定义拦截器TokenInterceptor：

```
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Token annotation = method.getAnnotation(Token.class);
            if (annotation != null) {
                boolean needSaveSession = annotation.save();
                if (needSaveSession) {
                    request.getSession(false).setAttribute("token", UUID.randomUUID().toString());
                }
                boolean needRemoveSession = annotation.remove();
                if (needRemoveSession) {
                    if (isRepeatSubmit(request)) {
                        return false;
                    }
                    request.getSession(false).removeAttribute("token");
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    private boolean isRepeatSubmit(HttpServletRequest request) {
        String serverToken = (String)request.getSession(false).getAttribute("token");
        if (serverToken == null) {
            return true;
        }

        String clientToken = request.getParameter("token");
        if (clientToken == null) {
            return true;
        }

        if(!serverToken.equals(clientToken)){
            return true;
        }

        return false;
    }
}
```

然后在springmvc的配置文件中加入:

```
<!--配置拦截器-->
<mvc:interceptors>
       <!--配置token拦截器,防止用户重复提交数据-->
       <mvc:interceptor>
              <mvc:mapping path="/**"/>
              <bean class="com.simple.interceptor.TokenInterceptor"/>
       </mvc:interceptor>
</mvc:interceptors>
```

在jsp页面form里面添加下面的代码:

```
<input type="hidden" name="token" value="${token}">
```

使用上面在需要生成token的controller上增加@Token(save=true) ，需要检查重复提交的controller上添加@Token(remove=true)

```
@RequestMapping("/save")
 @AvoidDuplicateSubmission(save= true)
    public synchronized ModelAndView save(ExecutionUnit unit, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
 
@RequestMapping("/edit")
    @AvoidDuplicateSubmission(remove= true)
    public ModelAndView edit(Integer id, HttpServletRequest request) throws Exception {
```