```
# zuul在转发请求时  会转发两次  第一次中带有options()方法寻找转发的路径  第二次才是真实转发 并且不会带上header中的参数  所以需要配置过滤器添加上header中的参数

# 在包路径下添加过滤器类即可 并添加到容器
@Component
public class WebFilter extends ZuulFilter {
    // 什么时候拦截
    @Override
    public String filterType() {
    // 前置过滤器 可以在请求被路由之前调用
        return "pre";
    }

    @Override
    public int filterOrder() {
    // 多个过滤器之间的优先级，数字越大，优先级越低
        return 0;
    }

    @Override
    public boolean shouldFilter() {
    // 过滤器是否生效

        return true;
    }
    // 过滤器的方法
    @Override
    public Object run() throws ZuulException {
        System.out.println("过滤器生效了");
        return null;
    }
}

```

```
@Component
public class WebFilter extends ZuulFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String token = request.getHeader("token");
        if(request.getMethod().equals("OPTIONS")){
            return null;
        }
        if ( request.getRequestURI().indexOf("/admin/login")>0){
            System.out.println("登陆页面");
            return null;
        }
        if (StringUtils.isNotBlank(token) && token.startsWith("JIao ")){
            String auth = token.substring(5);
            Claims claims ;
            try {
                claims = jwtUtil.parseJWT(auth);
                if (claims != null){
                    String roles = (String) claims.get("roles");
                    if (roles.equals("admin")){
                        currentContext.addZuulRequestHeader("token",token);
                        System.out.println("token验证通过，添加了头信息：" + token);
                        return null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 终止运行
                currentContext.setSendZuulResponse(false); # 拦截不放行
                return null;
            }

        }
        currentContext.setSendZuulResponse(false);
        currentContext.setResponseBody("无权访问");
        currentContext.setResponseStatusCode(401);
        currentContext.getResponse().setContentType("text/html;charset=UTF-8");
        return null;
    }
}

```

