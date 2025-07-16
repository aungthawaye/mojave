package io.mojaloop.common.component.vault;

import org.springframework.context.annotation.Bean;

public class DefaultVaultSettings {

    @Bean
    public VaultConfigurer.Settings vaultSettings() {

        return VaultConfigurer.Settings.withPropertyOrEnv();
    }

}
