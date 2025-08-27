package io.mojaloop.connector.gateway;

import io.mojaloop.connector.gateway.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.context.annotation.Import;

@Import(value = {FspiopInvokerConfiguration.class})
public class ConnectorGatewayConfiguration implements FspiopInvokerConfiguration.RequiredBeans {

    public interface RequiredBeans extends ConnectorInboundConfiguration.RequiredBeans,
                                           ConnectorOutboundConfiguration.RequiredBeans {

    }

    public interface RequiredSettings extends ConnectorInboundConfiguration.RequiredSettings,
                                              ConnectorOutboundConfiguration.RequiredSettings,
                                              FspiopInvokerConfiguration.RequiredSettings { }

}
