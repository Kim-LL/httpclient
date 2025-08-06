package org.apache.http.broker;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class Login {

    public static void main(String[] args) {

        // 创建一个 CookieStore 用来存储 Cookies
        CookieStore cookieStore = new BasicCookieStore();

        // 使用 CookieStore 构建 HttpClient
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
//        HttpPost post = new HttpPost("https://sitweb.da9893.com/api/user/authorizeWeb");
        HttpPost post = new HttpPost("http://localhost:7120/api/user/authorizeWeb");
        // 可选：设置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .build();
        post.setConfig(requestConfig);

        HttpEntity entity = null;
        List<BasicNameValuePair> params = List.of(
                new BasicNameValuePair("username", "icp_java@da9893.com"),
                new BasicNameValuePair("password", "94e8cde4612b3fd390677d42e7b22002"));
        try {
            entity = new UrlEncodedFormEntity(params, "utf-8");
            post.setEntity(entity);
            post.setHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"));
            CloseableHttpResponse response = httpClient.execute(post);

            entity = response.getEntity();
            System.out.println("响应体: " + EntityUtils.toString(entity));

            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                System.out.println("header: " + header.getName() + " = " + header.getValue());
            }
            // 获取 CookieStore 中的 Cookies
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                System.out.println("Cookie Name: " + cookie.getName());
                System.out.println("Cookie Value: " + cookie.getValue());
                System.out.println("Cookie Domain: " + cookie.getDomain());
                System.out.println("Cookie Path: " + cookie.getPath());
                System.out.println("Cookie Expiry Date: " + cookie.getExpiryDate());
                System.out.println("---------------------------------");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
