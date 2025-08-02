package io.mojaloop.core.participant.intercom;

import io.mojaloop.component.vault.VaultConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ParticipantIntercomApplication {

    public static void main(String[] args) {

        SpringApplication.run(new Class[]{ParticipantIntercomConfiguration.class, VaultSettings.class, ParticipantIntercomSettings.class},
                              args);
    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
