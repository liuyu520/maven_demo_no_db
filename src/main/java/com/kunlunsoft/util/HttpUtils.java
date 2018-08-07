package com.kunlunsoft.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created date: 2017-07-25
 *
 * @author mixta@chanjet.com
 */
public class HttpUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 发送HTTP POST请求
     *
     * @param url    地址
     * @param params 参数列表
     * @return HTTP body
     */
    public static String post(String url, Map<String, String> params) {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> formParams = new ArrayList<>();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        post.setEntity(new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));

        return getContent(post);
    }

    /**
     * 发送HTTP GET请求
     *
     * @param url 地址
     * @return HTTP body
     */
    public static String get(String url) {
        HttpGet get = new HttpGet(url);
        return getContent(get);
    }

    /**
     * 执行HTTP请求, 返回结果
     *
     * @param request 请求
     * @return HTTP body内容
     */
    public static String getContent(HttpUriRequest request) {
        CloseableHttpClient httpClient = CustomHttpClient.getHttpClient();
        CloseableHttpResponse response = null;

        try {
            log.info("Start to call {}", request.getURI());
            response = httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            log.info("End to call {}, result is {}", request.getURI(), statusLine);
//            if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
//                throw new HttpException();
//            }

            HttpEntity respEntity = response.getEntity();
            if (respEntity != null) {
                return EntityUtils.toString(respEntity, StandardCharsets.UTF_8);
            }

            return null;
        } catch (Exception e) {
            log.info("HttpUtil exception occurred", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return null;
    }

}
