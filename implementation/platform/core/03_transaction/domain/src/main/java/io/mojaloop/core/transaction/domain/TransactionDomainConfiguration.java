package io.mojaloop.core.transaction.domain;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.transaction.domain"})
@Import(value = {MiscConfiguration.class, RoutingJpaConfiguration.class})
public class TransactionDomainConfiguration {

    public interface RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings {

    }

}
