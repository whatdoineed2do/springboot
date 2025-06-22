package com.example.springboot.controller;

import com.example.springboot.service.SSLWebService;
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
    private SSLWebService sslWebService;

    public SSLEndpointController(SSLWebService sslWebService)
    {
        this.sslWebService = sslWebService;
    }

    @GetMapping("/endpoint")
    public Object hit(@RequestParam(name = "uri", required = true) String path)
    {
        log.info("request for SSL {}", path);
        return sslWebService
                .fetchSecureContent(path)
                .map(content -> Map.of("content", content))
                .doOnError(error -> log.error("Error fetching secure content from {}: {}", path, error.getMessage()));
    }
}
