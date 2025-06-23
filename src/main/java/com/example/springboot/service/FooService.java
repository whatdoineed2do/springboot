package com.example.springboot.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.dynamic.service", havingValue = "foo")
public class FooService implements DynamicService
{

    @Override
    public String what()
    {
        return "FooService";
    }
}
