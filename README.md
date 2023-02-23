# spring-cloud-easy-create-project
基于 spring cloud ，快速创建项目的基础包。

## 支持的功能
- Spring Rest 响应输出标准化。
- 响应内容国际化工具。

## 快速开始
### 引入包到项目中
maven 方式引入
```xml
<dependency>
    <groupId>cc.zenfery</groupId>
    <artifactId>spring-cloud-easy-create-project-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```
gradle 方式引入
```gradle
dependencies {
    implementation 'cc.zenfery:spring-cloud-easy-create-project-starter:1.1.0'
}
```

### 响应输出标准化
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

#### 开启响应自动格式化
引入包后，默认开启响应自动格式化功能。若需要关闭该功能，需要增加配置 `easycreateproject.response.enabled = false`。

#### 自定义响应格式
默认采用 `DefaultCloudResponseHandler` 来进行格式化。
工具包还提供了其它可选项：
- `CamelBodyCloudResponseHandler`。响应字段全部在 body 中。
```bash
## 正确响应
curl http://localhost:8080/hello -i
HTTP/1.1 200
Status: 0
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Thu, 23 Feb 2023 07:28:05 GMT

{"data":{"hello":"hello world"},"status":0}
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
### 1.1.0 2023-02-23
- 增加 CamelBodyCloudResponseHandler 格式化处理器。