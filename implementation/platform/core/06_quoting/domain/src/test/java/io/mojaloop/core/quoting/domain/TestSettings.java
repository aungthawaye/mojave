package io.mojaloop.core.quoting.domain;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

public class TestSettings implements QuotingDomainConfiguration.RequiredSettings {

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopCommonParticipantSettings() {

        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");
        var ilpSecret = System.getenv("FSPIOP_ILP_SECRET");
        var signJws = Boolean.parseBoolean(System.getenv("FSPIOP_SIGN_JWS"));
        var verifyJws = Boolean.parseBoolean(System.getenv("FSPIOP_VERIFY_JWS"));
        var privateKeyPem = System.getenv("FSPIOP_PRIVATE_KEY_PEM");
        var fspsEnv = System.getenv("FSPIOP_FSPS");
                var fsps = (fspsEnv == null || fspsEnv.isBlank()) ? new String[0] : fspsEnv.split(",", -1);
        var fspPublicKeyPem = new HashMap<String, String>();

        for (var fsp : fsps) {

            var env = "FSPIOP_PUBLIC_KEY_PEM_OF_" + fsp.toUpperCase();
            var publicKeyPem = System.getenv(env);

            if (publicKeyPem != null) {
                fspPublicKeyPem.put(fsp, publicKeyPem);
            }
        }

        return new FspiopCommonConfiguration.ParticipantSettings(fspCode, fspName, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);
    }

    @Bean
    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return new ParticipantIntercomService.Settings(System.getenv().getOrDefault("PARTICIPANT_INTERCOM_SERVICE_SETTINGS", "http://localhost:4102"));
    }

    @Bean
    @Override
    public ParticipantStoreConfiguration.Settings participantStoreSettings() {

        return new ParticipantStoreConfiguration.Settings(Integer.parseInt(System.getenv().getOrDefault("PARTICIPANT_STORE_REFRESH_INTERVAL_MS", "60000")));
    }

    @Bean
    @Override
    public QuotingDomainConfiguration.QuoteSettings quoteSettings() {

        return new QuotingDomainConfiguration.QuoteSettings(true);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        return new RoutingDataSourceConfigurer.ReadSettings(new RoutingDataSourceConfigurer.ReadSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_quoting?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root",
            "password",
            false), new RoutingDataSourceConfigurer.ReadSettings.Pool("participant-read", 2, 4));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        return new RoutingDataSourceConfigurer.WriteSettings(new RoutingDataSourceConfigurer.WriteSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_quoting?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root",
            "password",
            false), new RoutingDataSourceConfigurer.WriteSettings.Pool("participant-read", 2, 4));
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("participant-domain", false, false);
    }

}
