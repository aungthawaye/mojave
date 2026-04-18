package org.mojave.core.accounting.intercom.requestor;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        AccountingIntercomRequestorConfiguration.class,
        AccountingIntercomRequestorTestSettings.class})
public class AccountingIntercomRequestorTestConfiguration { }
