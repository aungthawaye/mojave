package io.mojaloop.connector.service;

import io.mojaloop.connector.service.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.service.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@Import(value = {FspiopInvokerConfiguration.class})
public class ConnectorServiceConfiguration implements FspiopInvokerConfiguration.RequiredBeans {

    public interface RequiredBeans extends ConnectorInboundConfiguration.RequiredBeans,
                                           ConnectorOutboundConfiguration.RequiredBeans {

    }

    public interface RequiredSettings extends ConnectorInboundConfiguration.RequiredSettings,
                                              ConnectorOutboundConfiguration.RequiredSettings,
                                              FspiopInvokerConfiguration.RequiredSettings { }

}
