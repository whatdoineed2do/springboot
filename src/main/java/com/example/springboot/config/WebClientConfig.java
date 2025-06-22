package com.example.springboot.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class WebClientConfig
{

    @Value("${ssl.trust-store.path}")
    private Resource trustStore;

    @Value("${ssl.trust-store.password}")
    private String   trustStorePassword;

    @Value("${ssl.trust-store.type}")
    private String   trustStoreType;

    @Bean
    public WebClient secureWebClient() throws Exception
    {
        SslContext sslContext = buildNettySslContext();

        HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    private SslContext buildNettySslContext() throws Exception
    {
        KeyStore ks = KeyStore.getInstance(trustStoreType);
        try (InputStream is = trustStore.getInputStream())
        {
            ks.load(is, trustStorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        return SslContextBuilder.forClient().trustManager(tmf).build();
    }
}
