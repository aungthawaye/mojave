package org.mojave.core.wallet.intercom.requestor;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        WalletIntercomRequestorConfiguration.class,
        WalletIntercomRequestorSettings.class})
public class WalletIntercomRequestorTestConfiguration { }

