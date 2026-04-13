package org.mojave.mono.core.admin;

import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.error.RestErrorConfiguration;
import org.mojave.component.web.logging.RequestIdMdcConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfiguration;
import org.mojave.core.accounting.intercom.requestor.AccountingIntercomRequestorConfiguration;
import org.mojave.core.participant.intercom.requestor.ParticipantIntercomRequestorConfiguration;
import org.mojave.core.wallet.intercom.requestor.WalletIntercomRequestorConfiguration;
import org.mojave.scheme.rule.DatatypeConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ComponentScan(
    basePackages = {
        "org.mojave.mono.core.admin"})
@Import(
    value = {
        OpenApiConfiguration.class,
        DatatypeConfiguration.class,
        RequestIdMdcConfiguration.class,
        RestErrorConfiguration.class,
        SpringSecurityConfiguration.class,
        AccountingIntercomRequestorConfiguration.class,
        ParticipantIntercomRequestorConfiguration.class,
        WalletIntercomRequestorConfiguration.class})
public class MonoAdminConfiguration {

    public interface RequiredDependencies extends OpenApiConfiguration.RequiredDependencies,
                                                  DatatypeConfiguration.RequiredDependencies,
                                                  RequestIdMdcConfiguration.RequiredDependencies,
                                                  RestErrorConfiguration.RequiredDependencies,
                                                  SpringSecurityConfiguration.RequiredDependencies,
                                                  AccountingIntercomRequestorConfiguration.RequiredDependencies,
                                                  ParticipantIntercomRequestorConfiguration.RequiredDependencies,
                                                  WalletIntercomRequestorConfiguration.RequiredDependencies {

    }

    public interface RequiredSettings extends OpenApiConfiguration.RequiredSettings,
                                              DatatypeConfiguration.RequiredSettings,
                                              RequestIdMdcConfiguration.RequiredSettings,
                                              RestErrorConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings,
                                              AccountingIntercomRequestorConfiguration.RequiredSettings,
                                              ParticipantIntercomRequestorConfiguration.RequiredSettings,
                                              WalletIntercomRequestorConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
