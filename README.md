# httpclient
# 修改环境 jdk11，当前项目直接在 org.apache.httpcomponents.httpclient 4.5.14 源码进行修改，并且直接将 httpcore httpmime 项目集成到里面。
future:
1. 添加 CloseableHttpResponse 接口 getRemoteAddress() 方法，可以获取调用的真实 ip 地址
2. 添加 CloseableHttpResponse 接口 getConnectElapseMillisecond() 方法，可以获取建立链接消耗的时间(单位:毫秒, 如果获取的是0，说明调用native的时候系统直接就有这个tcp连接)
3. 添加 CloseableHttpResponse 接口 getResponseElapseMillisecond() 方法，可以获取发出请求到接受相应的消耗时间(单位:毫秒)
