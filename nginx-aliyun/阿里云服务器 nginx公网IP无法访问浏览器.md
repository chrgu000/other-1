# 阿里云服务器 nginx公网IP无法访问浏览器

【转载】：https://blog.csdn.net/sinat_25957705/article/details/80641077

## 一、开始找原因

在浏览器输入：[http://ip](http://ip/)，正常的话，会有页面，welcome to nginx 
我这里是浏览器访问失败， 
查找原因：

## 1、在服务器上访问Ip

执行：curl [http://ip](http://ip/) 
结果：超时，未连接成功

查看nginxaccess.log日志，发现日志未滚动，代表本机访问不到公网ip

## 2、确认网络是否可达

```
telnet 12x.xx.x.xx 80

Trying 12x.xx.x.xx...

Connected to 12x.xx.x.xx.

Escape character is '^]'.1234
```

这样就说明网络上可达，并且TCP三次握手可以完成，因为能telnet通，排除了网络不通的情况 
但是我本地的网络不通。

## 3、是否受防火墙安全控制等。

将iptables和selinux关闭 
以下4条命令清除iptables的配置

```
iptables -F

iptables -F -t nat

iptables -X

iptables -X -t nat1234
```

setenforce 0 #关闭selinux 
重新远程访问，还是失败。说明不是防火墙的原因。这里因为我是debian，所以默认是没有开启防火墙的，所以不是这个原因。

## 4、去/var/log/nginx/error.log查看错误日志

日志报错：Address already in use 
这里就已经确认错误在哪了，原来是80端口被占用的问题。

## 5、解决方案

1）执行：lsof -i:80 查看此时哪个进程正在使用80端口 
我这边发现是阿里云盾正在使用80端口

2）杀掉云盾进程，重启nginx试试 
kill -s 9 pid 
具体参考博客：<http://blog.csdn.net/ljfphp/article/details/78666376>

## 6、好吧，惊奇的发现，通过浏览器还是访问不了nginx，但是此刻我的80端口已经被nginx监听了。继续查看错误日志。错误日志并没刷新。

7、百度发现有人说是iptables防火墙的原因，但是debian系统的安装默认是没有iptables,所以应该不是防火墙的原因。

8、看到有人说，是没有备案域名的原因，80端口默认是关闭状态。好吧，我准备改成8080端口，看看能不能行吧。（这个没来得及试）

## 二、真正的凶手

​      终于找到问题了，眼泪差点掉下来。原来是阿里云的问题。我刚开通的服务器，没有设置安全组规则。下面给大家演示一下安全组怎么设置。 
1、进入云服务控制台

2、找到安全组，点击进入 
![这里写图片描述](https://github.com/jjj2010/other/blob/master/nginx-aliyun/pic/20171129215539409.png)
3、在默认的一个安全组上，有一个配置规则按钮。点击配置规则 
![这里写图片描述](https://github.com/jjj2010/other/blob/master/nginx-aliyun/pic/20171129215548762.png)
4、这是我原来的安全组规则，没有http的

![这里写图片描述](https://github.com/jjj2010/other/blob/master/nginx-aliyun/pic/20171129215630643.png)

5、按照如图所示添加

![这里写图片描述](https://github.com/jjj2010/other/blob/master/nginx-aliyun/pic/20171129215646583.png)

6、浏览器访问ip成功 
![这里写图片描述](https://github.com/jjj2010/other/blob/master/nginx-aliyun/pic/20171129215700564.png)

配置好安全规则之后，我们就能在浏览器通过ip访问了。。千想万想，没想到会收到来自阿里云的GANK,很绝望。。不过在查错过程中也学到了很多，记录一下。

end