package com.example.springboot.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class RestClientConfig
{

    @Value("${ssl.trust-store.path}")
    private Resource trustStore;

    @Value("${ssl.trust-store.password}")
    private String   trustStorePassword;

    @Value("${ssl.trust-store.type}")
    private String   trustStoreType;

    @Bean
    public RestClient restClient(RestClient.Builder builder) throws Exception
    {
        SSLContext                             sslContext                 = buildSslContext();

        // Create an SSL connection socket factory
        var                                    sslConnectionSocketFactory = SSLConnectionSocketFactoryBuilder
                .create()
                .setSslContext(sslContext)
                .build();

        // Create a connection manager with the SSL socket factory
        var                                    connectionManager          = PoolingHttpClientConnectionManagerBuilder
                .create()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        // Build and return the HTTP client
        var                                    apacheHttpClient           = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory             = new HttpComponentsClientHttpRequestFactory(
                apacheHttpClient);

        return builder.requestFactory(requestFactory).build();
    }

    private SSLContext buildSslContext() throws Exception
    {
        KeyStore ks = KeyStore.getInstance(trustStoreType);
        try (InputStream is = trustStore.getInputStream())
        {
            ks.load(is, trustStorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }
}
