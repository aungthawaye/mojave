package org.mojave.core.settlement.domain;

import org.mojave.component.jpa.routing.RoutingJpaConfiguration;
import org.mojave.component.misc.MiscConfiguration;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.settlement.domain.component.provider.SettlementProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

@ComponentScan(basePackages = {"org.mojave.core.settlement.domain"})
@Import(
    value = {
        MiscConfiguration.class,
        RoutingJpaConfiguration.class})
public class SettlementDomainConfiguration {

    @Bean
    public SettlementProvider.SettlementService settlementService(ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(SettlementProvider.SettlementService.class,
                       "https://www.anywhere.com")
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    public interface RequiredDependencies { }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings {

    }

}
