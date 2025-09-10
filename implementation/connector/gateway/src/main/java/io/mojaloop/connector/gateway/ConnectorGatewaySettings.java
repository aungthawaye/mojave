package io.mojaloop.connector.gateway;

import io.mojaloop.component.misc.handy.P12Reader;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.connector.gateway.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import io.mojaloop.fspiop.invoker.api.PartiesService;
import io.mojaloop.fspiop.invoker.api.QuotesService;
import io.mojaloop.fspiop.invoker.api.TransfersService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ConnectorGatewaySettings implements ConnectorGatewayConfiguration.RequiredSettings {

    @Bean
    @Override
    @ConditionalOnMissingBean(FspiopCommonConfiguration.ParticipantSettings.class)
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
    public FspiopInvokerConfiguration.TransportSettings fspiopInvokerTransportSettings() {

        var useMutualTls = Boolean.parseBoolean(System.getenv().getOrDefault("INVOKER_USE_MUTUAL_TLS", "false"));

        if (!useMutualTls) {

            return new FspiopInvokerConfiguration.TransportSettings(false, null, null, true);
        }

        return new FspiopInvokerConfiguration.TransportSettings(true,
                                                                new FspiopInvokerConfiguration.TransportSettings.KeyStoreSettings(
                                                                    P12Reader.ContentType.valueOf(System.getenv("INVOKER_KEYSTORE_CONTENT_TYPE")),
                                                                    System.getenv("INVOKER_KEYSTORE_CONTENT_VALUE"),
                                                                    System.getenv("INVOKER_KEYSTORE_PASSWORD")),
                                                                new FspiopInvokerConfiguration.TransportSettings.TrustStoreSettings(
                                                                    P12Reader.ContentType.valueOf(System.getenv("INVOKER_TRUSTSTORE_CONTENT_TYPE")),
                                                                    System.getenv("INVOKER_TRUSTSTORE_CONTENT_VALUE"),
                                                                    System.getenv("INVOKER_TRUSTSTORE_PASSWORD")), true);
    }

    @Bean
    @Override
    public ConnectorInboundConfiguration.InboundSettings inboundSettings() {

        var useMutualTls = Boolean.parseBoolean(System.getenv().getOrDefault("FSPIOP_INBOUND_USE_MUTUAL_TLS", "false"));

        if (!useMutualTls) {

            return new ConnectorInboundConfiguration.InboundSettings(Integer.parseInt(System.getenv("FSPIOP_INBOUND_PORT")),
                                                                     Integer.parseInt(System.getenv("FSPIOP_INBOUND_MAX_THREAD")),
                                                                     Integer.parseInt(System.getenv("FSPIOP_INBOUND_CONNECTION_TIMEOUT")), false, null, null);
        }

        return new ConnectorInboundConfiguration.InboundSettings(Integer.parseInt(System.getenv("FSPIOP_INBOUND_PORT")),
                                                                 Integer.parseInt(System.getenv("FSPIOP_INBOUND_MAX_THREAD")),
                                                                 Integer.parseInt(System.getenv("FSPIOP_INBOUND_CONNECTION_TIMEOUT")), true,
                                                                 new ConnectorInboundConfiguration.InboundSettings.KeyStoreSettings(
                                                                     P12Reader.ContentType.valueOf(System.getenv("FSPIOP_INBOUND_KEYSTORE_CONTENT_TYPE")),
                                                                     System.getenv("FSPIOP_INBOUND_KEYSTORE_CONTENT_VALUE"),
                                                                     System.getenv("FSPIOP_INBOUND_KEYSTORE_PASSWORD"), null),
                                                                 new ConnectorInboundConfiguration.InboundSettings.TrustStoreSettings(
                                                                     P12Reader.ContentType.valueOf(System.getenv("FSPIOP_INBOUND_TRUSTSTORE_CONTENT_TYPE")),
                                                                     System.getenv("FSPIOP_INBOUND_TRUSTSTORE_CONTENT_VALUE"),
                                                                     System.getenv("FSPIOP_INBOUND_TRUSTSTORE_PASSWORD")));
    }

    @Bean
    @Override
    public ConnectorOutboundConfiguration.OutboundSettings outboundSettings() {

        return new ConnectorOutboundConfiguration.OutboundSettings(Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PORT")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_MAX_THREAD")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_CONNECTION_TIMEOUT")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PUT_RESULT_TIMEOUT")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PUBSUB_TIMEOUT")),
                                                                   System.getenv("FSPIOP_OUTBOUND_PUBLIC_KEY_PEM"),
                                                                   Boolean.parseBoolean(System.getenv().getOrDefault("FSPIOP_OUTBOUND_SECURED", "true")));
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
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/parties/**", "/quotes/**", "/transfers/**"});
    }

    @Bean
    @Override
    public ConnectorOutboundConfiguration.TransactionSettings transactionSettings() {

        return new ConnectorOutboundConfiguration.TransactionSettings(Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_EXPIRE_AFTER_SECONDS")));
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(TransfersService.Settings.class)
    public TransfersService.Settings transfersServiceSettings() {

        return new TransfersService.Settings(System.getenv("FSPIOP_TRANSFERS_URL"));
    }

}
