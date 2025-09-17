package io.mojaloop.connector.gateway;

import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.connector.gateway.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Import(value = {MiscConfiguration.class, FspiopCommonConfiguration.class})
public class ConnectorGatewayConfiguration {

    public interface RequiredBeans extends ConnectorInboundConfiguration.RequiredBeans, ConnectorOutboundConfiguration.RequiredBeans { }

    public interface RequiredSettings
        extends ConnectorInboundConfiguration.RequiredSettings, ConnectorOutboundConfiguration.RequiredSettings { }

}
