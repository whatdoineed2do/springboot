package com.example.springboot.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
class VaultRefresher
{

    private final ContextRefresher contextRefresher;

    VaultRefresher(ContextRefresher contextRefresher)
    {
        this.contextRefresher = contextRefresher;
    }

    @Scheduled(initialDelayString = "${secrets.refresh-interval-ms:10000}", fixedDelayString = "${secrets.refresh-interval-ms:10000}")
    void refresher()
    {
        contextRefresher.refresh();
        log.debug("refresh key-value secret");
    }
}
