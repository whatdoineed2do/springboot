package com.example.springboot.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Log4j2
@Getter
@RefreshScope
@Component
public class Secrets
{
    @Value("${secrets.db.username}")
    private String dbUsername;

    @Value("${secrets.db.password}")
    private String dbPassword;

    private long   lastUpdated;

    public Secrets()
    {
        // timing can mean these values are not initialized yet ..
        log.info("Secrets initialized: {}", this);
        lastUpdated = System.currentTimeMillis();
    }

    @PostConstruct
    private void postInit()
    {
        log.info("Secrets post-initialization complete: {}", this);
    }

    @Override
    public String toString()
    {
        return "Secrets {" + "dbUsername='" + dbUsername + '\'' + ", dbPassword='" + dbPassword + '\'' + '}';
    }
}
