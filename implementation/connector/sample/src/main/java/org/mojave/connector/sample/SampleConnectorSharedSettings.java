/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.connector.sample;

import org.mojave.connector.gateway.ConnectorGatewayConfiguration;
import org.mojave.connector.gateway.inbound.ConnectorInboundConfiguration;
import org.mojave.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.mojave.fspiop.component.FspiopComponentConfiguration;
import org.mojave.fspiop.invoker.FspiopInvokerConfiguration;
import org.mojave.fspiop.invoker.api.PartiesService;
import org.mojave.fspiop.invoker.api.QuotesService;
import org.mojave.fspiop.invoker.api.TransfersService;
import org.mojave.fspiop.spec.core.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.HashMap;

public class SampleConnectorSharedSettings
    implements ConnectorGatewayConfiguration.RequiredSettings {

    @Bean
    @Override
    public FspiopInvokerConfiguration.TransportSettings fspiopInvokerTransportSettings() {

        var useMutualTls = Boolean.parseBoolean(System.getenv("INVOKER_USE_MUTUAL_TLS"));

        if (!useMutualTls) {

            return new FspiopInvokerConfiguration.TransportSettings(false, null, null, true);
        }

        return new FspiopInvokerConfiguration.TransportSettings(
            true, new FspiopInvokerConfiguration.TransportSettings.KeyStoreSettings(
            System.getenv("KEYSTORE_P12B64_CONTENT"), System.getenv("KEYSTORE_PASSWORD")),
            new FspiopInvokerConfiguration.TransportSettings.TrustStoreSettings(
                System.getenv("TRUSTSTORE_P12B64_CONTENT"), System.getenv("TRUSTSTORE_PASSWORD")),
            true);
    }

    @Bean
    @Override
    public ConnectorInboundConfiguration.InboundSettings inboundSettings() {

        var useMutualTls = Boolean.parseBoolean(System.getenv("FSPIOP_INBOUND_USE_MUTUAL_TLS"));

        if (!useMutualTls) {

            return new ConnectorInboundConfiguration.InboundSettings(
                Integer.parseInt(System.getenv("FSPIOP_INBOUND_PORT")),
                Integer.parseInt(System.getenv("FSPIOP_INBOUND_MAX_THREAD")),
                Integer.parseInt(System.getenv("FSPIOP_INBOUND_CONNECTION_TIMEOUT")), false, null,
                null);
        }

        return new ConnectorInboundConfiguration.InboundSettings(
            Integer.parseInt(System.getenv("FSPIOP_INBOUND_PORT")),
            Integer.parseInt(System.getenv("FSPIOP_INBOUND_MAX_THREAD")),
            Integer.parseInt(System.getenv("FSPIOP_INBOUND_CONNECTION_TIMEOUT")), true,
            new ConnectorInboundConfiguration.InboundSettings.KeyStoreSettings(
                System.getenv("KEYSTORE_P12B64_CONTENT"), System.getenv("KEYSTORE_PASSWORD"), null),
            new ConnectorInboundConfiguration.InboundSettings.TrustStoreSettings(
                System.getenv("TRUSTSTORE_P12B64_CONTENT"), System.getenv("TRUSTSTORE_PASSWORD")));
    }

    @Bean
    @Override
    public ConnectorOutboundConfiguration.OutboundSettings outboundSettings() {

        return new ConnectorOutboundConfiguration.OutboundSettings(
            Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PORT")),
            Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_MAX_THREAD")),
            Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_CONNECTION_TIMEOUT")),
            Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PUT_RESULT_TIMEOUT")),
            Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PUBSUB_TIMEOUT")),
            System.getenv("FSPIOP_OUTBOUND_PUBLIC_KEY_PEM"),
            Boolean.parseBoolean(System.getenv("FSPIOP_OUTBOUND_SECURED")));
    }

    @Bean
    @Override
    public FspiopComponentConfiguration.ParticipantSettings participantSettings() {

        var hubCode = System.getenv("FSPIOP_HUB_CODE");
        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");

        var currencyNames = System.getenv("FSPIOP_CURRENCIES").split(",", -1);
        var currencies = new ArrayList<Currency>();

        for (var currencyName : currencyNames) {
            currencies.add(Currency.valueOf(currencyName));
        }

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

        return new FspiopComponentConfiguration.ParticipantSettings(
            hubCode, fspCode, fspName,
            currencies, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(PartiesService.Settings.class)
    public PartiesService.Settings partiesServiceSettings() {

        return new PartiesService.Settings(System.getenv("FSPIOP_PARTIES_URL"));
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(QuotesService.Settings.class)
    public QuotesService.Settings quotesServiceSettings() {

        return new QuotesService.Settings(System.getenv("FSPIOP_QUOTES_URL"));
    }

    @Bean
    @Override
    public ConnectorOutboundConfiguration.TransferSettings transferSettings() {

        return new ConnectorOutboundConfiguration.TransferSettings(
            Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_EXPIRE_AFTER_SECONDS")));
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(TransfersService.Settings.class)
    public TransfersService.Settings transfersServiceSettings() {

        return new TransfersService.Settings(System.getenv("FSPIOP_TRANSFERS_URL"));
    }

}
