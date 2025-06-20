package com.example.springboot.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

@Log4j2
@Getter
@RefreshScope
@Component
public class VaultTemplateConfig
{
    @Autowired
    private VaultTemplate vaultTemplate;

    private String        username;
    private String        password;
    private String        key;

    @PostConstruct
    void init()
    {
        // kv2/secret/app
        // vault kv put kv2/secret/app secret.db.user=admin secret.db.password=secret
        // secret.api.key=1234567890
        VaultResponse response = vaultTemplate
                .opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                .get("app");
        username = response.getData().get("secret.db.user").toString();
        password = response.getData().get("secret.db.password").toString();
        key = response.getData().get("secret.api.key").toString();
        log.info("Vault Secrets initialized with username={}, password={}, key={}", username, password, key);
    }

    /*
     * @Value("${spring.cloud.vault.uri}") private String uri;
     *
     * @Value("${spring.cloud.vault.token}") private String token;
     *
     * @Value("${spring.cloud.vault.namespace:}") private String namespace;
     *
     * @PostConstruct void init() {
     * log.info("VaultTemplateConfig initialized with URI={} token={} namespace={}",
     * uri, token, namespace); }
     *
     * @Bean public VaultTemplate vaultTemplate() { var template = new
     * VaultTemplate(VaultEndpoint.from(URI.create(uri)), new
     * TokenAuthentication(token)); if (!namespace.isEmpty()) { VaultTemplate() }
     * return new VaultTemplate(endpoint, new TokenAuthentication(token)); }
     */
}
