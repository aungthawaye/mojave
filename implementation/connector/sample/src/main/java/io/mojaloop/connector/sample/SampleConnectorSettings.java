package io.mojaloop.connector.sample;

import io.mojaloop.connector.gateway.ConnectorGatewayConfiguration;
import io.mojaloop.connector.gateway.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.invoker.api.PartiesService;
import io.mojaloop.fspiop.invoker.api.QuotesService;
import io.mojaloop.fspiop.invoker.api.TransfersService;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

public class SampleConnectorSettings implements ConnectorGatewayConfiguration.RequiredSettings {

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopParticipantSettings() {

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
    public ConnectorInboundConfiguration.InboundSettings inboundSettings() {

        return new ConnectorInboundConfiguration.InboundSettings(Integer.parseInt(System.getenv("FSPIOP_INBOUND_PORT")),
                                                                 Integer.parseInt(System.getenv("FSPIOP_INBOUND_MAX_THREAD")),
                                                                 Integer.parseInt(System.getenv("FSPIOP_INBOUND_CONNECTION_TIMEOUT")));
    }

    @Bean
    @Override
    public ConnectorOutboundConfiguration.OutboundSettings outboundSettings() {

        return new ConnectorOutboundConfiguration.OutboundSettings(Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PORT")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_MAX_THREAD")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_CONNECTION_TIMEOUT")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PUT_RESULT_TIMEOUT")),
                                                                   Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_PUBSUB_TIMEOUT")));
    }

    @Bean
    @Override
    public PartiesService.Settings partiesServiceSettings() {

        return new PartiesService.Settings(System.getenv("FSPIOP_PARTIES_URL"));
    }

    @Bean
    @Override
    public QuotesService.Settings quotesServiceSettings() {

        return new QuotesService.Settings(System.getenv("FSPIOP_QUOTES_URL"));
    }

    @Bean
    @Override
    public ConnectorOutboundConfiguration.TransactionSettings transactionSettings() {

        return new ConnectorOutboundConfiguration.TransactionSettings(
            Integer.parseInt(System.getenv("FSPIOP_OUTBOUND_EXPIRE_AFTER_SECONDS")));
    }

    @Bean
    @Override
    public TransfersService.Settings transfersServiceSettings() {

        return new TransfersService.Settings(System.getenv("FSPIOP_TRANSFERS_URL"));
    }

}

