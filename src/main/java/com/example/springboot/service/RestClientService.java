package com.example.springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RestClientService
{
    private final RestClient restClient;

    public RestClientService(RestClient restClient)
    {
        this.restClient = restClient;
    }

    public String fetchSecureContent(String url)
    {
        return restClient.get().uri(url).retrieve().body(String.class);
    }
}
