package com.example.springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SSLWebService
{

    private final WebClient secureWebClient;

    public SSLWebService(WebClient secureWebClient)
    {
        this.secureWebClient = secureWebClient;
    }

    public Mono<String> fetchSecureContent(String url)
    {
        return secureWebClient.get().uri(url).retrieve().bodyToMono(String.class);
    }
}
