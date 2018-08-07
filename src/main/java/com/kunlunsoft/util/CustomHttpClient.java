package com.kunlunsoft.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.nio.charset.StandardCharsets;

/**
 * Created date: 2017-07-25
 *
 * @author mixta@chanjet.com
 */
public class CustomHttpClient {

    private static CloseableHttpClient customerHttpClient;

    private CustomHttpClient() {
    }

    public static synchronized CloseableHttpClient getHttpClient() {
        if (null == customerHttpClient) {
            // Create a connection manager with custom configuration.
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom()
                    .setTcpNoDelay(true)
                    .setSoKeepAlive(true)
                    .build();

            // Configure the connection manager to use socket configuration by default
            connManager.setDefaultSocketConfig(socketConfig);

            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setCharset(StandardCharsets.UTF_8)
                    .build();
            connManager.setDefaultConnectionConfig(connectionConfig);

            // 设置每个站点的最大连接数
            connManager.setDefaultMaxPerRoute(50);
            connManager.setMaxTotal(200);

            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setStaleConnectionCheckEnabled(true)
                    .build();

            customerHttpClient = HttpClientBuilder.create()
                    .setConnectionManager(connManager)
                    .setDefaultRequestConfig(defaultRequestConfig)
//                    .setRetryHandler(new CustomHttpRequestRetryHandler())
                    .build();
        }

        return customerHttpClient;
    }

}
