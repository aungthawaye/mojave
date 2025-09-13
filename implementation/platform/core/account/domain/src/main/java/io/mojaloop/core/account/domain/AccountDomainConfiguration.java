package io.mojaloop.core.account.domain;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.redis.RedissonOpsClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.account.domain"})
@Import(value = {MiscConfiguration.class, RedissonOpsClientConfiguration.class, RoutingJpaConfiguration.class})
public class AccountDomainConfiguration {

    public interface RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              RoutingJpaConfiguration.RequiredSettings,
                                              RedissonOpsClientConfiguration.RequiredSettings { }
}
