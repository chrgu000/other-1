```java
@Indexed
此注解是为某个字段申请一个索引

@JsonProperty("token_type")
此注解用于属性上，作用是把该属性的名称序列化为另外一个名称，如把trueName属性序列化为name，@JsonProperty(value="name")。

@Inherited
通过对注解上使用元注解Inherited声明出的注解，在使用时用在类上，可以被子类所继承，对属性或方法无效。

@Caching注解
cache的组合注解

@PageableDefault注解
Pageable 是Spring Data库中定义的一个接口，该接口是所有分页相关信息的一个抽象，通过该接口，我们可以得到和分页相关所有信息（例如pageNumber、pageSize等）。

Pageable定义了很多方法，但其核心的信息只有两个：一是分页的信息（page、size），二是排序的信息。

在springmvc的请求中只需要在方法的参数中直接定义一个pageable类型的参数，当Spring发现这个参数时，Spring会自动的根据request的参数来组装该pageable对象，Spring支持的request参数如下：

page，第几页，从0开始，默认为第0页  
size，每一页的大小，默认为20  
sort，排序相关的信息，以property,property(,ASC|DESC)的方式组织，例如sort=firstname&sort=lastname,desc表示在按firstname正序排列基础上按lastname倒序排列。  
这样，我们就可以通过url的参数来进行多样化、个性化的查询。

Spring data提供了@PageableDefault帮助我们个性化的设置pageable的默认配置。例如@PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC)表示默认情况下我们按照id倒序排列，每一页的大小为15。

@ResponseBody  
@RequestMapping(value = "list", method=RequestMethod.GET)  
public Page<blog> listByPageable(@PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC)  
    Pageable pageable) {  
    return blogRepository.findAll(pageable);  
}

# 导出数据		
response.setHeader("Content-disposition", disposition);
response.setContentType("application/force-download;charset=utf-8");
IOUtils.copy(is, os);
Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，文件直接在浏览器上显示或者在访问时弹出文件下载对话框。


```

```java
1.@ModelAttribute注释方法 
（1）@ModelAttribute注释void返回值的方法 
@Controller
    public class HelloWorldController {
 
        @ModelAttribute
        public void populateModel(@RequestParam String abc, Model model) {
           model.addAttribute("attributeName", abc);
        }
 
        @RequestMapping(value = "/helloWorld")
        public String helloWorld() {
           return "helloWorld";
        }
    }
    因为加了@RequestParam所以abc必输入，/helloWorld?abc=123，则model中会多个属性["attributeName"：123]
    
 （2）@ModelAttribute 注释返回具体类的方法 
 @ModelAttribute
    public Account addAccount(@RequestParam String number) {
       return accountManager.findAccount(number);
    }

     这种情况，model属性的名称没有指定，它由返回类型隐含表示，如这个方法返回Account类型，那么这个model属性的名称是account,类似["account"：xxxx]
    
 （3）@ModelAttribute(value="")注释返回具体类的方法 
 	 @ModelAttribute("attributeName")
        public String addAccount(@RequestParam String abc) {
           return abc;
        }
 这个例子中使用@ModelAttribute注释的value属性，来指定model属性的名称。model属性对象就是方法的返回值。它无须要特定的参数。
 
 （4）@ModelAttribute和@RequestMapping同时注释一个方法 
 	@Controller
    public class HelloWorldController {
 
        @RequestMapping(value = "/helloWorld.do")
        @ModelAttribute("attributeName")
        public String helloWorld() {
           return "hi";
        }
    }

    这时这个方法的返回值并不是表示一个视图名称，而是model属性的值，视图名称由RequestToViewNameTranslator根据请求"/helloWorld.do"转换为逻辑视图helloWorld。
    Model属性名称有@ModelAttribute(value=””)指定，相当于在request中封装了key=attributeName，value=hi。
    
    
    2.@ModelAttribute注释一个方法的参数 
	（1）从model中获取
    @Controller
    public class HelloWorldController {
 
        @ModelAttribute("user")
        public User addAccount() {
           return new User("jz","123");
        }
 
        @RequestMapping(value = "/helloWorld")
        public String helloWorld(@ModelAttribute("user") User user) {
           user.setUserName("jizhou");
           return "helloWorld";
        }
    }
   在这个例子里，@ModelAttribute("user") User user注释方法参数，参数user的值来源于addAccount()方法中的model属性。
    此时如果方法体没有标注@SessionAttributes("user")，那么scope为request，如果标注了，那么scope为session
    
    （2）从Form表单或URL参数中获取（实际上，不做此注释也能拿到user对象） 
    @Controller
        public class HelloWorldController {

            @RequestMapping(value = "/helloWorld")
            public String helloWorld(@ModelAttribute User user) {
               return "helloWorld";
            }
        }
	注意这时候这个User类一定要有没有参数的构造函数。
```

