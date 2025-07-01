package com.example.springboot.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.layer")
public class AppConfig
{
    private List<String> list;

    @PostConstruct
    private void init()
    {
        log.info("AppConfig initialized: {}", list);
    }
}
