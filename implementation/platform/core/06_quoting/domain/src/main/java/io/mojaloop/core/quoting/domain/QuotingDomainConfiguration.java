package io.mojaloop.core.quoting.domain;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.quoting.domain"})
@Import(value = {MiscConfiguration.class, FspiopCommonConfiguration.class, ParticipantStoreConfiguration.class, RoutingJpaConfiguration.class})
public class QuotingDomainConfiguration {

    public interface RequiredBeans
        extends MiscConfiguration.RequiredBeans, FspiopCommonConfiguration.RequiredBeans, ParticipantStoreConfiguration.RequiredBeans, RoutingJpaConfiguration.RequiredBeans {

    }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings,
                FspiopCommonConfiguration.RequiredSettings,
                ParticipantStoreConfiguration.RequiredSettings,
                RoutingJpaConfiguration.RequiredSettings {

        QuoteSettings quoteSettings();

    }

    public record QuoteSettings(boolean stateful) { }

}
