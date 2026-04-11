package org.mojave.accounting.intercom.requestor;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        AccountingIntercomRequestorConfiguration.class,
        AccountingIntercomRequestorSettings.class})
public class AccountingIntercomRequestorTestConfiguration { }
