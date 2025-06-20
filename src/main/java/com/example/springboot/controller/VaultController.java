package com.example.springboot.controller;

import com.example.springboot.config.VaultTemplateConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@Validated
@RestController
@RequestMapping(value = "/api/v1/vault", produces = "application/json")
public class VaultController
{
    private VaultTemplateConfig vaultSecrets;

    public VaultController(VaultTemplateConfig vaultSecrets)
    {
        this.vaultSecrets = vaultSecrets;
    }

    @GetMapping("/secret")
    public Object getSecret()
    {
        return Map
                .of("instance", vaultSecrets.getId(), "meta",
                        Map.of("version", vaultSecrets.getMetaVersion(), "created", vaultSecrets.getMetaCreated()),
                        "db", Map.of("username", vaultSecrets.getUsername(), "password", vaultSecrets.getPassword()),
                        "apiKey", vaultSecrets.getKey());
    }
}
