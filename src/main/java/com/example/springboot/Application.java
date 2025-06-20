package com.example.springboot;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Application
{

    public static void main(String[] args)
    {
        log.debug("process starting");
        SpringApplication.run(Application.class, args);
        log.debug("process terminating");
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx)
    {
        return args -> {
            log.debug("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames)
            {
                log.debug("  " + beanName);
            }
            log
                    .info("beans total=" + beanNames.length + "  appname='" + ctx.getApplicationName() + "' displname='"
                            + ctx.getDisplayName() + "' env='" + ctx.getEnvironment() + "'");
        };
    }
}
