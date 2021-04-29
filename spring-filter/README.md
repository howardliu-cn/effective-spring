# spring-filter: 自定义Filter

- [Spring 实战：自定义 Filter 优雅获取请求参数和响应结果](https://www.howardliu.cn/spring-request-recorder/)

## 借助IDEA的HTTP工具模拟接口调用

```call
###
GET http://localhost:8080/index/get?name=howard

###
POST http://localhost:8080/index/post
Content-Type: application/json

{"name":"howard"}

###
POST http://localhost:8080/index/upload
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="file"; filename="history.txt"
Content-Type: multipart/form-data
</Users/liuxh/history.txt
--WebAppBoundary--

###
GET http://localhost:8080/index/download

```

## 控制台日志

```log
2021-04-29 16:36:26.732  INFO 75466 --- [nio-8080-exec-9] c.h.d.s.filter.AccessLogFilter           : time=2ms,ip=127.0.0.1,uri=/index/get,headers=[host:localhost:8080,connection:Keep-Alive,user-agent:Apache-HttpClient/4.5.12 (Java/11.0.7),accept-encoding:gzip,deflate],status=200,requestContentType=null,responseContentType=text/plain;charset=UTF-8,params=name=howard,request=,response=
2021-04-29 16:36:26.835  INFO 75466 --- [io-8080-exec-10] c.h.d.s.filter.AccessLogFilter           : time=1ms,ip=127.0.0.1,uri=/index/post,headers=[content-type:application/json,content-length:17,host:localhost:8080,connection:Keep-Alive,user-agent:Apache-HttpClient/4.5.12 (Java/11.0.7),accept-encoding:gzip,deflate],status=200,requestContentType=application/json,responseContentType=application/json,params=,request={"name":"howard"},response={"name":"howard","timestamp":"1619685386834"}
2021-04-29 16:36:26.879  INFO 75466 --- [nio-8080-exec-1] c.h.d.s.filter.AccessLogFilter           : time=2ms,ip=127.0.0.1,uri=/index/upload,headers=[content-type:multipart/form-data; boundary=WebAppBoundary,content-length:232,host:localhost:8080,connection:Keep-Alive,user-agent:Apache-HttpClient/4.5.12 (Java/11.0.7),accept-encoding:gzip,deflate],status=200,requestContentType=multipart/form-data; boundary=WebAppBoundary,responseContentType=application/json,params=,request=,response={"contentLength":"0","contentType":"multipart/form-data"}
2021-04-29 16:36:26.899  INFO 75466 --- [nio-8080-exec-2] c.h.d.s.filter.AccessLogFilter           : time=5ms,ip=127.0.0.1,uri=/index/download,headers=[host:localhost:8080,connection:Keep-Alive,user-agent:Apache-HttpClient/4.5.12 (Java/11.0.7),accept-encoding:gzip,deflate],status=200,requestContentType=null,responseContentType=application/octet-stream;charset=utf-8,params=,request=,response=
```
