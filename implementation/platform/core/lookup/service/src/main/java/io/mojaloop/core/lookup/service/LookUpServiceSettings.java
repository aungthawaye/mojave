package io.mojaloop.core.lookup.service;

import io.mojaloop.common.component.spring.security.SpringSecurityConfigurer;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class LookUpServiceSettings implements LookUpServiceConfiguration.RequiredSettings {

    private final Vault vault;

    public LookUpServiceSettings(Vault vault) {

        assert vault != null;

        this.vault = vault;
    }

    @Bean
    @Override
    public LookUpServiceConfiguration.TomcatSettings lookUpServiceTomcatSettings() {

        return this.vault.get(VaultPaths.LOOKUP_SERVICE_SETTINGS, LookUpServiceConfiguration.TomcatSettings.class);
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return this.vault.get(VaultPaths.SPRING_SECURITY_SETTINGS, SpringSecurityConfigurer.Settings.class);
    }

    public static class VaultPaths {

        public static final String LOOKUP_SERVICE_SETTINGS = "micro/core/lookup/service/settings";

        public static final String SPRING_SECURITY_SETTINGS = "micro/core/lookup/service/spring-security/settings";

    }

}
