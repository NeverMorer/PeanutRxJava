# Dribble-MVP Android客户端
* * *
# Peanut
重构了[基于Volley的Peanut](https://github.com/gatsbydhn/Peanut)项目。基于RxJava, Retrofit, MVP架构开发的[https://dribbble.com](https://dribbble.com/)第三方客户端。
#### 架构设计
![示意图](https://github.com/gatsbydhn/Peanut/blob/master/image/MVP.png)
在这里我将Fragment作为View,对外界提供更新界面的方法比如showShots()、showLoading()等方法，在生命周期函数中或接收用户响应时调用Presenter提供的方法，将具体逻辑交给Presenter，Presenter从Model获取数据（这里可以提供一个接口，从网络和从数据库获取数据都可实现该接口），Presenter从Model获取数据后，将数据交给View对外提供的更新界面的方法，显示在界面上。
#### 目前完成的功能：
- 浏览Shots, Debuts, Gifs等模块
- 登陆，与网站同步数据
- 分享
- 收藏作品
- 关注作者
- 发表作品

#### 优化：
-	图片预览
-	Material Design设计
-	MVP模式
-	LeakCanary检测内存泄露
-	图片预览
-	RxJava使用

### 关于RxJava和Retrofit学习
可以参考我的博客：
[Retrofit源码分析](http://www.jianshu.com/p/097947afddaf)
[源码分析：Retrofit结合RxJava](http://www.jianshu.com/p/5249b551a1b8)
[RxJava源码浅析一：构造数据源](http://www.jianshu.com/p/2de2976c7b82)
[OKHttp源码解析](http://www.jianshu.com/p/d860bec13b41)

##下载
[小米应用市场](http://app.xiaomi.com/detail/420500)

##截图
![主页](https://github.com/gatsbydhn/Peanut/blob/master/image/shot1.png)
![登陆](https://github.com/gatsbydhn/Peanut/blob/master/image/shot2.png)
![喜爱页](https://github.com/gatsbydhn/Peanut/blob/master/image/shot4.png)
![用户详情页](https://github.com/gatsbydhn/Peanut/blob/master/image/shot3.png)
![喜爱页](https://github.com/gatsbydhn/Peanut/blob/master/image/shot5.png)
![创建作品](https://github.com/gatsbydhn/Peanut/blob/master/image/shot6.png)

## 其他
- 如果发现任何bug，请及时联系我，邮箱：zjqzcsdhn@gmail.com,谢谢！

