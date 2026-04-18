package org.mojave.core.accounting.domain;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        AccountingDomainConfiguration.class,
        AccountingDomainTestSettings.class,
        AccountingDomainDependencies.class})
public class AccountingDomainTestConfiguration { }
