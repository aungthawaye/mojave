package io.mojaloop.core.transaction.domain;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.redis.RedissonOpsClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.transaction.domain"})
@Import(value = {MiscConfiguration.class, RoutingJpaConfiguration.class, RedissonOpsClientConfiguration.class})
public class TransactionDomainConfiguration {

    public interface RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings, RedissonOpsClientConfiguration.RequiredSettings {

    }

}
