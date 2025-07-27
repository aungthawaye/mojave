package io.mojaloop.core.lookup.service;

import io.mojaloop.common.component.vault.VaultConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class LookUpServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(new Class[]{LookUpServiceConfiguration.class, VaultSettings.class}, args);
    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
