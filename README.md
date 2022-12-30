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
    <version>1.0.3</version>
</dependency>
```
gradle 方式引入
```gradle
dependencies {
    implementation 'cc.zenfery:spring-cloud-easy-create-project-starter:1.0.3'
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