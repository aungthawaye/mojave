package io.mojaloop.connector.service;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.misc.pubsub.local.LocalPubSub;
import io.mojaloop.component.misc.pubsub.local.LocalPubSubClient;
import io.mojaloop.component.vault.VaultConfigurer;
import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
class ConnectorServiceApplication implements ConnectorServiceConfiguration.RequiredBeans {

    public static void main(String[] args) {

        SpringApplication.run(new Class[]{ConnectorServiceConfiguration.class, VaultSettings.class, ConnectorServiceSettings.class}, args);
    }

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

        return new LocalPubSubClient(new LocalPubSub());
    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
