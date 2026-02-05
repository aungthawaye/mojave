package org.mojave.core.settlement.domain;

import org.mojave.component.jpa.routing.RoutingJpaConfiguration;
import org.mojave.component.misc.MiscConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"org.mojave.core.settlement.domain"})
@Import(
    value = {
        MiscConfiguration.class,
        RoutingJpaConfiguration.class})
public class SettlementDomainConfiguration {

    public interface RequiredBeans { }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings {

    }

}
