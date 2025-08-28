package io.mojaloop.core.participant.admin;

import io.mojaloop.component.vault.VaultConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ParticipantAdminApplication {

    public static void main(String[] args) {

        SpringApplication.run(new Class[]{ParticipantAdminConfiguration.class, VaultSettings.class, ParticipantAdminSettings.class}, args);
    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
