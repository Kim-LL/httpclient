package org.apache.http;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestDemo {
    public static void main(String[] args) {

        int maxTotal = 10;
        int defaultMaxPerRoute = 10;
        int connectTimeout = 3000;
        int connectionRequestTimeout = 500;
        int socketTimeout = 10000;
        int validateAfterInactivity = 10000;
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 最大链接数
        httpClientConnectionManager.setMaxTotal(maxTotal);
        // 并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        // 获取连接后 再次校验是否空闲超时
        httpClientConnectionManager.setValidateAfterInactivity(validateAfterInactivity);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);

        List<BasicHeader> headers = new ArrayList<>();
        headers.add(new BasicHeader("Connection", "Keep-Alive"));
        headers.add(new BasicHeader("Accept", "*/*"));
        httpClientBuilder.setDefaultHeaders(headers);
        RequestConfig.Builder builder = RequestConfig.custom();

        RequestConfig requestConfig = builder.setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout).build();

        CloseableHttpClient httpClient = httpClientBuilder.setDefaultRequestConfig(requestConfig).build();

        String url = "https://www.baidu.com/";
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> params = List.of(
            new BasicHeader("type", "json"),
            new BasicHeader("HOME", "/root")
        );
        List<BasicHeader> headerList = List.of(new BasicHeader("Content-Type", "application/json"));

        httpPost.setHeaders(headerList.toArray(new BasicHeader[0]));
        JSONObject obj = new JSONObject();
        for(NameValuePair param: params){
            obj.put(param.getName(), param.getValue());
        }

        StringEntity entity = new StringEntity(obj.toJSONString(), StandardCharsets.UTF_8.displayName());
        entity.setContentEncoding(StandardCharsets.UTF_8.displayName());
        httpPost.setEntity(entity);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            long connectElapseMillisecond = response.getConnectElapseMillisecond();
            String remoteAddress = response.getRemoteAddress();
            long responseElapseMillisecond = response.getResponseElapseMillisecond();
            System.out.println("address: " + remoteAddress);
            System.out.println("connectElapseMillisecond: " + connectElapseMillisecond);
            System.out.println("responseElapseMillisecond: " + responseElapseMillisecond);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
