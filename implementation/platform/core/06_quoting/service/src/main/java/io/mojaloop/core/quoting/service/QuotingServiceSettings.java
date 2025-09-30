package io.mojaloop.core.quoting.service;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

public class QuotingServiceSettings implements QuotingServiceConfiguration.RequiredSettings {

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopCommonParticipantSettings() {

        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");
        var ilpSecret = System.getenv("FSPIOP_ILP_SECRET");
        var signJws = Boolean.parseBoolean(System.getenv("FSPIOP_SIGN_JWS"));
        var verifyJws = Boolean.parseBoolean(System.getenv("FSPIOP_VERIFY_JWS"));
        var privateKeyPem = System.getenv("FSPIOP_PRIVATE_KEY_PEM");
        var fsps = System.getenv("FSPIOP_FSPS").split(",", -1);
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
    public QuotingServiceConfiguration.TomcatSettings quotingServiceTomcatSettings() {

        return new QuotingServiceConfiguration.TomcatSettings(Integer.parseInt(System.getenv().getOrDefault("QUOTING_SERVICE_PORT", "4503")));
    }

    @Bean
    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return new ParticipantIntercomService.Settings(System.getenv().getOrDefault("PARTICIPANT_INTERCOM_BASE_URL", "http://localhost:4202"));
    }

    @Bean
    @Override
    public ParticipantStoreConfiguration.Settings participantStoreSettings() {

        return new ParticipantStoreConfiguration.Settings(Integer.parseInt(System.getenv().getOrDefault("PARTICIPANT_STORE_REFRESH_INTERVAL_MS", "300000")));
    }

    @Bean
    @Override
    public QuotingDomainConfiguration.QuoteSettings quoteSettings() {

        return new QuotingDomainConfiguration.QuoteSettings(Boolean.parseBoolean(System.getenv().getOrDefault("QUOTING_STATEFUL", "true")));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(System.getenv()
                                                                                       .getOrDefault("QOT_READ_DB_URL",
                                                                                                     "jdbc:mysql://localhost:3306/ml_quoting?createDatabaseIfNotExist=true"),
                                                                                 System.getenv().getOrDefault("QOT_READ_DB_USER", "root"),
                                                                                 System.getenv().getOrDefault("QOT_READ_DB_PASSWORD", "password"),
                                                                                 false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool("quoting-service-read",
                                                                     Integer.parseInt(System.getenv().getOrDefault("QOT_READ_DB_MIN_POOL_SIZE", "2")),
                                                                     Integer.parseInt(System.getenv().getOrDefault("QOT_READ_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(System.getenv()
                                                                                        .getOrDefault("QOT_WRITE_DB_URL",
                                                                                                      "jdbc:mysql://localhost:3306/ml_quoting?createDatabaseIfNotExist=true"),
                                                                                  System.getenv().getOrDefault("QOT_WRITE_DB_USER", "root"),
                                                                                  System.getenv().getOrDefault("QOT_WRITE_DB_PASSWORD", "password"),
                                                                                  false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool("quoting-service-write",
                                                                      Integer.parseInt(System.getenv().getOrDefault("QOT_WRITE_DB_MIN_POOL_SIZE", "2")),
                                                                      Integer.parseInt(System.getenv().getOrDefault("QOT_WRITE_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("quoting-service", false, false);
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/quotes/**"});
    }

}
