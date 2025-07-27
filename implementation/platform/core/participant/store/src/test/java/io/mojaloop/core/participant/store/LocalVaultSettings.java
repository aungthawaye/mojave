package io.mojaloop.core.participant.store;

import io.mojaloop.common.component.vault.VaultConfigurer;
import org.springframework.context.annotation.Bean;

public class LocalVaultSettings {

    public static final String VAULT_ADDR = "http://localhost:8200";

    public static final String VAULT_TOKEN = "hvs.GJBnvtehSoCzpvF8TFibWbUU";

    public static final String ENGINE_PATH = "Mojaloop";

    @Bean
    public VaultConfigurer.Settings vaultSettings() {

        return new VaultConfigurer.Settings(VAULT_ADDR, VAULT_TOKEN, ENGINE_PATH);
    }

}
