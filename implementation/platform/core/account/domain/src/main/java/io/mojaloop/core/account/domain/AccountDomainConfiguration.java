package io.mojaloop.core.account.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.redis.RedissonOpsClientConfiguration;
import io.mojaloop.core.account.domain.component.ledger.Ledger;
import io.mojaloop.core.account.domain.component.ledger.strategy.MySqlLedger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.account.domain"})
@Import(value = {MiscConfiguration.class, RedissonOpsClientConfiguration.class, RoutingJpaConfiguration.class})
public class AccountDomainConfiguration {

    @Bean
    public Ledger ledgers(MySqlLedger.LedgerDbSettings ledgerDbSettings, ObjectMapper objectMapper) {

        return new MySqlLedger(ledgerDbSettings, objectMapper);
    }

    public interface RequiredBeans { }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings, RedissonOpsClientConfiguration.RequiredSettings {

        MySqlLedger.LedgerDbSettings ledgerDbSettings();

    }

}
