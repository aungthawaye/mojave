package org.mojave.wallet.domain;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        WalletDomainConfiguration.class,
        WalletDomainSettings.class,
        WalletDomainDependencies.class})
public class WalletDomainTestConfiguration { }
