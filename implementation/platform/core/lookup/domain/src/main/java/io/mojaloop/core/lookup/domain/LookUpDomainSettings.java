package io.mojaloop.core.lookup.domain;

import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import io.mojaloop.common.fspiop.FspiopConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class LookUpDomainSettings implements LookUpDomainConfiguration.RequiredSettings {

    private final Vault vault;

    public LookUpDomainSettings(Vault vault) {

        assert vault != null;

        this.vault = vault;
    }

    @Bean
    @Override
    public FspiopConfiguration.Settings fspiopConfigurationSettings() {

        return this.vault.get(VaultPaths.FSPIOP_SETTINGS, FspiopConfiguration.Settings.class);
    }

    public static class VaultPaths {

        public static final String FSPIOP_SETTINGS = "micro/core/lookup/domain/fspiop/settings";

    }

}
