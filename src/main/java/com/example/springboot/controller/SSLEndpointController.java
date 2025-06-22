package com.example.springboot.controller;

import com.example.springboot.service.RestClientService;
import com.example.springboot.service.WebClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@Validated
@RestController
@RequestMapping(value = "/api/v1/ssl", produces = "application/json")
public class SSLEndpointController
{
    private WebClientService  webClientService;
    private RestClientService restClientService;

    public SSLEndpointController(WebClientService webClientService, RestClientService restClietntService)
    {
        this.restClientService = restClietntService;
        this.webClientService = webClientService;
    }

    @GetMapping("/endpoint/webclient")
    public Object hitViaWebClient(@RequestParam(name = "uri", required = true) String path)
    {
        log.info("request for SSL via WebClient {}", path);
        return webClientService
                .fetchSecureContent(path)
                .map(content -> Map.of("content", content))
                .doOnError(error -> log.error("Error fetching secure content from {}: {}", path, error.getMessage()));
    }

    @GetMapping("/endpoint/restclient")
    public Object hitViaRestClient(@RequestParam(name = "uri", required = true) String path)
    {
        log.info("request for SSL via RestClient {}", path);
        return restClientService.fetchSecureContent(path);
    }
}
