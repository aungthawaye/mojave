package org.mojave.accounting.domain;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        AccountingDomainConfiguration.class,
        AccountingDomainSettings.class,
        AccountingDomainDependencies.class})
public class AccountingDomainTestConfiguration { }
