package io.mojaloop.connector.service;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.misc.pubsub.local.LocalPubSub;
import io.mojaloop.component.misc.pubsub.local.LocalPubSubClient;
import io.mojaloop.component.vault.VaultConfigurer;
import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.connector.service.inbound.ConnectorInboundApplication;
import io.mojaloop.connector.service.outbound.ConnectorOutboundApplication;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
    ConnectorServiceConfiguration.class, ConnectorServiceApplication.VaultSettings.class, ConnectorServiceSettings.class,
    ConnectorServiceApplication.RequiredDependencies.class})
class ConnectorServiceApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(ConnectorServiceApplication.class)
            .web(WebApplicationType.NONE)
            .properties("spring.profiles.active=prod")
            .child(ConnectorInboundApplication.class)
            .properties("spring.application.name=connector-inbound", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                        "spring.jmx.default-domain=connector-inbound", "spring.application.admin.enabled=true",
                        "management.server.port=9080", "management.endpoints.web.base-path=/actuator",
                        "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=Inbound,context=connector-inbound")
            .web(WebApplicationType.SERVLET)
            .sibling(ConnectorOutboundApplication.class)
            .properties("spring.application.name=connector-outbound", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                        "spring.jmx.default-domain=connector-outbound", "spring.application.admin.enabled=true",
                        "management.server.port=9081", "management.endpoints.web.base-path=/actuator",
                        "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=Outbound,context=connector-outbound")
            .web(WebApplicationType.SERVLET)
            .run(args);
    }

    public static class RequiredDependencies implements ConnectorServiceConfiguration.RequiredBeans {

        private final LocalPubSubClient localPubSubClient = new LocalPubSubClient(new LocalPubSub());

        @Bean
        @Override
        public FspAdapter fspAdapter() {

            return new FspAdapter() {

                @Override
                public PartiesTypeIDPutResponse getParties(PartyIdType partyIdType, String partyId, String subId) {

                    return null;
                }

                @Override
                public TransfersIDPutResponse initiateTransfer(TransfersPostRequest request) {

                    return null;
                }

                @Override
                public void notifyTransfer(TransfersIDPatchResponse response) {

                }

                @Override
                public QuotesIDPutResponse quote(QuotesPostRequest request) {

                    return null;
                }
            };
        }

        @Bean
        @Override
        public PubSubClient pubSubClient() {

            return this.localPubSubClient;
        }

    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
