package io.mojaloop.core.transfer;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.core.transfer.domain.component.interledger.PartyUnwrapperRegistry;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.transfer.domain"})
@Import(value = {MiscConfiguration.class, FspiopCommonConfiguration.class, ParticipantStoreConfiguration.class, RoutingJpaConfiguration.class})
public class TransferDomainConfiguration {

    public interface RequiredBeans
        extends MiscConfiguration.RequiredBeans, FspiopCommonConfiguration.RequiredBeans, ParticipantStoreConfiguration.RequiredBeans, RoutingJpaConfiguration.RequiredBeans {

        PartyUnwrapperRegistry unwrapperRegistry();
    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              FspiopCommonConfiguration.RequiredSettings,
                                              ParticipantStoreConfiguration.RequiredSettings,
                                              RoutingJpaConfiguration.RequiredSettings {

        TransferSettings transferSettings();

    }

    public record TransferSettings(int reserveLifetimeMs, int expiryTimeoutMs) { }

}
