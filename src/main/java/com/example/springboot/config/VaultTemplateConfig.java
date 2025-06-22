package com.example.springboot.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Getter
@RefreshScope
@ConditionalOnProperty(name = "app.vault.enable", havingValue = "true", matchIfMissing = false) // feature toggle
@Component
public class VaultTemplateConfig
{
    @Autowired
    @Getter(AccessLevel.NONE)
    private VaultTemplate                        vaultTemplate;

    @Getter
    private static int                           id          = 0;

    private Map<String, AtomicReference<String>> secrets;

    private AtomicReference<String>              metaVersion = new AtomicReference<>();
    private AtomicReference<String>              metaCreated = new AtomicReference<>();
    private AtomicReference<String>              username    = new AtomicReference<>();
    private AtomicReference<String>              password    = new AtomicReference<>();
    private AtomicReference<String>              key         = new AtomicReference<>();

    public VaultTemplateConfig()
    {
        ++VaultTemplateConfig.id;
    }

    @PostConstruct
    void init()
    {
        var immutable = Map
                .of("metadata.created_time", metaCreated, "metadata.version", metaVersion, "secret.db.user", username,
                        "secret.db.password", password, "secret.api.key", key);
        secrets = Map.copyOf(immutable);

        // secret/app/stuff <-- secret/ is the vault path prefix/mount point
        // vault kv put secret/app/stuff secret.db.user=admin secret.db.password=secret
        // secret.api.key=1234567890
        VaultResponse response = vaultTemplate
                .opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                .get("app/stuff");

        secrets.forEach((key, value) -> {
            var what = response.getData().get(key);
            if (what == null)
            {
                log.warn("Vault response for key '{}' is null", key);
                value.set(null);
                return;
            }
            value.set(what.toString());
        });
        log.info("Vault Secrets initialized with {}", secrets);
    }
}
