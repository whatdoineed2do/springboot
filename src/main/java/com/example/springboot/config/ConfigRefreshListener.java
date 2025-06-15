package com.example.springboot.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ConfigRefreshListener implements ApplicationListener<RefreshScopeRefreshedEvent>
{

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event)
    {
        log.info("Configuration scope has been refreshed");
        /*
         * You can add additional logic here if needed, such as logging or notifying
         * other components NB: not necessary to refresh beans manually (ie Secrets.java
         * and its @Value members), as they will be refreshed automatically
         */
    }
}
