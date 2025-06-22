package com.example.springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

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
        return secureWebClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("Server error")))
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)));
    }
}
