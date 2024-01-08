# spring-cloud-easy-create-project
基于 spring cloud ，快速创建项目的基础包。

[TOC]

## 1、支持的功能
- Spring Rest 响应输出标准化。
- 响应内容国际化工具。

## 2、快速开始
### 2.1、 引入包到项目中
maven 方式引入
```xml
<dependency>
    <groupId>cc.zenfery</groupId>
    <artifactId>spring-cloud-easy-create-project-starter</artifactId>
    <version>1.1.3</version>
</dependency>
```
gradle 方式引入
```gradle
dependencies {
    implementation 'cc.zenfery:spring-cloud-easy-create-project-starter:1.1.3'
}
```

### 2.2、响应输出标准化
该功能可以帮助采用 Spring Cloud 项目，提供的接口服务统一格式化输出。
引入包后，默认开启响应输出标准化；开启后，默认采用`DefaultCloudResponseHandler`来进行格式化输出。
- 正确响应
```java
  @GetMapping("/hello")
  public Map<String, String> hello(){
    String hello = "hello world! curr time: " + new Date();
    return new HashMap<>(){{
      put("hello", hello);
    }};
  }
```
请求输出：
```bash
http http://localhost:8080/hello

HTTP/1.1 200
Status: 0

{
    "hello": "hello world! curr time: Fri Dec 30 11:11:29 CST 2022"
}
```

- 异常响应
```java
  @GetMapping("/hello-exception")
  public String helloException(){
    throw new ResponseException("HelloError", "Sorry, Hello Exception happen!!!");
  }
```
请求输出：
```bash
http http://localhost:8080/hello-exception

HTTP/1.1 500
Error-Code: HelloError
Status: 1

{
    "errorCode": "HelloError",
    "msg": "Sorry, Hello Exception happen!!!",
    "status": 1
}
```

## 3、核心功能
### 3.1、Spring Rest 响应输出标准化
### 3.1.1、 开启/关闭响应自动格式化
- 开启响应自动格式化  
引入包后，默认开启响应自动格式化功能；或显式地指定配置，进行开启。
```properties
easycreateproject.response.enabled = true
```

- 关闭响应自动格式化  
若需要关闭该功能，需要增加配置
```properties
easycreateproject.response.enabled = false
```

### 3.1.2、ResponseException
建议应用中，出现异常抛出`cc.zenfery.easycreateproject.response.ResponseException`类型的异常。直接抛出 `ResponseException` 异常，或者自定义 `ResponseException` 类的子类。

> 工具包处理异常的逻辑：
> 
> 根据 errorCode 查找国际化文件`message*.properties`, 找到对应的值赋给 msg 。
> 
> 若国际化文件中不存在，则会采用 `ResponseException` 中传入的 msg 。


### 3.1.3、 自定义响应格式
工具包自带一些处理器：
- `DefaultCloudResponseHandler` 默认格式处理器。
- `CamelBodyCloudResponseHandler` 驼峰 body 格式处理器。
标准响应字段(status, errorCode, msg, data)全部在 body 中。
```bash
## 正确响应
curl http://localhost:8080/hello -i
HTTP/1.1 200
Status: 0
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Thu, 23 Feb 2023 07:28:05 GMT

{"data":{"hello":"hello world"},"status":0}

## 异常响应
{
    "errorCode": "HelloError",
    "msg": "Sorry, Hello Exception happen!!!",
    "status": 1
}
```

若工具包提供的不能满足需要，也可自定义实现。
1. 首先定义接口 `CloudResponseHandler` 的实现类。
2. 将实现注入到 Spring 容器中。
```java
@Configuration
public class ResponseConfiguration {

    @Bean
    public CloudResponseHandler cloudResponseHandler() {
        return new CamelBodyCloudResponseHandler();
    }
}
```

## 发布历史
### 1.1.3 2024-01-08
- 修改：出现异常优先从国际化message中查找信息，若查找不到，则取异常 ResponseException 中指定的 msg。
- 解决：异常出现时，视图一直返回 "error" 字符串，导致Spring获取错误的消息转器，导致消息转换异常，进而导致不响应开发者格式化结果。

### 1.1.2 2024-01-05
- 优化异常时，msg 的获取逻辑。

### 1.1.0 2023-02-23
- 增加 CamelBodyCloudResponseHandler 格式化处理器。