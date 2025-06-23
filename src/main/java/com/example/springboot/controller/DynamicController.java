package com.example.springboot.controller;

import com.example.springboot.config.VaultTemplateConfig;
import com.example.springboot.service.DynamicService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@Validated
@RestController
@RequestMapping(value = "/api/v1/dynamic", produces = "application/json")
public class DynamicController
{
    private DynamicService service;

    public DynamicController(DynamicService service)
    {
        this.service = service;
    }

    @GetMapping("/service")
    public Object service()
    {
        return Map.of("service", service.what());
    }
}
