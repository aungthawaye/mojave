package io.mojaloop.core.participant.domain;

import io.mojaloop.common.component.vault.VaultConfiguration;
import org.springframework.context.annotation.Bean;

public class LocalVaultSettings {

    public static final String VAULT_ADDR = "";

    public static final String VAULT_TOKEN = "";

    public static final String ENGINE_PATH = "";

    @Bean
    public VaultConfiguration.Settings vaultSettings() {

        return new VaultConfiguration.Settings(VAULT_ADDR, VAULT_TOKEN, ENGINE_PATH);
    }

}
